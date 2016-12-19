package com.music.musicplayer135.features.coldStart

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.music.musicplayer135.features.BookActivity

/**
 * Activity that just exists to fake a toolbar through its windowbackground upon start
 *
 * @author Paul Woitaschek
 */
class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val intent = Intent(this, BookActivity::class.java)
    startActivity(intent)
    finish()
  }
}