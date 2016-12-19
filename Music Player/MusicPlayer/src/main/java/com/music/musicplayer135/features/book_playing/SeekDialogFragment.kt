package com.music.musicplayer135.features.book_playing

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.SeekBar
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.music.musicplayer135.R
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.find
import com.music.musicplayer135.misc.layoutInflater
import com.music.musicplayer135.misc.onProgressChanged
import com.music.musicplayer135.misc.value
import com.music.musicplayer135.persistence.PrefsManager
import javax.inject.Inject

class SeekDialogFragment : DialogFragment() {

  @Inject lateinit var prefs: PrefsManager

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    App.component().inject(this)

    // find views
    val view = context.layoutInflater().inflate(R.layout.dialog_amount_chooser, null)
    val seekBar: SeekBar = view.find(R.id.seekBar)
    val textView: TextView = view.find(R.id.textView)

    // init
    val oldSeekTime = prefs.seekTime.value()
    seekBar.max = (MAX - MIN) * FACTOR
    seekBar.onProgressChanged(initialNotification = true) {
      val value = it / FACTOR + MIN
      textView.text = context.resources.getQuantityString(R.plurals.seconds, value, value)
    }
    seekBar.progress = (oldSeekTime - MIN) * FACTOR

    return MaterialDialog.Builder(context)
      .title(R.string.pref_seek_time)
      .customView(view, true)
      .positiveText(R.string.dialog_confirm)
      .negativeText(R.string.dialog_cancel)
      .onPositive { materialDialog, dialogAction ->
        val newSeekTime = seekBar.progress / FACTOR + MIN
        prefs.seekTime.set(newSeekTime)
      }.build()
  }

  companion object {
    val TAG: String = SeekDialogFragment::class.java.simpleName

    private val FACTOR = 10
    private val MIN = 3
    private val MAX = 60
  }
}
