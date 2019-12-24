package com.bluebird.ShortMemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.Session

class KakaoLoginActivity : AppCompatActivity() {
    companion object {
        lateinit var session : Session
        lateinit var mySessionCallback : MySessionStatusCallback
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("test001", "Kakao Login Activity OnCreate")
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)


        //testcode
        session = Session.getCurrentSession()
        mySessionCallback = MySessionStatusCallback()
        session.addCallback(mySessionCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        session.removeCallback(mySessionCallback)
    }

    override fun onResume() {
        super.onResume()
        Log.d("test001", "session onResume")
        if(session.isClosed() ) {
            //logout state
        }else {
            // login state
            if(session.isOpenable()) {
                session.checkAndImplicitOpen()
                Log.d("test001", "session opend")
            }
        }

    }
}
