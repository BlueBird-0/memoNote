package com.example.ShortMemo.accessibility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.BaseColumns
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.ShortMemo.*
import java.util.*

class BroadcastReceiverApp : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("Test001_Broadcast", "Called BroadcastReceiverApp! set Start Service  action:[ " + intent?.action+"]")

        val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
        val action = intent?.action

        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            //TODO 자동 실행 체크확인, AutoExecution
            if(sharedPref.getBoolean(context.getString(R.string.option_autoExecution), false) == false) return

            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
        else if(action.equals("NotificationDeleted")) {
            //TODO 상태 바 체크 확인
            if(sharedPref.getBoolean(context.getString(R.string.option_statusBar), false) == false) return

            with(NotificationManagerCompat.from(context)) {
                notify(970317, FunNotification.notification(context).build())
                readNotes(this, context)
            }
        }

    }
    private fun readNotes(notifyManagerCompat: NotificationManagerCompat, context : Context) {
        var notifyId = 970318
        /* db 데이터 읽어오기 */
        val dbHelper = FeedReaderDbHelper(context)
        var db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
        val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL"
        val sortOrder = "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} ASC"

        val cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,//selection,              // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rowss
                null,           // don't filter by row groups
                sortOrder               // The sort order
        )

        val itemIds = mutableListOf<Long>()
        with(cursor) {
            Log.d("MainActivity", "커서 시작됨")
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)

                Log.d("BroadcastReceiver", "ID : " + cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
                Log.d("BroadcastReceiver", "CONTENT : " + cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
                Log.d("BroadcastReceiver", "CREATED_TIME : " + cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                Log.d("BroadcastReceiver", "CHECKED_TIME : " + cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                Log.d("BroadcastReceiver", "PICTURE_URI : " + cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")))

                val id = cursor.getLong(getColumnIndex("${BaseColumns._ID}"))
                val content = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}"))
                val createdTime: Date? = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                var checkedTime: Date?
                if (cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")) == null) {
                    checkedTime = null
                } else {
                    checkedTime = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                }
                var pictureUri = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}"))

                var note = Note(id, content, createdTime, checkedTime, pictureUri)
                notifyManagerCompat.notify(notifyId++, FunNotification.notificationNote(context, note).build())
            }
        }
    }
}