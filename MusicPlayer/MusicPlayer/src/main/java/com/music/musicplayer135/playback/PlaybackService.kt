package com.music.musicplayer135.playback

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import d
import com.music.musicplayer135.features.BookActivity
import com.music.musicplayer135.features.book_overview.BookShelfController
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.RxBroadcast
import com.music.musicplayer135.misc.asV2Observable
import com.music.musicplayer135.misc.value
import com.music.musicplayer135.persistence.BookRepository
import com.music.musicplayer135.persistence.PrefsManager
import com.music.musicplayer135.playback.PlayStateManager.PauseReason
import com.music.musicplayer135.playback.PlayStateManager.PlayState
import com.music.musicplayer135.playback.events.*
import com.music.musicplayer135.playback.utils.BookUriConverter
import com.music.musicplayer135.playback.utils.ChangeNotifier
import com.music.musicplayer135.playback.utils.MediaBrowserHelper
import com.music.musicplayer135.playback.utils.NotificationAnnouncer
import e
import i
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import v
import java.io.File
import javax.inject.Inject

/**
 * Service that hosts the longtime playback and handles its controls.
 *
 * @author Paul Woitaschek
 */
class PlaybackService : MediaBrowserServiceCompat() {

  override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) = mediaBrowserHelper.onLoadChildren(parentId, result)

  override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot = mediaBrowserHelper.onGetRoot()

  init {
    App.component().inject(this)
  }

  private val disposables = CompositeDisposable()
  @Inject lateinit var prefs: PrefsManager
  @Inject lateinit var player: MediaPlayer
  @Inject lateinit var repo: BookRepository
  @Inject lateinit var notificationManager: NotificationManager
  @Inject lateinit var audioManager: AudioManager
  @Inject lateinit var audioFocusReceiver: AudioFocusReceiver
  @Inject lateinit var notificationAnnouncer: NotificationAnnouncer
  @Inject lateinit var playStateManager: PlayStateManager
  @Inject lateinit var audioFocusManager: AudioFocusManager
  @Inject lateinit var bookUriConverter: BookUriConverter
  @Inject lateinit var mediaBrowserHelper: MediaBrowserHelper
  private lateinit var mediaSession: MediaSessionCompat
  private lateinit var changeNotifier: ChangeNotifier


  override fun onCreate() {
    super.onCreate()

    val eventReceiver = ComponentName(packageName, MediaEventReceiver::class.java.name)
    val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
      component = eventReceiver
    }
    val buttonReceiverIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    mediaSession = MediaSessionCompat(this, TAG, eventReceiver, buttonReceiverIntent).apply {

      setCallback(object : MediaSessionCompat.Callback() {

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
          i { "onPlayFromMediaId $mediaId" }
          val uri = Uri.parse(mediaId)
          val type = bookUriConverter.match(uri)
          if (type == BookUriConverter.BOOK_ID) {
            val id = bookUriConverter.extractBook(uri)
            prefs.currentBookId.set(id)
            onPlay()
          } else {
            e { "Invalid mediaId $mediaId" }
          }
        }

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
          i { "onPlayFromSearch $query" }
          player.play()
        }

        override fun onSkipToNext() {
          i { "onSkipToNext" }
          onFastForward()
        }

        override fun onRewind() {
          i { "onRewind" }
          player.skip(MediaPlayer.Direction.BACKWARD)
        }

        override fun onSkipToPrevious() {
          i { "onSkipToPrevious" }
          onRewind()
        }

        override fun onFastForward() {
          i { "onFastForward" }
          player.skip(MediaPlayer.Direction.FORWARD)
        }

        override fun onStop() {
          i { "onStop" }
          player.stop()
        }

        override fun onPause() {
          i { "onPause" }
          player.pause(true)
        }

        override fun onPlay() {
          i { "onPlay" }
          player.play()
        }
      })
      setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
    }
    sessionToken = mediaSession.sessionToken
    changeNotifier = ChangeNotifier(mediaSession)

    player.onError()
      .subscribe {
        // inform user on errors
        e { "onError" }
        val book = player.book()
        if (book != null) {
          startActivity(BookActivity.malformedFileIntent(this, book.currentFile))
        } else {
          val intent = Intent(this, BookShelfController::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
          }
          startActivity(intent)
        }
      }

    // update book when changed by player
    player.bookObservable()
      .filter { it != null }
      .subscribe { repo.updateBook(it) }

    playStateManager.playState.onNext(PlayState.STOPPED)

    disposables.apply {
      // set seek time to the player
      add(prefs.seekTime.asV2Observable()
        .subscribe { player.seekTime = it })

      // set auto rewind amount to the player
      add(prefs.autoRewindAmount.asV2Observable()
        .subscribe { player.autoRewindAmount = it })

      // re-init controller when there is a new book set as the current book
      add(prefs.currentBookId.asV2Observable()
        .subscribe {
          if (player.book()?.id != it) {
            player.stop()

            val newBook = repo.bookById(it)
            if (newBook != null) player.init(newBook)
          }
        })

      // notify player about changes in the current book
      add(repo.updateObservable()
        .filter { it.id == prefs.currentBookId.value() }
        .subscribe {
          player.init(it)
          changeNotifier.notify(ChangeNotifier.Type.METADATA, it)
        })

      var currentlyHasFocus = false
      add(audioFocusReceiver.focusObservable()
        .map { it == AudioFocus.GAIN }
        .subscribe { currentlyHasFocus = it })

      // handle changes on the play state
      add(playStateManager.playState
        .observeOn(Schedulers.io())
        .subscribe {
          d { "onPlayStateManager.PlayStateChanged:$it" }
          val controllerBook = player.book()
          if (controllerBook != null) {
            when (it!!) {
              PlayState.PLAYING -> {
                if (!currentlyHasFocus) {
                  audioManager.requestAudioFocus(audioFocusReceiver.audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                }

                mediaSession.isActive = true
                d { "set mediaSession to active" }
                val notification = notificationAnnouncer.getNotification(controllerBook, it, mediaSession.sessionToken)
                startForeground(NOTIFICATION_ID, notification)
              }
              PlayState.PAUSED -> {
                stopForeground(false)
                val notification = notificationAnnouncer.getNotification(controllerBook, it, mediaSession.sessionToken)
                notificationManager.notify(NOTIFICATION_ID, notification)
              }
              PlayState.STOPPED -> {
                mediaSession.isActive = false
                d { "Set mediaSession to inactive" }

                audioManager.abandonAudioFocus(audioFocusReceiver.audioFocusListener)
                notificationManager.cancel(NOTIFICATION_ID)
                stopForeground(true)
              }
            }

            changeNotifier.notify(ChangeNotifier.Type.PLAY_STATE, controllerBook)
          }
        })

      // resume playback when headset is reconnected. (if settings are set)
      add(HeadsetPlugReceiver.events(this@PlaybackService)
        .subscribe { headsetState ->
          if (headsetState == HeadsetPlugReceiver.HeadsetState.PLUGGED) {
            if (playStateManager.pauseReason == PauseReason.BECAUSE_HEADSET) {
              if (prefs.resumeOnReplug.value()) {
                player.play()
              }
            }
          }
        })

      // adjusts stream and playback based on music focus.
      add(audioFocusManager.handleAudioFocus(audioFocusReceiver.focusObservable()))

      // notifies the media service about added or removed books
      add(repo.booksStream().map { it.size }.distinctUntilChanged()
        .subscribe {
          v { "notify media browser service about children changed." }
          notifyChildrenChanged(bookUriConverter.allBooks().toString())
        })

      // pause when music is becoming noisy.
      add(RxBroadcast.register(this@PlaybackService, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        .subscribe {
          d { "music becoming noisy. Playstate=${playStateManager.playState.value}" }
          if (playStateManager.playState.value === PlayState.PLAYING) {
            playStateManager.pauseReason = PauseReason.BECAUSE_HEADSET
            player.pause(true)
          }
        })
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    v { "onStartCommand, intent=$intent, flags=$flags, startId=$startId" }

    when (intent?.action) {
      Intent.ACTION_MEDIA_BUTTON -> MediaButtonReceiver.handleIntent(mediaSession, intent)
      PlayerController.ACTION_SPEED -> player.setPlaybackSpeed(intent!!.getFloatExtra(PlayerController.EXTRA_SPEED, 1F))
      PlayerController.ACTION_CHANGE -> {
        val time = intent!!.getIntExtra(PlayerController.CHANGE_TIME, 0)
        val file = File(intent.getStringExtra(PlayerController.CHANGE_FILE))
        player.changePosition(time, file)
      }
      PlayerController.ACTION_PAUSE_NON_REWINDING -> player.pause(rewind = false)
      PlayerController.ACTION_FORCE_NEXT -> player.next()
      PlayerController.ACTION_FORCE_PREVIOUS -> player.previous(toNullOfNewTrack = true)
      PlayerController.ACTION_VOLUME_HIGH -> player.setVolume(loud = true)
      PlayerController.ACTION_VOLUME_LOW -> player.setVolume(loud = false)
    }

    return Service.START_STICKY
  }

  override fun onDestroy() {
    v { "onDestroy called" }
    player.stop()

    mediaSession.release()
    disposables.dispose()

    super.onDestroy()
  }

  companion object {
    private val TAG = PlaybackService::class.java.simpleName
    private val NOTIFICATION_ID = 42
  }
}