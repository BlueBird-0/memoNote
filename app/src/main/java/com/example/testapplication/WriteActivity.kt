package com.example.testapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        keyboardEdit.requestFocus()

//        var keyManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        fab_cam.setOnClickListener{}
        fab_mic.setOnClickListener{}
        uploadBtn.setOnClickListener{view->

            /* writeNote test code */
            var content = keyboardEdit.text.toString()

            var note = Note(content)
            FeedReaderDbHelper.writeData(applicationContext, note)


            var returnIntent = Intent()
            returnIntent.putExtra("note", note)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(R.anim.hold_activity,R.anim.close_activity)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.hold_activity,R.anim.close_activity)
        //super.onBackPressed()
    }
}