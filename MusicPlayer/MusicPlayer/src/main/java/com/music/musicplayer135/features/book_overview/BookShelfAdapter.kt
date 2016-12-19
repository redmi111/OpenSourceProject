package com.music.musicplayer135.features.book_overview

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.internal.MDTintHelper
import com.squareup.picasso.Picasso
import com.music.musicplayer135.Book
import com.music.musicplayer135.R
import com.music.musicplayer135.injection.App
import com.music.musicplayer135.misc.*
import com.music.musicplayer135.persistence.PrefsManager
import com.music.musicplayer135.uitools.CoverReplacement
import com.music.musicplayer135.uitools.visible
import i
import org.acra.util.ToastSender
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.music.musicplayer135.ads.*;

// display all the books
class BookShelfAdapter(private val c: Context, private val bookClicked: (Book, ClickType) -> Unit) : RecyclerView.Adapter<BookShelfAdapter.BaseViewHolder>() {

  private var books = ArrayList<Book>()
  public  var type   = 0;
  var check = false
  var j = 0;
  private var bookcache = ArrayList<Book>()
  private var bookold = ArrayList<Book>()
  private var adsInstall = false
  private var adsNext = false
  private var adsStart = false

  @Inject lateinit var prefs: PrefsManager

  init {
    i { "A new adapter was created." }
    App.component().inject(this)
    setHasStableIds(true)
  }

  private fun formatTime(ms: Int): String {
    val h = "%02d".format((TimeUnit.MILLISECONDS.toHours(ms.toLong())))
    val m = "%02d".format((TimeUnit.MILLISECONDS.toMinutes(ms.toLong()) % 60))
    return h + ":" + m
  }

  /** Adds a new set of books and removes the ones that do not exist any longer **/
  fun newDataSet(newBooks: List<Book>) {
    i { "newDataSet was called with ${newBooks.size} books" }

    val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

      override fun getOldListSize(): Int = books.size

      override fun getNewListSize(): Int = newBooks.size

      override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = books[oldItemPosition]
        val newItem = newBooks[newItemPosition]
        return oldItem.globalPosition() == newItem.globalPosition() && oldItem.name == newItem.name
      }

      override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = books[oldItemPosition]
        val newItem = newBooks[newItemPosition]
        return oldItem.id == newItem.id
      }
    }, false) // no need to detect moves as the list is sorted

    books.clear()
    books.addAll(newBooks)
    bookold = books
    var packetname = "com.music.musicplayer135"
     GetConfig(packetname)
    diffResult.dispatchUpdatesTo(this)
  }

  fun changeBookCover(book: Book) {
    val index = books.indexOfFirst { it.id == book.id }
    if (index >= 0) {
      notifyItemChanged(index)
    }
  }

  override fun getItemId(position: Int): Long = books[position].id

  fun getItem(position: Int): Book = books[position]

  var displayMode: BookShelfController.DisplayMode = BookShelfController.DisplayMode.LIST
    set(value) {
      if (value != field) {
        field = value
        i { "displayMode changed to $field" }
        notifyDataSetChanged()
      }
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
    type = viewType
    when (viewType) {
      1 -> return GridViewHolder(parent)
      0 -> {
        return ListViewHolder(parent)
      }
      else -> throw IllegalStateException("Illegal viewType=" + viewType)
    }
  }

  override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.bind(books[position])

  override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) = when {
    payloads.isEmpty() -> onBindViewHolder(holder, position)
    else -> holder.bind(books[position])
  }

  override fun getItemCount(): Int = books.size

  override fun getItemViewType(position: Int): Int = if (displayMode == BookShelfController.DisplayMode.LIST) 0 else 1

  inner class ListViewHolder(parent: ViewGroup) : BaseViewHolder(parent.layoutInflater().inflate(R.layout.book_shelf_list_layout, parent, false)) {
    private val progressBar = find<ProgressBar>(R.id.progressBar)
    private val leftTime: TextView = find(R.id.leftTime)
    private val rightTime: TextView = find(R.id.rightTime)

    init {
      MDTintHelper.setTint(progressBar, parent.context.color(R.color.accent))
    }

    override fun bind(book: Book) {
      super.bind(book)
      val globalPosition = book.globalPosition()
      val globalDuration = book.globalDuration
      val progress = Math.round(100f * globalPosition.toFloat() / globalDuration.toFloat())

      leftTime.text = formatTime(globalPosition)
      progressBar.progress = progress
      rightTime.text = formatTime(globalDuration)
    }
  }


  /** ViewHolder for the grid **/
  inner class GridViewHolder(parent: ViewGroup) : BaseViewHolder(parent.layoutInflater()
    .inflate(R.layout.book_shelf_grid_layout, parent, false))

  /** ViewHolder base class **/
  abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val coverView: ImageView

    private val currentPlayingIndicator: ImageView
    private val titleView: TextView
    private val editBook: View
    var indicatorVisible = false
      private set


    init {
      coverView = itemView.find(R.id.coverView)
      currentPlayingIndicator = itemView.find(R.id.currentPlayingIndicator)
      titleView = itemView.find(R.id.title)
      editBook = itemView.find(R.id.editBook)
      if (GetConfig.AppNextId == "" && GetConfig.InstalId == ""  && GetConfig.StartAppId == ""){
        adsNext = true
        adsStart = true
        adsInstall = true
      }
      if (type == 1 ) {
//        itemView.findViewById(R.id.layout_navigator_install).visible = false
//        itemView.findViewById(R.id.layout_navigator_next).visible = false
//        itemView.findViewById(R.id.layout_navigator_start).visible = false
        books = bookold
      }

    }

    /**
     * Binds the ViewHolder to a book

     * @param book The book to bind to
     */
    @CallSuper
    open fun bind(book: Book) {

      //setting text
      val name = book.name
      titleView.text = name

      bindCover(book)
      indicatorVisible = book.id == prefs.currentBookId.value()
      currentPlayingIndicator.visible = indicatorVisible

      itemView.setOnClickListener { bookClicked(getItem(adapterPosition), ClickType.REGULAR) }
      editBook.setOnClickListener { bookClicked(getItem(adapterPosition), ClickType.MENU) }

      coverView.supportTransitionName = book.coverTransitionName
      if ( type == 0)
      runAds(book)
    }

    /*
      this method call ads
      - 1 . get index for book
      - 2 . select insert ads ex : mod 3,7,10
      - 3 . disable view not use
      - 4 . show view ads
      - 5 . call back return view disable
     */
    private fun runAds(book: Book){
     var i =  books.indexOf(book)
      if (i % 3 == 0 && i != 0) {
        var q = i / 3
        if (!adsNext && q == 1) {
          showViewAds(i, book);
          books.add(book)
          adsNext = true
        } else if (!adsInstall && q % 2 == 0) {
          showViewAds(i, book)
          books.add(book)
          adsInstall = true
        } else if (!adsStart && q % 3 == 0) {
          showViewAds(i, book);
          books.add(book)
          adsStart = true
        }
        if (j==3){
          adsInstall = false
          adsStart = false
          adsNext = false
          j == 0
        }
        else if (adsNext && adsStart && adsInstall)
          j = j + 1
      }
    }
    /*
    * - This method show view ads from i : 1,2,3
     */
    private fun showViewAds(i : Int,book: Book){
      coverView.visible = false;
      currentPlayingIndicator.visible = false
      titleView.visible = false
      editBook.visible = false
      if (i==3)
      itemView.findViewById(R.id.layout_navigator_next).visible = true;
      else
        if (i==7)
          itemView.findViewById(R.id.layout_navigator_install).visible = true;
      else
          itemView.findViewById(R.id.layout_navigator_start).visible = true

      itemView.findViewById(R.id.leftTime).visible = false;
      itemView.findViewById(R.id.rightTime).visible = false;
      itemView.findViewById(R.id.progressBar).visible = false
      check = true
    }
    private fun bindCover(book: Book) {
      // (Cover)
      val coverFile = book.coverFile()
      val coverReplacement = CoverReplacement(book.name, c)

      if (coverFile.exists() && coverFile.canRead()) {
        Picasso.with(c).load(coverFile).placeholder(coverReplacement).into(coverView)
      } else {
        Picasso.with(c).cancelRequest(coverView)
        // we have to set the replacement in onPreDraw, else the transition will fail.
        coverView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
          override fun onPreDraw(): Boolean {
            coverView.viewTreeObserver.removeOnPreDrawListener(this)
            coverView.setImageDrawable(coverReplacement)
            return true
          }
        })
      }
    }
  }

  enum class ClickType {
    REGULAR,
    MENU
  }
}