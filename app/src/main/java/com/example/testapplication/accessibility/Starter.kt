package com.example.testapplication.accessibility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.testapplication.MainActivity

class Starter : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("test001", "on receive")
        var action = intent?.action
        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            var intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }

        //부팅시
//        if(action.equals("android.intent.action.BOOT_COMPLETED")) {
//            var intent = Intent(context, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context?.startActivity(intent)
//        }
    }
}