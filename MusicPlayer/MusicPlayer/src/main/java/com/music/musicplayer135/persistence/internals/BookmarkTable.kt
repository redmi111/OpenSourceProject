package com.music.musicplayer135.persistence.internals

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.music.musicplayer135.Bookmark
import java.io.File

fun Bookmark.toContentValues() = ContentValues().apply {
  put(BookmarkTable.TIME, time)
  put(BookmarkTable.PATH, mediaFile.absolutePath)
  put(BookmarkTable.TITLE, title)
  if (id != Bookmark.ID_UNKNOWN) put(BookmarkTable.ID, id)
}

fun Cursor.toBookmark(): Bookmark {
  val path = string(BookmarkTable.PATH)
  val title = string(BookmarkTable.TITLE)
  val time = int(BookmarkTable.TIME)
  val id = long(BookmarkTable.ID)
  return Bookmark(File(path), title, time, id)
}

/**
 * Represents an sql table for bookmarks
 *
 * @author Paul Woitaschek
 */
object BookmarkTable {

  const val PATH = "bookmarkPath"
  const val TITLE = "bookmarkTitle"
  const val TABLE_NAME = "tableBookmarks"
  const val TIME = "bookmarkTime"
  const val ID = BaseColumns._ID
  private val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ( " +
    " $ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
    " $PATH TEXT NOT NULL, " +
    " $TITLE TEXT NOT NULL, " +
    " $TIME INTEGER NOT NULL)"

  fun onCreate(db: SQLiteDatabase) {
    db.execSQL(CREATE_TABLE)
  }

  fun dropTableIfExists(db: SQLiteDatabase) {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
  }
}