package com.music.musicplayer135.misc

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.music.musicplayer135.R
import com.music.musicplayer135.uitools.ThemeUtil
import java.util.*

/**
 * Adapter fror [Spinner] that highlights the current selection and shows multiple lines of text.
 *
 * @author Paul Woitaschek
 */
class MultiLineSpinnerAdapter<Type>(private val spinner: Spinner, private val context: Context, @ColorInt private val unselectedTextColor: Int) : BaseAdapter(), SpinnerAdapter {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
    // no need for view holder pattern, we can just reuse the view as its a single TextView
    val textView =
      if (convertView == null) {
        context.layoutInflater().inflate(R.layout.book_play_spinner, parent, false) as TextView
      } else {
        convertView as TextView
      }

    val selected = position == spinner.selectedItemPosition
    textView.text = getItem(position).shown

    if (parent == spinner) {
      textView.setBackgroundResource(0)
      textView.setTextColor(unselectedTextColor)
    } else if (selected) {
      textView.setBackgroundResource(R.drawable.spinner_selected_background)
      textView.setTextColor(Color.WHITE)
    } else {
      textView.setBackgroundResource(ThemeUtil.getResourceId(context, android.R.attr.windowBackground))
      textView.setTextColor(context.color(ThemeUtil.getResourceId(context, android.R.attr.textColorPrimary)))
    }

    return textView
  }

  override fun getItem(position: Int) = data[position]

  override fun getItemId(position: Int) = position.toLong()

  override fun getCount() = data.size

  private val data = ArrayList<Data<Type>>()

  fun setData(data: List<Data<Type>>) {
    if (this.data != data) {
      this.data.clear()
      this.data.addAll(data)
      notifyDataSetChanged()
    }
  }

  data class Data<out E>(val data: E, val shown: String)
}