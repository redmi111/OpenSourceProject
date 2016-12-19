package com.music.musicplayer135.injection

import android.app.Application
import android.content.Intent
import android.support.v7.app.AppCompatDelegate
import com.music.musicplayer135.BuildConfig
import com.music.musicplayer135.features.BookAdder
import com.music.musicplayer135.persistence.PrefsManager
import com.music.musicplayer135.playback.PlaybackService
import org.acra.ACRA
import org.acra.annotation.ReportsCrashes
import org.acra.config.ConfigurationBuilder
import org.acra.sender.HttpSender.Method
import org.acra.sender.HttpSender.Type
import timber.log.Timber
import javax.inject.Inject

@ReportsCrashes(
  httpMethod = Method.PUT,
  reportType = Type.JSON,
  buildConfigClass = BuildConfig::class,
  formUri = BuildConfig.ACRA_SERVER,
  formUriBasicAuthLogin = BuildConfig.ACRA_USER,
  formUriBasicAuthPassword = BuildConfig.ACRA_PASSWORD)
class App : Application() {

  @Inject lateinit var bookAdder: BookAdder
  @Inject lateinit var prefsManager: PrefsManager

  override fun onCreate() {
    super.onCreate()

    // init acra + return early if this is the sender service
    if (!BuildConfig.DEBUG) {
      val isSenderProcess = ACRA.isACRASenderServiceProcess()
      //if (isSenderProcess || Random().nextInt(5) == 0) {
      val config = ConfigurationBuilder(this)
        .build()
      ACRA.init(this, config)
      if (isSenderProcess) return
    }

    applicationComponent = newComponent()
    component().inject(this)

    if (BuildConfig.DEBUG) {
      // init timber
      Timber.plant(Timber.DebugTree())
    }

    bookAdder.scanForFiles(true)
    startService(Intent(this, PlaybackService::class.java))

    AppCompatDelegate.setDefaultNightMode(prefsManager.theme.get()!!.nightMode)
  }

  private fun newComponent(): ApplicationComponent {
    return DaggerApplicationComponent.builder()
      .androidModule(AndroidModule(this))
      .build()
  }

  companion object {

    private var applicationComponent: ApplicationComponent? = null

    fun component(): ApplicationComponent {
      return applicationComponent!!
    }
  }
}