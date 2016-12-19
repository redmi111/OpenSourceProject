package com.music.musicplayer135.features.folder_overview

import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.asV2Observable
import com.music.musicplayer135.mvp.Presenter
import com.music.musicplayer135.persistence.PrefsManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import java.util.*

import javax.inject.Inject

/**
 * The presenter for [FolderOverviewController]
 *
 * @author Paul Woitaschek
 */
class FolderOverviewPresenter : Presenter<FolderOverviewController>() {

  init {
    App.component().inject(this)
  }

  @Inject lateinit var prefsManager: PrefsManager

  override fun onBind(view: FolderOverviewController, disposables: CompositeDisposable) {

    val collectionFolderStream = prefsManager.collectionFolders.asV2Observable()
      .map { it.map { FolderModel(it, true) } }
    val singleFolderStream = prefsManager.singleBookFolders.asV2Observable()
      .map { it.map { FolderModel(it, false) } }

    val combined = Observable.combineLatest(collectionFolderStream, singleFolderStream, BiFunction<List<FolderModel>, List<FolderModel>, List<FolderModel>> { t1, t2 -> t1 + t2 })
    disposables.add(combined
      .subscribe { view.newData(it) })
  }


  /** removes a selected folder **/
  fun removeFolder(folder: FolderModel) {
    prefsManager.collectionFolders.asObservable()
      .map { HashSet(it) }
      .first()
      .subscribe {
        val removed = it.remove(folder.folder)
        if (removed) prefsManager.collectionFolders.set(it)
      }

    prefsManager.singleBookFolders.asObservable()
      .map { HashSet(it) }
      .first()
      .subscribe {
        val removed = it.remove(folder.folder)
        if (removed) prefsManager.singleBookFolders.set(it)
      }
  }
}