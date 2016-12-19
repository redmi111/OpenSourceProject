package com.music.musicplayer135.injection

import android.content.Context
import dagger.Component
import com.music.musicplayer135.features.BaseActivity
import com.music.musicplayer135.features.BookActivity
import com.music.musicplayer135.features.book_overview.*
import com.music.musicplayer135.features.book_playing.BookPlayController
import com.music.musicplayer135.features.book_playing.JumpToPositionDialogFragment
import com.music.musicplayer135.features.book_playing.SeekDialogFragment
import com.music.musicplayer135.features.book_playing.SleepTimerDialogFragment
import com.music.musicplayer135.features.bookmarks.BookmarkDialogFragment
import com.music.musicplayer135.features.folder_chooser.FolderChooserActivity
import com.music.musicplayer135.features.folder_chooser.FolderChooserPresenter
import com.music.musicplayer135.features.folder_overview.FolderOverviewPresenter
import com.music.musicplayer135.features.imagepicker.ImagePickerController
import com.music.musicplayer135.features.settings.SettingsController
import com.music.musicplayer135.features.settings.dialogs.AutoRewindDialogFragment
import com.music.musicplayer135.features.settings.dialogs.PlaybackSpeedDialogFragment
import com.music.musicplayer135.features.settings.dialogs.ThemePickerDialogFragment
import com.music.musicplayer135.features.widget.WidgetUpdateService
import com.music.musicplayer135.playback.PlaybackService
import com.music.musicplayer135.playback.utils.ChangeNotifier
import com.music.musicplayer135.uitools.CoverReplacement
import javax.inject.Singleton

/**
 * Base component that is the entry point for injection.
 *
 * @author Paul Woitaschek
 */
@Singleton
@Component(modules = arrayOf(BaseModule::class, AndroidModule::class, PrefsModule::class))
interface ApplicationComponent {

  val bookShelfPresenter: BookShelfPresenter
  val context: Context

  fun inject(target: App)
  fun inject(target: AutoRewindDialogFragment)
  fun inject(target: BaseActivity)
  fun inject(target: PlaybackService)
  fun inject(target: BookActivity)
  fun inject(target: BookShelfAdapter)
  fun inject(target: BookmarkDialogFragment)
  fun inject(target: BookPlayController)
  fun inject(target: BookShelfController)
  fun inject(target: ChangeNotifier)
  fun inject(target: CoverReplacement)
  fun inject(target: SettingsController)
  fun inject(target: EditBookTitleDialogFragment)
  fun inject(target: EditBookBottomSheet)
  fun inject(target: EditCoverDialogFragment)
  fun inject(target: FolderChooserActivity)
  fun inject(target: FolderChooserPresenter)
  fun inject(target: FolderOverviewPresenter)
  fun inject(target: ImagePickerController)
  fun inject(target: JumpToPositionDialogFragment)
  fun inject(target: PlaybackSpeedDialogFragment)
  fun inject(target: SeekDialogFragment)
  fun inject(target: SleepTimerDialogFragment)
  fun inject(target: ThemePickerDialogFragment)
  fun inject(target: WidgetUpdateService)
}