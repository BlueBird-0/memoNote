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
                setOnClickPendingIntent(R.id.button4, pendingIntent)

                setRemoteAdapter(R.id.widgetListView, remoteIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}