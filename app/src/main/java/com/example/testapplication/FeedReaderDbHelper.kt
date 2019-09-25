package com.example.testapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

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
        const val DATABASE_VERSION = 5
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
            db.insert(FeedEntry.TABLE_NAME, null, values)
        }

        fun checkData(context: Context, position: Int) {
            /* db 데이터 읽어오기 */
            val dbHelper = FeedReaderDbHelper(context)
            var db = dbHelper.writableDatabase

            val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
            val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL "
            val cursor = db.query(
                    FeedEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,//selection,              // The columns for the WHERE clause
                    null,     // The values for the WHERE clause
                    null,         // don't group the rows
                    null,           // don't filter by row groups
                    null               // The sort order
            )

            with(cursor){
                Log.d("test001", "entered cursor : position : "+position)
                move(position+1)  //해당 컬럼으로 이동
                Log.d("test001", "ID : "+cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
//                Log.d("test001", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
//                Log.d("test001", "CREATED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
//                Log.d("test001", "CHECKED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
//                Log.d("test001", "PICTURE_URI : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")))
                val values = ContentValues().apply {
                    put(FeedEntry.COLUMNS_NOTE_CHECKED_TIME, sdf.format(Date()))
                }
                val whereId = "${BaseColumns._ID} = ${cursor.getLong(getColumnIndex("${BaseColumns._ID}"))}"
                db.update(FeedEntry.TABLE_NAME,  values,  whereId, null)
                Log.d("test001", "업데이트 완료?")
            }

        }
    }

}