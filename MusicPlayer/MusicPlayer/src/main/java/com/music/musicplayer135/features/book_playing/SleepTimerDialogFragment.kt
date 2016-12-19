package com.music.musicplayer135.features.book_playing

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.SwitchCompat
import android.text.format.DateUtils
import android.widget.SeekBar
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.music.musicplayer135.Book
import com.music.musicplayer135.R
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.layoutInflater
import com.music.musicplayer135.misc.onProgressChanged
import com.music.musicplayer135.misc.positiveClicked
import com.music.musicplayer135.misc.value
import com.music.musicplayer135.persistence.BookRepository
import com.music.musicplayer135.persistence.BookmarkProvider
import com.music.musicplayer135.persistence.PrefsManager
import com.music.musicplayer135.playback.Sandman
import com.music.musicplayer135.playback.ShakeDetector
import com.music.musicplayer135.uitools.visible
import e
import javax.inject.Inject

/**
 * Simple dialog for activating the sleep timer
 *
 * @author Paul Woitaschek
 */
class SleepTimerDialogFragment : DialogFragment() {

  @Inject lateinit var bookmarkProvider: BookmarkProvider
  @Inject lateinit var prefs: PrefsManager
  @Inject lateinit var sandMan: Sandman
  @Inject lateinit var repo: BookRepository
  @Inject lateinit var shakeDetector: ShakeDetector

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    App.component().inject(this)

    @SuppressWarnings("InflateParams")
    val layout = context.layoutInflater().inflate(R.layout.dialog_sleep, null)
    val seekBar = layout.findViewById(R.id.seekBar) as SeekBar
    val textView = layout.findViewById(R.id.text) as TextView
    val bookmarkSwitch = layout.findViewById(R.id.bookmarkSwitch) as SwitchCompat
    val shakeToResetSwitch = layout.findViewById(R.id.shakeToResetSwitch) as SwitchCompat

    val bookId = arguments.getLong(NI_BOOK_ID)
    val book = repo.bookById(bookId)
    if (book == null) {
      e { "no book" }
      return super.onCreateDialog(savedInstanceState)
    }

    // setup seekBar
    val min = 5
    val max = 120
    seekBar.max = (max - min) * SEEK_FACTOR
    seekBar.progress = (prefs.sleepTime.value() - min) * SEEK_FACTOR
    seekBar.onProgressChanged(initialNotification = true) {
      val corrected = it / SEEK_FACTOR + min
      val text = resources.getQuantityString(R.plurals.pauses_after, corrected, corrected)
      textView.text = text
    }

    // setup bookmark toggle
    bookmarkSwitch.isChecked = prefs.bookmarkOnSleepTimer.value()

    // setup shake to reset setting
    shakeToResetSwitch.isChecked = prefs.shakeToReset.value()
    val shakeSupported = shakeDetector.shakeSupported()
    if (!shakeSupported) {
      shakeToResetSwitch.visible = false
    }

    return MaterialDialog.Builder(context)
      .title(R.string.action_sleep)
      .positiveText(R.string.dialog_confirm)
      .negativeText(R.string.dialog_cancel)
      .customView(layout, true)
      .positiveClicked {
        val corrected = seekBar.progress / SEEK_FACTOR + min
        prefs.sleepTime.set(corrected)

        prefs.bookmarkOnSleepTimer.set(bookmarkSwitch.isChecked)
        if (prefs.bookmarkOnSleepTimer.value()) {
          val date = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_NUMERIC_DATE)
          bookmarkProvider.addBookmarkAtBookPosition(book, date + ": " + getString(R.string.action_sleep))
        }

        prefs.shakeToReset.set(shakeToResetSwitch.isChecked)

        sandMan.setActive(true)
      }
      .build()
  }

  companion object {
    private val NI_BOOK_ID = "BOOK_ID"
    private val SEEK_FACTOR = 10
    fun newInstance(book: Book) = SleepTimerDialogFragment().apply {
      arguments = Bundle().apply {
        putLong(NI_BOOK_ID, book.id)
      }
    }
  }
}