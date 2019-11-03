package com.example.ShortMemo.accessibility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.ShortMemo.MainActivity
import com.example.ShortMemo.R

class BroadcastReceiverApp : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("Test001_Broadcast", "Called BroadcastReceiverApp! set Start Service  action:[ " + intent?.action+"]")

        val sharedPref = context.getSharedPreferences(context.getString(R.string.USER_SETTINGS_PREF), Context.MODE_PRIVATE)
        val action = intent?.action

        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            //TODO 자동 실행 체크확인, AutoExecution
            if(sharedPref.getBoolean(context.getString(R.string.option_autoExecution), false) == false) return

            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
        else if(action.equals("NotificationDeleted")) {
            //TODO 상태 바 체크 확인
            if(sharedPref.getBoolean(context.getString(R.string.option_statusBar), false) == false) return

            with(NotificationManagerCompat.from(context)) {
                //notify(100685, FunNotification.notificationNote(context).build())
                //notify(100686, FunNotification.notificationNote(context).build())
                notify(100687, FunNotification.notification(context).build())
            }
        }

    }
}