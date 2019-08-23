package com.example.testapplication

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        keyboard.requestFocus()
        //val imm : InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.showSoftInput(keyboard, 0)

        var aa : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (aa.isAcceptingText) {
            Log.d("test001","Software Keyboard was shown")
        } else {
            Log.d("test001","Software Keyboard was not shown")
        }
    }

}