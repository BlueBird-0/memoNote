package com.example.ShortMemo

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
        val sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS")

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
            val db = dbHelper.writableDatabase
            db.insert(FeedEntry.TABLE_NAME, null, values)
        }

        fun checkData(context: Context, position: Int) {
            /* db 데이터 읽어오기 */
            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.writableDatabase

            val projection = arrayOf(BaseColumns._ID)
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
                move(position)  //해당 컬럼으로 이동
                Log.d("test001", "ID : "+cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
                val values = ContentValues().apply {
                    put(FeedEntry.COLUMNS_NOTE_CHECKED_TIME, sdf.format(Date()))
                }
                val whereId = "${BaseColumns._ID} = ${cursor.getLong(getColumnIndex("${BaseColumns._ID}"))}"
                db.update(FeedEntry.TABLE_NAME,  values,  whereId, null)
            }
        }
        fun swapData(context: Context, swapPositionFrom: Int, swapPositionTo: Int){
            /* db swap data
            * change CREATED_TIME at From */
            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.writableDatabase

            val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CONTENT)
            val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL "
            val sortOrder = "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} ASC"

            val cursor = db.query(
                    FeedEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,//selection,              // The columns for the WHERE clause
                    null,     // The values for the WHERE clause
                    null,         // don't group the rows
                    null,           // don't filter by row groups
                    sortOrder               // The sort order
            )
            with(cursor){
                if(swapPositionFrom >= cursor.count || swapPositionTo >= cursor.count)  //리스트 마지막 벗어나는 에러 처리
                    return


                moveToPosition(swapPositionFrom)  //해당 컬럼으로 이동
                val From_Id = cursor.getLong(getColumnIndex("${BaseColumns._ID}"))
                Log.d("test001", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))

                moveToPosition(swapPositionTo)  //해당 컬럼으로 이동
                Log.d("test001", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
                val To_createdTime = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}"))
//                Log.d("test001", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))



                var diffTime : Long = 0

                Log.d("test001", "count : ${cursor.count}")
                if(swapPositionFrom < swapPositionTo) { //위에서 아래로 스왑
                    Log.d("test001", "----------1번 실행")
                    diffTime = 1000
                    if(swapPositionTo+1 <= cursor.count-1) {      //더 밑에 블록이 있을 때
                        Log.d("test001", "----------2번 실행")
                        moveToPosition(swapPositionTo +1)
                        val ToUnder_createdTime = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}"))
                        diffTime = (sdf.parse(ToUnder_createdTime).time - sdf.parse(To_createdTime).time) /2
                    }
                }else if (swapPositionFrom > swapPositionTo) { //아래에서 위로 스왑
                    diffTime = -1000
                    Log.d("test001", "----------3번 실행")
                    if(swapPositionTo-1 >= 0) {    //더 위에 블록이 있을 때
                        Log.d("test001", "----------4번 실행")
                        moveToPosition(swapPositionTo -1)
                        val ToOver_createdTime = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}"))
                        diffTime = (sdf.parse(ToOver_createdTime).time - sdf.parse(To_createdTime).time) /2
                    }
                }

                val changedTime = sdf.parse(To_createdTime)
                Log.d("test001", "originTime : "+changedTime +"---"+ sdf.format(changedTime))
                changedTime.time = changedTime.time+ diffTime
                Log.d("test001", "changedTime : "+changedTime +"---"+ sdf.format(changedTime))

                val values = ContentValues().apply {
                    put(FeedEntry.COLUMNS_NOTE_CREATED_TIME, sdf.format(changedTime))
                }
                val whereId = "${BaseColumns._ID} = ${From_Id}"
                db.update(FeedEntry.TABLE_NAME,  values,  whereId, null)
                Log.d("test001", "update!")
            }

        }
    }

}