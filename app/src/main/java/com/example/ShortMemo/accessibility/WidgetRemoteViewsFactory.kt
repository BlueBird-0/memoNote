package com.example.ShortMemo.accessibility

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Binder
import android.provider.BaseColumns
import android.util.Log
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.ShortMemo.FeedEntry
import com.example.ShortMemo.FeedReaderDbHelper
import com.example.ShortMemo.R
import kotlin.contracts.ContractBuilder

class WidgetRemoteViewsFactory(var applicationContext : Context, intent : Intent) : RemoteViewsService.RemoteViewsFactory {
    private lateinit var mCursor : Cursor

    override fun onCreate() {
        /* db 데이터 읽어오기 */
        val dbHelper = FeedReaderDbHelper(applicationContext)
        var db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
        val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL"
        val sortOrder = "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} ASC"

        mCursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,//selection,              // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rows
                null,           // don't filter by row groups
                sortOrder               // The sort order
        )
    }

    override fun onDataSetChanged() {
        Log.d("Test001_widget", "changed 1")
        if(mCursor != null){
            mCursor.close()
        }
        Log.d("Test001_widget", "changed 2")

        var identityToken = Binder.clearCallingIdentity()

        /* db 데이터 읽어오기 */
        val dbHelper = FeedReaderDbHelper(applicationContext)
        var db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
        val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL"
        val sortOrder = "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} ASC"

        mCursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,//selection,              // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rows
                null,           // don't filter by row groups
                sortOrder               // The sort order
        )

//        var uri =PATH_TODOS


//        mCursor = applicationContext.contentResolver.query(uri,
//                null,
//                null,
//                null,
//                "")
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {
        if(mCursor != null) {
            mCursor.close()
        }
    }

    override fun getCount(): Int {
        if(mCursor == null)
            return 0
        else
            return mCursor.count
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if(position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null
        }

        var remoteViews = RemoteViews(applicationContext.packageName, R.layout.list_widget_item)
        remoteViews.setTextViewText(R.id.widgetItemTaskNameLabel, mCursor.getString(1))
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        if(mCursor.moveToPosition(position) == true)
        {
            return mCursor.getLong(0)
        }else {
            return position.toLong()
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}