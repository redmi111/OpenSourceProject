package com.music.musicplayer135.features.book_playing

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.NumberPicker
import com.afollestad.materialdialogs.MaterialDialog
import com.music.musicplayer135.R
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.layoutInflater
import com.music.musicplayer135.misc.value
import com.music.musicplayer135.persistence.BookRepository
import com.music.musicplayer135.persistence.PrefsManager
import com.music.musicplayer135.playback.PlayerController
import com.music.musicplayer135.uitools.theme
import com.music.musicplayer135.uitools.visible
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class JumpToPositionDialogFragment : DialogFragment() {

  @Inject lateinit var prefs: PrefsManager
  @Inject lateinit var repo: BookRepository
  @Inject lateinit var playerController: PlayerController

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    App.component().inject(this)
    val view = context.layoutInflater().inflate(R.layout.dialog_time_picker, null)
    val colon = view.findViewById(R.id.colon)
    val numberHour = view.findViewById(R.id.numberHour) as NumberPicker
    val numberMinute = view.findViewById(R.id.numberMinute) as NumberPicker

    // init
    val book = repo.bookById(prefs.currentBookId.value())!!
    val duration = book.currentChapter().duration
    val position = book.time
    val biggestHour = TimeUnit.MILLISECONDS.toHours(duration.toLong()).toInt()
    val durationInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong()).toInt()
    if (biggestHour == 0) {
      //sets visibility of hour related things to gone if max.hour is zero
      colon.visible = false
      numberHour.visible = false
    }

    //set maximum values
    numberHour.maxValue = biggestHour
    if (biggestHour == 0) {
      numberMinute.maxValue = TimeUnit.MILLISECONDS.toMinutes(duration.toLong()).toInt()
    } else {
      numberMinute.maxValue = 59
    }

    //set default values
    val defaultHour = TimeUnit.MILLISECONDS.toHours(position.toLong()).toInt()
    val defaultMinute = TimeUnit.MILLISECONDS.toMinutes(position.toLong()).toInt() % 60
    numberHour.value = defaultHour
    numberMinute.value = defaultMinute

    numberHour.setOnValueChangedListener { picker, oldVal, newVal ->
      if (newVal == biggestHour) {
        numberMinute.maxValue = (durationInMinutes - newVal * 60) % 60
      } else {
        numberMinute.maxValue = 59
      }
    }

    numberMinute.setOnValueChangedListener { picker, oldVal, newVal ->
      var hValue = numberHour.value

      //scrolling forward
      if (oldVal == 59 && newVal == 0) {
        numberHour.value = ++hValue
      }
      //scrolling backward
      if (oldVal == 0 && newVal == 59) {
        numberHour.value = --hValue
      }
    }

    numberMinute.theme()
    numberHour.theme()

    return MaterialDialog.Builder(context)
      .customView(view, true)
      .title(R.string.action_time_change)
      .onPositive { materialDialog, dialogAction ->
        val h = numberHour.value
        val m = numberMinute.value
        val newPosition = (m + 60 * h) * 60 * 1000
        playerController.changePosition(newPosition, book.currentChapter().file)
      }
      .positiveText(R.string.dialog_confirm)
      .negativeText(R.string.dialog_cancel)
      .build()
  }

  companion object {

    val TAG: String = JumpToPositionDialogFragment::class.java.simpleName
  }
}
