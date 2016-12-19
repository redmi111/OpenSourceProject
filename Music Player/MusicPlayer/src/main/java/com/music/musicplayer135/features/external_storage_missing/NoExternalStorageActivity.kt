package com.music.musicplayer135.features.external_storage_missing

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.music.musicplayer135.R
import com.music.musicplayer135.features.BaseActivity
import com.music.musicplayer135.misc.find
import com.music.musicplayer135.misc.setupActionbar

class NoExternalStorageActivity : AppCompatActivity() {


  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_no_external)

    setupActionbar(toolbar = find(R.id.toolbarInclude),
      title = getString(R.string.no_external_storage_action_bar_title))
  }

  override fun onBackPressed() {
    if (BaseActivity.storageMounted()) {
      super.onBackPressed()
    } else {
      val i = Intent(Intent.ACTION_MAIN)
      i.addCategory(Intent.CATEGORY_HOME)
      i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
      startActivity(i)
    }
  }

  public override fun onResume() {
    super.onResume()
    if (BaseActivity.storageMounted()) {
      onBackPressed()
    }
  }
}
