package com.bluebird.ShortMemo.accessibility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bluebird.ShortMemo.*


class FunNotification {
    companion object {
        val ACTION_DELETE : String = "NotificationDeleted"
        val CHANNEL_ID = "ChannelID"
        val GROUP_ID = "GroupID"


        fun notification(context: Context): NotificationCompat.Builder {
            var intent = Intent(context, MainActivity::class.java)
            var pendingIntent = PendingIntent.getActivity(context, 1, intent, 0)


            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_app)
                    .setContentTitle(context.getString(R.string.str_statusbar))
                    //.setContentText("textContent")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(false)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true)
                    //.setNumber(10) //알람 개수

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "ChannelName"
                val descriptionText = "ChannelDiscription"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    setSound(null, null)
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            //DeleteEvent
            var deleteIntent = Intent(context, BroadcastReceiverApp::class.java)
            deleteIntent.action = ACTION_DELETE
            var deletePendingIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0)
            builder.setDeleteIntent(deletePendingIntent)

            return builder
        }

        fun notificationNote(context: Context, note:Note): NotificationCompat.Builder {
            var intent = Intent(context, MainActivity::class.java)
            var pendingIntent = PendingIntent.getActivity(context, 1, intent, 0)


            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    //.setContentTitle(context.getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_app)
                    .setContentText(note.content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setWhen(note.createdTime!!.time)// 시간 체크
                    .setGroup(GROUP_ID)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "ChannelName"
                val descriptionText = "ChannelDiscription"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    setSound(null, null)
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

            }

            //DeleteEvent
            var deleteIntent = Intent(context, BroadcastReceiverApp::class.java)
            deleteIntent.setAction(ACTION_DELETE)
            var deletePendingIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0)
            builder.setDeleteIntent(deletePendingIntent)

            return builder
        }

    }


}