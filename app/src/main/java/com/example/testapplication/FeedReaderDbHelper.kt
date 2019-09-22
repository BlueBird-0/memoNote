package com.example.testapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import java.text.SimpleDateFormat

object FeedEntry : BaseColumns {
    val TABLE_NAME = "entry"
    val COLUMNS_NOTE_CONTENT = "content"
    val COLUMNS_NOTE_CREATED_TIME = "createTime"
    val COLUMNS_NOTE_CHECKED_TIME = "checkTime"
    val COLUMNS_NOTE_PICTURE_URI = "pictureUri"
}


class FeedReaderDbHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                    "${FeedEntry.COLUMNS_NOTE_CONTENT} TEXT," +
                    "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} DATE," +
                    "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} DATE," +
                    "${FeedEntry.COLUMNS_NOTE_PICTURE_URI} TEXT)"

    private val SQL_DELETE__ENTRIES =
            "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        Log.d("test001", "DB 생성 완료")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE__ENTRIES)
        Log.d("test001", "DB 업그레이드")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //super.onDowngrade(db, oldVersion, newVersion)
        onUpgrade(db , oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME = "FeedReader.db"
        val sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        fun writeData(context: Context, note: Note) {
            //데이터 쓰기
            val dbHelper = FeedReaderDbHelper(context)
            val values = ContentValues().apply {
                put(FeedEntry.COLUMNS_NOTE_CONTENT, note.content)
                if (note.createdTime != null)
                    put(FeedEntry.COLUMNS_NOTE_CREATED_TIME, sdf.format(note?.createdTime))
                if (note.checkedTime != null)
                    put(FeedEntry.COLUMNS_NOTE_CHECKED_TIME, sdf.format(note?.checkedTime))
                put(FeedEntry.COLUMNS_NOTE_PICTURE_URI, note.pictureUri.toString())
            }
            var db = dbHelper.writableDatabase
            val newRowId = db.insert(FeedEntry.TABLE_NAME, null, values)
            Log.d("test001", "글쓰기 실행 했음." + newRowId.toString())
        }
    }

}