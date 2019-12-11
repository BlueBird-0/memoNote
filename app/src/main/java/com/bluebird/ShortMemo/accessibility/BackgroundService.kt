package com.bluebird.ShortMemo.accessibility

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class BackgroundService : Service() {
    val receiver = BroadcastReceiverApp()
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Test001_service", "service start")

        try {
            unregisterReceiver(receiver)
        }catch(e : IllegalArgumentException) {}

        registerReceiver(receiver, IntentFilter(Intent.ACTION_SCREEN_ON))
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        }catch (e : IllegalArgumentException){}
    }
}