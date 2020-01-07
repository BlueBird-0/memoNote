package com.bluebird.ShortMemo

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FireStoreDbHelper {
    companion object {
        var kakaoId : String? = null
        var pass = null

        fun write(context: Context, note : Note) {
            if(!isLogined(context)) return
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(kakaoId!!).collection("Notes")
                    .add(note.getHashMap())
                    .addOnSuccessListener { documentReference ->
                        Log.d("test001", "firebase : ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.d("test001" ,"firebase : ${e}")
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
                            Log.d("test001", "document checked")
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