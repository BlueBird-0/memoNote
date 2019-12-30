package com.bluebird.ShortMemo

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException


class MySessionStatusCallback(var context : Context) : ISessionCallback {
    override fun onSessionOpened() {
        Log.d("test001", "kakao Login Session Opend")
        requestMe()
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        Log.d("test001", "kakao Login Session Failed : " + exception)
        //if(exception != null) {
        //  Logger.e(exception)
        //}
        //setContentView(R.layout.activity_login)
    }



    fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
//                (context as AppCompatActivity).findViewById<ConstraintLayout>(R.id.layout_kakao).setBackgroundResource(R.color.colorAccent)


                Log.d("test001", "user Respon : " + result)
                val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
                sharedPref.edit().putBoolean(context.getString(R.string.option_kakaoLogined), true).commit()
                sharedPref.edit().putString(context.getString(R.string.option_kakaoId), result?.id.toString()).commit()
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("test001", "user Error : " + errorResult)
                val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
                sharedPref.edit().putBoolean(context.getString(R.string.option_kakaoLogined), false).commit()
                sharedPref.edit().putString(context.getString(R.string.option_kakaoId), null).commit()
            }
        })
    }
}
