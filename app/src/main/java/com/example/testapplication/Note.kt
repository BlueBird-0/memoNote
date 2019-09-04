package com.example.testapplication

import androidx.lifecycle.ViewModel

class Note (var content:String) : ViewModel() {
    var created_time: Long? = null
}