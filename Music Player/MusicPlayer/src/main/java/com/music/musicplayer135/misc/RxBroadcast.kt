package com.music.musicplayer135.misc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Observable

/**
 * Wraps a broadcast receiver in an observable that registers and unregisters based on the subscription.
 *
 * @author Paul Woitaschek
 */
object RxBroadcast {
  fun register(c: Context, filter: IntentFilter): Observable<Intent> = Observable.create {
    val receiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent) {
        it.onNext(intent)
      }
    }

    // register upon subscription, unregister upon unsubscription
    c.registerReceiver(receiver, filter)
    it.setCancellable {
      c.unregisterReceiver(receiver)
    }
  }
}