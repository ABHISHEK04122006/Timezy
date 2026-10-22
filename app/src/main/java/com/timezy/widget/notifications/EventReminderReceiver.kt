package com.timezy.widget.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.timezy.widget.CountdownApplication
import com.timezy.widget.MainActivity
import com.timezy.widget.R
import com.timezy.widget.data.database.AppDatabase
import com.timezy.widget.data.model.CountdownEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = try { goAsync() } catch (_: Throwable) { null }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Reschedule all active reminders on system restart
                    ReminderScheduler.rescheduleAllSync(context)
                    com.timezy.widget.widget.WidgetUpdateHelper.updateAllWidgets(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult?.finish()
                }
            }
            return
        }

        val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        if (eventId == -1L) return

        val pendingResult = try { goAsync() } catch (_: Throwable) { null }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getInstance(context)
                val eventEntity = database.eventDao().getEventByIdSync(eventId) ?: return@launch
                val event = eventEntity.toDomain()

                val appPreferences = (context.applicationContext as? CountdownApplication)?.preferences
                if (appPreferences != null && !appPreferences.isNotificationsEnabled()) {
                    return@launch
                }

                if (!event.isNotificationEnabled) return@launch

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val contentIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(MainActivity.EXTRA_NAVIGATE_TO_EVENT_ID, eventId)
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    eventId.toInt(),
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = buildNotification(context, event, pendingIntent).build()

                notificationManager.notify(eventId.toInt(), notification)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult?.finish()
            }
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"

        /**
         * Resolves the notification title and body text for an event:
         * - Title is the actual event name (no redundant prefix/suffix).
         * - Body is the saved event description if available.
         * - If description is empty, falls back to remaining time (e.g. "Today!", "Tomorrow", "44 days left") or null.
         */
        fun getNotificationContent(event: CountdownEvent, nowMillis: Long = System.currentTimeMillis()): Pair<String, String?> {
            val title = event.title.trim()
            val description = event.description.trim()

            val body = if (description.isNotEmpty()) {
                description
            } else {
                val remaining = event.calculateRemainingTime(nowMillis)
                when {
                    remaining.isToday -> "Today!"
                    remaining.isTomorrow -> "Tomorrow"
                    remaining.calendarDays > 0 -> remaining.displayDaysText
                    else -> null
                }
            }

            return Pair(title, body)
        }

        /**
         * Builds the NotificationCompat.Builder for an event reminder:
         * - Small icon: Countdown app logo monochrome vector drawable (ic_notification) on the left
         * - Large icon / right-side image: completely removed per user request
         * - Title: actual event name
         * - Text/Body: saved event description (or sensible remaining-time fallback if description empty)
         * - Tap action: opens the specific event in MainActivity
         */
        fun buildNotification(
            context: Context,
            event: CountdownEvent,
            pendingIntent: PendingIntent,
            nowMillis: Long = System.currentTimeMillis()
        ): NotificationCompat.Builder {
            val (title, body) = getNotificationContent(event, nowMillis)

            val builder = NotificationCompat.Builder(context, CountdownApplication.CHANNEL_REMINDERS_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)

            if (!body.isNullOrBlank()) {
                builder.setContentText(body)
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(body))
            }

            return builder
        }
    }
}
