package com.bluebird.ShortMemo

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreDbHelper {
    companion object {
        var kakaoId = null
//        var pass = null

        fun write(context: Context, note : Note) {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
            var kakaoLogined = sharedPref.getBoolean(context.getString(R.string.option_kakaoLogined), false)

            if(kakaoLogined == false)
                return


            val db = FirebaseFirestore.getInstance()

            db.collection("users")
                    .add(note.getHashMap())
                    .addOnSuccessListener { documentReference ->
                        Log.d("test001", "firebase : ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.d("test001" ,"firebase : ${e}")
                    }
        }
    }
}