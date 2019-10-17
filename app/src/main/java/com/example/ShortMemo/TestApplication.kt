package com.example.ShortMemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class TestApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        MultiDex.install(this)
    }
}