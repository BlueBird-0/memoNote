package com.bluebird.ShortMemo

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.kakao.auth.*


class TestApplication : MultiDexApplication() {
    companion object {
        lateinit var instance : TestApplication

        fun getGlobalApplicationContext() : TestApplication {
            if(instance == null) {
                throw IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
            }
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("test001", "init test")
        instance = this
        KakaoSDK.init(KakaoSDKAdapter())
//        Session.getCurrentSession()
    }
}

private class KakaoSDKAdapter : KakaoAdapter() {
    /**
     * Session Config에 대해서는 default값들이 존재한다.
     * 필요한 상황에서만 override해서 사용하면 됨.
     * @return Session의 설정값.
     */
    override fun getSessionConfig(): ISessionConfig {
        return object : ISessionConfig {
            override fun getAuthTypes(): Array<AuthType> {
                return arrayOf(AuthType.KAKAO_LOGIN_ALL)
            }

            override fun isUsingWebviewTimer(): Boolean {
                return false
            }

            override fun isSecureMode(): Boolean {
                return false
            }

            override fun getApprovalType(): ApprovalType? {
                return ApprovalType.INDIVIDUAL
            }

            override fun isSaveFormData(): Boolean {
                return true
            }
        }
    }

    override fun getApplicationConfig(): IApplicationConfig {
        return IApplicationConfig { TestApplication.getGlobalApplicationContext() }
    }
}