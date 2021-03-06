package com.bluebird.ShortMemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.bluebird.ShortMemo.accessibility.BroadcastReceiverApp
import com.bluebird.ShortMemo.accessibility.FloatingViewService
import com.bluebird.ShortMemo.accessibility.FunNotification
import com.bluebird.ShortMemo.record.RecordActivity
import com.google.android.gms.ads.AdRequest
import com.kakao.auth.Session
import com.kakao.usermgmt.LoginButton
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import kotlinx.android.synthetic.main.activity_main.adView
import kotlinx.android.synthetic.main.activity_main.btn_rec
import kotlinx.android.synthetic.main.activity_main.btn_set
import kotlinx.android.synthetic.main.activity_option.*


class OptionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        //AddMob
//        setAdView()
        btn_rec()
        btn_set()

//        testcode

        //activity code
        val sharedPref = this.getSharedPreferences(getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
        var usingStatusBar = sharedPref.getBoolean(getString(R.string.option_statusBar), false)
        switch_statusbar.isChecked = usingStatusBar
        var usingWindowShortcut = sharedPref.getBoolean(getString(R.string.option_windowShortcut), false)
        switch_windowShortcut.isChecked = usingWindowShortcut
        var usingAutoExecution = sharedPref.getBoolean(getString(R.string.option_autoExecution), false)
        switch_autoexec.isChecked = usingAutoExecution


        switch_kakaoLogined.isChecked = sharedPref.getBoolean(getString(R.string.option_kakaoLogined), false)
        layout_kakao.setOnClickListener(View.OnClickListener {
//            if(Session.getCurrentSession().isOpened) {}
            if(switch_kakaoLogined.isChecked) {
                switch_kakaoLogined.isChecked = false
                UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                    override fun onCompleteLogout() {
                        Log.d("test001", "Logouted!")       // Logout Code
                        sharedPref.edit().putBoolean(getString(R.string.option_kakaoLogined), false).commit()
                        sharedPref.edit().putString(getString(R.string.option_kakaoId), null).commit()
                    }
                })
            }else {
                var btn = findViewById<LoginButton>(R.id.kakaoLoginBtn)
                btn.performClick()
            }
        })


        //Widget 도움말 페이지 열기
        layout_widget.setOnClickListener(View.OnClickListener {
            var intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://support.google.com/android/answer/9450271?hl=ko")
            startActivity(intent)
        })

        //Window 바로가기 floating button
        layout_windowShortcut.setOnClickListener(View.OnClickListener {
            switch_windowShortcut.isChecked = !switch_windowShortcut.isChecked
        })
        switch_windowShortcut.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                //Floating View 권한 체크
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
                    var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + packageName))
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
                    switch_windowShortcut.isChecked = false
                }else
                {
                    //Floating View 실행
                    startService(Intent(applicationContext, FloatingViewService::class.java))
                    sharedPref.edit().putBoolean(getString(R.string.option_windowShortcut), true).commit()
                }
            }else {
                stopService(Intent(applicationContext, FloatingViewService::class.java))
                sharedPref.edit().putBoolean(getString(R.string.option_windowShortcut), false).commit()
            }
        })

        //Notification
        layout_statusbar.setOnClickListener(View.OnClickListener {
            switch_statusbar.isChecked = !switch_statusbar.isChecked
        })
        switch_statusbar.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                with(NotificationManagerCompat.from(this)) {
                    //notify(970317, FunNotification.notification(applicationContext).build())
                    var callIntent = Intent(applicationContext, BroadcastReceiverApp::class.java)
                    callIntent.action = FunNotification.ACTION_DELETE
                    sendBroadcast(callIntent)

                }
                sharedPref.edit().putBoolean(getString(R.string.option_statusBar), true).commit()
            }else {
                with(NotificationManagerCompat.from(this)) {
                    cancelAll()
                }
                sharedPref.edit().putBoolean(getString(R.string.option_statusBar), false).commit()
            }
        })

        //AutoExecution
        layout_autoexec.setOnClickListener(View.OnClickListener {
            switch_autoexec.isChecked = !switch_autoexec.isChecked
        })
        switch_autoexec.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Toast.makeText(applicationContext, getString(R.string.alert_message_checkAutoexceution), Toast.LENGTH_LONG).show()
                sharedPref.edit().putBoolean(getString(R.string.option_autoExecution), true).commit()
            }else {
                sharedPref.edit().putBoolean(getString(R.string.option_autoExecution), false).commit()
            }
        })


        val billingManager = BillingManager(this)
        billingManager.connectBillingClient()
        layout_purchase.setOnClickListener(View.OnClickListener {
            billingManager.processToPurchase()  //playstore client 설정
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("test001", "call onActivity Result")
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setAdView() {
        val sharedPref = this.getSharedPreferences(getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
        if(sharedPref.getBoolean(getString(R.string.option_windowShortcut), false) == false) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            adView.visibility = View.VISIBLE
        }
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