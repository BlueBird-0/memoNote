package com.example.testapplication

import android.arch.lifecycle.ViewModel
import android.support.v7.widget.RecyclerView

class Note (var content:String) : ViewModel() {
    var created_time: Long? = null
}