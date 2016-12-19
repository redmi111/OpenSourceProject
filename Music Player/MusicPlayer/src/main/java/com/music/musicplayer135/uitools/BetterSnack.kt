package com.music.musicplayer135.uitools

import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView
import com.music.musicplayer135.R
import com.music.musicplayer135.misc.color
import com.music.musicplayer135.misc.find

/**
 * Creates [Snackbar]s with convenient values set + theming
 *
 * @author Paul Woitaschek
 */
object BetterSnack {

  /**
   * @param root the root for the snackbar
   * @param text the text to be displayed
   * @param duration the duration of the snacbkar
   * @param action the text that should be set as action
   * @param listener the listener that should be invoked when an action was made
   */
  fun make(root: View,
           text: String,
           duration: Duration = Duration.INDEFINITE_NO_DISMISS,
           action: String? = null,
           listener: (() -> Unit)? = null) {
    val bar = Snackbar.make(root, text, duration.internalDuration)
    bar.setCallback(object : Snackbar.Callback() {
      override fun onDismissed(snackbar: Snackbar?, event: Int) {
        if (event == Snackbar.Callback.DISMISS_EVENT_SWIPE && duration == Duration.INDEFINITE_NO_DISMISS) {
          // show again to enforce a decision
          make(root, text, duration, action, listener)
        }
      }
    })

    // set action if set
    if (action != null && listener != null) {
      bar.setAction(action) {
        listener()
      }
    }

    // theme background
    bar.view.setBackgroundColor(root.context.color(R.color.snackbar_background_color))

    // theme text color
    val textView: TextView = bar.view.find(android.support.design.R.id.snackbar_text)
    textView.setTextColor(root.context.color(R.color.snackbar_text_color))

    bar.show()
  }

  enum class Duration(val internalDuration: Int) {
    INDEFINITE_NO_DISMISS(Snackbar.LENGTH_INDEFINITE)
  }
}