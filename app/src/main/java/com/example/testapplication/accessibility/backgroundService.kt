package com.example.testapplication.accessibility

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class backgroundService : Service() {
    var receiver = Starter()
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("test001", "service start")
        registerReceiver(receiver, IntentFilter(Intent.ACTION_SCREEN_ON))
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}