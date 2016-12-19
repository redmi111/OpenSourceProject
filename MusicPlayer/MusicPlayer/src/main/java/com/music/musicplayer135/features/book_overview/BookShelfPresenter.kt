package com.music.musicplayer135.features.book_overview

import com.music.musicplayer135.features.BookAdder
import com.music.musicplayer135.misc.asV2Observable
import com.music.musicplayer135.misc.value
import com.music.musicplayer135.mvp.Presenter
import com.music.musicplayer135.persistence.BookRepository
import com.music.musicplayer135.persistence.PrefsManager
import com.music.musicplayer135.playback.PlayStateManager
import com.music.musicplayer135.playback.PlayStateManager.PlayState
import com.music.musicplayer135.playback.PlayerController
import i
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * Presenter for [BookShelfController].
 *
 * @author Paul Woitaschek
 */
class BookShelfPresenter
@Inject
constructor(private val repo: BookRepository,
            private val bookAdder: BookAdder,
            private val prefsManager: PrefsManager,
            private val playStateManager: PlayStateManager,
            private val playerController: PlayerController)
  : Presenter<BookShelfController>() {

  override fun onBind(view: BookShelfController, disposables: CompositeDisposable) {
    i { "onBind Called for $view" }

    val audioFoldersEmpty = prefsManager.collectionFolders.value().isEmpty() && prefsManager.singleBookFolders.value().isEmpty()
    if (audioFoldersEmpty) view.showNoFolderWarning()

    // scan for files
    bookAdder.scanForFiles(false)

    disposables.apply {

      // update books when they changed
      add(repo.booksStream().subscribe {
        view.newBooks(it)
      })

      // Subscription that notifies the adapter when the current book has changed. It also notifies
      // the item with the old indicator now falsely showing.
      add(prefsManager.currentBookId.asV2Observable()
        .subscribe {
          val book = repo.bookById(it)
          view.currentBookChanged(book)
        })

      // if there are no books and the scanner is active, show loading
      add(Observable.combineLatest(bookAdder.scannerActive, repo.booksStream().map { it.isEmpty() }, BiFunction<Boolean, Boolean, Boolean> { active, booksEmpty ->
        if (booksEmpty) active else false
      }).subscribe { view.showLoading(it) })

      // Subscription that updates the UI based on the play state.
      add(playStateManager.playState
        .subscribe {
          val playing = it == PlayState.PLAYING
          view.setPlayerPlaying(playing)
        })
    }
  }

  fun playPauseRequested() = playerController.playPause()
}