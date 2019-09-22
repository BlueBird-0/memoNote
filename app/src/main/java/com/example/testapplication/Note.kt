package com.example.testapplication

import android.net.Uri
import androidx.lifecycle.ViewModel
import java.util.*

class Note (var content:String, var createdTime:Date? = Date(), var checkedTime:Date? = null, var pictureUri : Array<Uri>? = null) : ViewModel() {
    //var createdTime: Date? = Date()
    //var checkedTime: Date? = null
    //var pictureUri: Array<Uri> ?= null
}