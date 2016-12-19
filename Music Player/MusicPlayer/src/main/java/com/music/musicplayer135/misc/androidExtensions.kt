package com.music.musicplayer135.misc

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.bluelinelabs.conductor.Controller
import com.f2prateek.rx.preferences.Preference
import java.io.File
import java.io.FileFilter

fun Context.layoutInflater(): LayoutInflater = LayoutInflater.from(this)

fun Context.drawable(@DrawableRes id: Int): Drawable = ContextCompat.getDrawable(this, id)

@ColorInt fun Context.color(@ColorRes id: Int): Int {
  return ContextCompat.getColor(this, id)
}

var View.supportTransitionName: String?
  get() = ViewCompat.getTransitionName(this)
  set(value) = ViewCompat.setTransitionName(this, value)

fun View.layoutInflater() = context.layoutInflater()

fun MaterialDialog.Builder.positiveClicked(listener: () -> Unit): MaterialDialog.Builder {
  onPositive { dialog, which -> listener() }
  return this
}

/** same as get() but force cast to non null **/
fun <T> Preference<T>.value() = get()!!

fun Context.dpToPx(dp: Int) = Math.round(TypedValue.applyDimension(
  TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics))

fun Drawable.tinted(@ColorInt color: Int): Drawable {
  val wrapped = DrawableCompat.wrap(this)
  DrawableCompat.setTint(wrapped, color)
  return wrapped
}

fun Controller.setupActionbar(toolbar: Toolbar,
                              @DrawableRes upIndicator: Int? = null,
                              title: String? = null) =
  (activity as AppCompatActivity).setupActionbar(
    toolbar = toolbar,
    upIndicator = upIndicator,
    title = title)

fun AppCompatActivity.setupActionbar(toolbar: Toolbar,
                                     @DrawableRes upIndicator: Int? = null,
                                     title: String? = null) {
  setSupportActionBar(toolbar)
  val actionBar = supportActionBar!!

  if (upIndicator != null) actionBar.setHomeAsUpIndicator(upIndicator)
  actionBar.setDisplayHomeAsUpEnabled(upIndicator != null)

  if (title != null) actionBar.title = title
  actionBar.setDisplayShowTitleEnabled(title != null)
}

/**
 * As there are cases where [File.listFiles] returns null even though it is a directory, we return
 * an empty list instead.
 */
fun File.listFilesSafely(filter: FileFilter? = null): List<File> {
  val array: Array<File>? = if (filter == null) listFiles() else listFiles(filter)
  return array?.toList() ?: emptyList()
}