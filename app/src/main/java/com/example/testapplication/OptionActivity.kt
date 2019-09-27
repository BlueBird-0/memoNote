package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_main.*


class OptionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        //AddMob
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        btn_rec()
        btn_set()
    }

    override fun onBackPressed() {
        finish()
//        overridePendingTransition(R.anim.hold_activity,R.anim.left_activity)
//        super.onBackPressed()
    }

    private fun btn_rec() {
        btn_rec.setOnClickListener {
            val recordActivity = Intent(this, RecordActivity::class.java)
            startActivity(recordActivity)
//            startActivityForResult(writeIntent, WRITE_NOTE_REQUEST_CODE)
            overridePendingTransition(R.anim.left_activity, R.anim.hold_activity)
            finish()
        }
    }
    private  fun btn_set() {
        btn_set.setOnClickListener {
            finish()
        }
    }
}