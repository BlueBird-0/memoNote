package com.example.ShortMemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_write.*
import java.util.*

class WriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        var note = Note(0, "")
        // 전달 된 노트 실행
        var id = intent.getLongExtra(BaseColumns._ID, -1)
        if(id != -1L ) {
            note = readNote(id)
            keyboardEdit.setText(note.content)
        }


        keyboardEdit.requestFocus()
//        var keyManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        fab_cam.setOnClickListener{}
        fab_mic.setOnClickListener{}
        uploadBtn.setOnClickListener{view->

            /* writeNote test code */
            var content = keyboardEdit.text.toString()
            note.content = content
            if(note.id == 0L) {
                note.id = FeedReaderDbHelper.writeData(applicationContext, note)
            }else {
                FeedReaderDbHelper.updateData(applicationContext, note, id)
            }


            var returnIntent = Intent()
            returnIntent.putExtra("note", note)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(R.anim.hold_activity,R.anim.close_activity)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.hold_activity,R.anim.close_activity)
        //super.onBackPressed()
    }

    private fun readNote(id: Long) : Note{
        /* db 데이터 읽어오기 */
        val dbHelper = FeedReaderDbHelper(applicationContext)
        var db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
        val selection = "${BaseColumns._ID} = ${id}"

        val cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,//selection,              // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rows
                null,           // don't filter by row groups
                null               // The sort order
        )

        val itemIds = mutableListOf<Long>()
        with(cursor){
            Log.d("Test001_writeActivity", "커서 시작됨")
            //while(moveToNext()){
            moveToNext()
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)

                Log.d("Test001_writeActivity", "ID : "+cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
                Log.d("Test001_writeActivity", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
                Log.d("Test001_writeActivity", "CREATED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                Log.d("Test001_writeActivity", "CHECKED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                Log.d("Test001_writeActivity", "PICTURE_URI : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")))

                val id = cursor.getLong(getColumnIndex("${BaseColumns._ID}"))
                val content = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}"))
                val createdTime : Date? = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                var checkedTime : Date?
                if (cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")) == null) {
                    checkedTime = null
                }else {
                    checkedTime = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                }

                var note = Note(id, content, createdTime, checkedTime, pictureUri = null)
                //val pictureUri = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}"))
                return note
            //}
        }
    }

}