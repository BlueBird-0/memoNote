package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_main.*


class RecordActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        //AddMob
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        btn_rec()
        btn_set()
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