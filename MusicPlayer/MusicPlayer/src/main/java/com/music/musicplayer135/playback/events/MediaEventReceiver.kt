package com.music.musicplayer135.playback.events

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.music.musicplayer135.playback.PlaybackService

/**
 * Forwards intents to [PlaybackService]
 *
 * @author Paul Woitaschek
 */
class MediaEventReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (context != null && intent != null) {
      context.startService(Intent(intent).apply {
        component = ComponentName(context, PlaybackService::class.java)
      })
    }
  }
}