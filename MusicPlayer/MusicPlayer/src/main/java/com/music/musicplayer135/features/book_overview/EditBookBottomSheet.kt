package com.music.musicplayer135.features.book_overview

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.widget.TextView
import com.music.musicplayer135.Book
import com.music.musicplayer135.R
import com.music.musicplayer135.features.bookmarks.BookmarkDialogFragment
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.*
import com.music.musicplayer135.persistence.BookRepository
import e
import javax.inject.Inject

/**
 * Bottom sheet dialog fragment that will be displayed when a book edit was requested
 *
 * @author Paul Woitaschek
 */
class EditBookBottomSheet : BottomSheetDialogFragment() {

  @Inject lateinit var repo: BookRepository

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    App.component().inject(this)

    val dialog = BottomSheetDialog(context, R.style.BottomSheetStyle)

    // if there is no book, skip here
    val book = repo.bookById(bookId())
    if (book == null) {
      e { "book is null. Return early" }
      return dialog
    }

    @SuppressWarnings("InflateParams")
    val view = context.layoutInflater().inflate(R.layout.book_more_bottom_sheet, null, false)
    val title = view.findViewById(R.id.title) as TextView
    val internetCover = view.findViewById(R.id.internetCover) as TextView
    val fileCover = view.findViewById(R.id.fileCover) as TextView
    val bookmark = view.findViewById(R.id.bookmark) as TextView
    dialog.setContentView(view)

    title.setOnClickListener {
      EditBookTitleDialogFragment.newInstance(book)
        .show(fragmentManager, EditBookTitleDialogFragment.TAG)
      dismiss()
    }
    internetCover.setOnClickListener {
      (activity as Callback).onInternetCoverRequested(book)
      dismiss()
    }
    fileCover.setOnClickListener {
      (activity as Callback).onFileCoverRequested(book)
      dismiss()
    }
    bookmark.setOnClickListener {
      BookmarkDialogFragment.newInstance(book.id)
        .show(fragmentManager, BookShelfController.TAG)
      dismiss()
    }

    tintLeftDrawable(title)
    tintLeftDrawable(internetCover)
    tintLeftDrawable(fileCover)
    tintLeftDrawable(bookmark)

    return dialog
  }

  private fun tintLeftDrawable(textView: TextView) {
    val left = textView.leftCompoundDrawable()!!
    val tinted = left.tinted(context.color(R.color.icon_color))
    textView.setCompoundDrawables(tinted, textView.topCompoundDrawable(), textView.rightCompoundDrawable(), textView.bottomCompoundDrawable())
  }

  private fun bookId() = arguments.getLong(NI_BOOK)

  companion object {
    private const val NI_BOOK = "niBook"
    fun newInstance(book: Book) = EditBookBottomSheet().apply {
      arguments = Bundle().apply {
        putLong(NI_BOOK, book.id)
      }
    }
  }

  interface Callback {
    fun onInternetCoverRequested(book: Book)
    fun onFileCoverRequested(book: Book)
  }
}