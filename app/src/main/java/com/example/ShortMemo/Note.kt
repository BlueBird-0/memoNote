package com.example.ShortMemo

import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Note (var id: Long, var content:String, var createdTime:Date? = Date(), var checkedTime:Date? = null, var pictureUri : ArrayList<Uri>? = null) : Parcelable

class NoteViewModel(val note : Note): ViewModel()