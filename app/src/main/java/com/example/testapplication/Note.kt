package com.example.testapplication

import android.content.ClipData
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Note (var content:String, var createdTime:Date? = Date(), var checkedTime:Date? = null, var pictureUri : ArrayList<Uri>? = null) : Parcelable

class NoteViewModel(val note : Note): ViewModel()