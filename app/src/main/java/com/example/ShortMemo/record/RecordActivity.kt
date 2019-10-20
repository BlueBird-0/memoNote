package com.example.ShortMemo.record

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ShortMemo.*
import com.example.ShortMemo.ItemTouchHelperCallBack
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class RecordActivity: AppCompatActivity() {

    private val list = mutableListOf<ViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        //AddMob
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        initRecordList()


        btn_rec()
        btn_set()


        var noteVM = NoteViewModel(Note("content123"))
        noteVM.note.checkedTime = Date()

        list.add(noteVM)
        list.add(noteVM)
        list.add(noteVM)
    }


    private fun initRecordList(){
        note_list.layoutManager = LinearLayoutManager(this)
        val adapter = RecordRecyclerViewAdapter(applicationContext, list)
        note_list.adapter = adapter

        val itemTouchHelperCallBack = ItemTouchHelperCallBack(adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        touchHelper.attachToRecyclerView(note_list)
    }

    override fun onBackPressed() {
        finish()
//        overridePendingTransition(R.anim.hold_activity,R.anim.right_activity)
//        super.onBackPressed()
    }

    private fun btn_rec() {
        btn_rec.setOnClickListener {
            finish()
        }
    }
    private  fun btn_set() {
        btn_set.setOnClickListener {
            val optionActivity = Intent(this, OptionActivity::class.java)
            startActivity(optionActivity)
//            startActivityForResult(writeIntent, WRITE_NOTE_REQUEST_CODE)
            overridePendingTransition(R.anim.right_activity, R.anim.hold_activity)
            finish()
        }
    }
}