package com.bluebird.ShortMemo

import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Parcelize
data class Note (var id: Long, var content:String, var createdTime:Date? = Date(), var checkedTime:Date? = null, var pictureUri : ArrayList<Uri>? = null) : Parcelable
{
    constructor(id: Long, content:String, createdTime:Date? = Date(), checkedTime:Date? = null, pictureUriStr : String) : this(id, content, createdTime, checkedTime) {
        //이미지 불러오기
                if(pictureUriStr == "null"){
                    pictureUri = null
                }else {
                    val pictureUriString = pictureUriStr
                    val pictureUriReplace = pictureUriString?.substring(1, pictureUriString.length-1)?.split(", ")
                    pictureUri = ArrayList()
                    for (str in pictureUriReplace!!) {
                        pictureUri!!.add(Uri.parse(str))
                    }
                }
    }

    fun getHashMap() : HashMap <String, Any?>{
        return hashMapOf(
                "id" to id,
                "content" to content,
                "createdTime" to createdTime,
                "checkedTime" to checkedTime,
                "pictureUri" to "null"
        )
    }
}


class NoteViewModel(val note : Note): ViewModel()