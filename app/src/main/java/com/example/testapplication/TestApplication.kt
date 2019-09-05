package com.example.testapplication

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class TestApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        MultiDex.install(this)
    }
}