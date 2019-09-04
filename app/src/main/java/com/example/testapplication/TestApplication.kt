package com.example.testapplication

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


class TestApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
