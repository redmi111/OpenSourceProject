package com.music.musicplayer135.mvp

import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.music.musicplayer135.features.BaseController

/**
 * Base fragment that provides a convenient way for binding a view to a presenter
 *
 * @author Paul Woitaschek
 */
abstract class MvpBaseController<V, out P> : BaseController() where P : Presenter<V> {

  init {
    addLifecycleListener(object : LifecycleListener() {
      override fun onRestoreInstanceState(controller: Controller, savedInstanceState: Bundle) {
        presenter.onRestore(savedInstanceState)
      }

      override fun postAttach(controller: Controller, view: View) {
        presenter.bind(provideView())
      }

      override fun postDetach(controller: Controller, view: View) {
        presenter.unbind()
      }

      override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
        presenter.onSave(outState)
      }
    })
  }

  abstract fun provideView(): V

  abstract val presenter: P
}