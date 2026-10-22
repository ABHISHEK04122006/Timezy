package com.timezy.widget.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WidgetPinReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_PIN_SUCCESS) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val eventId = intent.getLongExtra(EXTRA_PINNED_EVENT_ID, -1L)

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && eventId != -1L) {
                WidgetUpdateHelper.setWidgetEventId(context, appWidgetId, eventId)

                val themeId = intent.getStringExtra(EXTRA_PINNED_THEME_ID)
                if (themeId != null) {
                    WidgetUpdateHelper.setWidgetColorTheme(context, appWidgetId, com.timezy.widget.data.model.EventTheme.fromId(themeId))
                }

                val styleName = intent.getStringExtra(EXTRA_PINNED_STYLE)
                if (styleName != null) {
                    WidgetUpdateHelper.setWidgetStyle(context, appWidgetId, com.timezy.widget.data.model.CircleStyle.fromName(styleName))
                }

                val appWidgetManager = AppWidgetManager.getInstance(context)
                WidgetUpdateHelper.updateCircleWidget(context, appWidgetManager, appWidgetId)
                WidgetUpdateHelper.updateMediumWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    companion object {
        const val ACTION_PIN_SUCCESS = "com.timezy.widget.ACTION_PIN_SUCCESS"
        const val EXTRA_PINNED_EVENT_ID = "extra_pinned_event_id"
        const val EXTRA_PINNED_THEME_ID = "extra_pinned_theme_id"
        const val EXTRA_PINNED_STYLE = "extra_pinned_style"
    }
}
