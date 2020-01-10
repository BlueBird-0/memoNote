package com.bluebird.ShortMemo

import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FireStoreDbHelper {
    companion object {
        var kakaoId : String? = null

        fun synchronize(context: Context) {
            if(!isLogined(context)) return

            //init firebase DB
//            initFirebase(context)

            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(kakaoId!!).collection("Notes")
                    .get()
                    .addOnSuccessListener { documentReference ->
                        Log.d("test001", "get reference count : ${documentReference.documents.size}")
                        if(documentReference.documents.size != FeedReaderDbHelper.)
                        for(document in documentReference.documents) {
                            if(document.get("id") == )
                        }
                    }
        }

//        fun addSnapchot() {
//            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
//                for(document in querySnapshot!!.documents){
//                    Log.d("test001", "read Data")
//                    Log.d("test001", "id: {$document.get(\"id\")}")
//                }
//            }
//        }

        fun initFirebase(context : Context) {
                /* db 데이터 읽어오기 */
                val dbHelper = FeedReaderDbHelper(context)
                var db = dbHelper.readableDatabase

                val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMNS_NOTE_CONTENT, FeedEntry.COLUMNS_NOTE_CREATED_TIME, FeedEntry.COLUMNS_NOTE_CHECKED_TIME, FeedEntry.COLUMNS_NOTE_PICTURE_URI)
                val selection = "${FeedEntry.COLUMNS_NOTE_CHECKED_TIME} IS NULL"
                val sortOrder = "${FeedEntry.COLUMNS_NOTE_CREATED_TIME} ASC"

                val cursor = db.query(
                        FeedEntry.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        null,//selection,              // The columns for the WHERE clause
                        null,     // The values for the WHERE clause
                        null,         // don't group the rowss
                        null,           // don't filter by row groups
                        null               // The sort order
                )

                with(cursor){
                    Log.d("test001_FirebaseInit", "커서 시작됨")
                    while(moveToNext()){
                        Log.d("test001_Fire", "ID : "+cursor.getLong(getColumnIndex("${BaseColumns._ID}")))
                        Log.d("test001_Fire", "CONTENT : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}")))
                        Log.d("test001_Fire", "CREATED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                        Log.d("test001_Fire", "CHECKED_TIME : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                        Log.d("test001_Fire", "PICTURE_URI : "+cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}")))

                        val id = cursor.getLong(getColumnIndex("${BaseColumns._ID}"))
                        val content = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CONTENT}"))
                        val createdTime : Date? = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CREATED_TIME}")))
                        var checkedTime : Date?
                        if (cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")) == null) {
                            checkedTime = null
                        }else {
                            checkedTime = FeedReaderDbHelper.sdf.parse(cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_CHECKED_TIME}")))
                        }
                        var pictureUri = cursor.getString(getColumnIndex("${FeedEntry.COLUMNS_NOTE_PICTURE_URI}"))

                        var noteVM = NoteViewModel(Note(id, content, createdTime, checkedTime, pictureUri))
                    }
                }
        }


        fun write(context: Context, note : Note) {
            if(!isLogined(context)) return
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(kakaoId!!).collection("Notes")
                    .add(note.getHashMap())
                    .addOnSuccessListener { documentReference ->
                        Log.d("test001", "firebase : ${documentReference.id} write success!")
                    }
                    .addOnFailureListener { e ->
                        Log.d("test001" ,"firebase Error : ${e}")
                    }
        }

        fun check(context: Context, id: Long, date: Date) {
            if(!isLogined(context)) return
            val db = FirebaseFirestore.getInstance()
            Log.d("test001", "onDelete()")

            db.collection("users").document(kakaoId!!).collection("Notes")
                    .whereEqualTo("id", id).get().addOnSuccessListener {
                        Log.d("test001", "addOnSuccessListener")
                        for (document in it.documents) {
                            document.reference.update("checkedTime", date)
                            Log.d("test001", "document checked")
                        }

                    }
        }

        fun delete(context: Context, id: Long) {
            if(!isLogined(context)) return
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(kakaoId!!).collection("Notes")
                    .whereEqualTo("id", id).get().addOnSuccessListener {
                        Log.d("test001", "addOnSuccessListener")
                        for (document in it.documents) {
                            document.reference.delete()
                            Log.d("test001", "document deleted")
                        }
                    }
        }

        fun isLogined(context: Context) : Boolean {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
            var kakaoLogined = sharedPref.getBoolean(context.getString(R.string.option_kakaoLogined), false)
            kakaoId = sharedPref.getString(context.getString(R.string.option_kakaoId), null)
            if(kakaoLogined == false || kakaoId == null)
                return false
            return true
        }
    }
}