package com.example.ShortMemo.accessibility

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.ShortMemo.MainActivity
import com.example.ShortMemo.R

class WidgetProvider : AppWidgetProvider() {
    companion object {
        const val ACTION_WRITE = "actionWriteFromWidget"
        const val EXTRA_WRITE_POS = "call_writeactivity_position"
        const val ACTION_CHECK = "actionCheckFromWidget"
        const val EXTRA_CHECK_POS = "call_check_position"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId->
            Log.d("test001", "Widget : called onUpdate")
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                    .let { intent -> PendingIntent.getActivity(context, 0, intent, 0)
                    }
            val remoteIntent : Intent = Intent(context, WidgetRemoteViewsService::class.java)

            val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.layout_widget
            ).apply {
                setOnClickPendingIntent(R.id.widget_write, pendingIntent)
                setRemoteAdapter(R.id.widgetListView, remoteIntent)


                val intent = Intent(context, BroadcastReceiverApp::class.java)
                var listClickPendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                this.setPendingIntentTemplate(R.id.widgetListView, listClickPendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d("test001", "lllllllllllllllllllllllllllllllllllllllllllllllllll")
    }
}