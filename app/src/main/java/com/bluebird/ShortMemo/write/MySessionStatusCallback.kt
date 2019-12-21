package com.bluebird.ShortMemo.write

import android.util.Log
import android.widget.Toast
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException


class MySessionStatusCallback : ISessionCallback {
    override fun onSessionOpened() {
//        requestMe()
        //redirectSignupActivity()//세션 연결 성공 시
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        //if(exception != null) {
        //  Logger.e(exception)
        //}
        //setContentView(R.layout.activity_login)
    }

    fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                Log.d("test001", "user Respon : " + result)
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("test001", "user Error : " + errorResult)
            }
        })
    }


}
