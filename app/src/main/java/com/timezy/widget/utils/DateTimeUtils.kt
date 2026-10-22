package com.timezy.widget.utils

import com.timezy.widget.data.model.CountdownEvent
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateTimeUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    private val dateShortFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
    private val time12Formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    private val time24Formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val fullDateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    fun formatDate(epochMillis: Long): String {
        val localDate = Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return localDate.format(dateFormatter)
    }

    fun formatDateShort(epochMillis: Long): String {
        val localDate = Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return localDate.format(dateShortFormatter)
    }

    fun formatTime(epochMillis: Long, is24Hour: Boolean = false): String {
        val localTime = Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
        return if (is24Hour) localTime.format(time24Formatter) else localTime.format(time12Formatter)
    }

    fun formatFullDateTime(epochMillis: Long): String {
        val localDateTime = Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return localDateTime.format(fullDateTimeFormatter)
    }

    fun toEpochMillis(date: LocalDate, time: LocalTime? = null): Long {
        val zone = ZoneId.systemDefault()
        return if (time != null) {
            date.atTime(time).atZone(zone).toInstant().toEpochMilli()
        } else {
            date.atStartOfDay(zone).toInstant().toEpochMilli()
        }
    }

    fun toLocalDate(epochMillis: Long): LocalDate {
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun toLocalTime(epochMillis: Long): LocalTime {
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalTime()
    }

    /**
     * Relative friendly status string: e.g. "Today!", "Tomorrow", "In 15 days", "3 days ago"
     */
    fun getRelativeStatus(event: CountdownEvent, nowMillis: Long = System.currentTimeMillis()): String {
        val remaining = event.calculateRemainingTime(nowMillis)
        return when {
            remaining.isToday -> "Today!"
            remaining.isTomorrow -> "Tomorrow"
            remaining.isPast -> "${Math.abs(remaining.calendarDays)} days ago"
            remaining.calendarDays == 1 -> "1 day left"
            else -> "${remaining.calendarDays} days left"
        }
    }

    /**
     * Calculates progress between creation and target time (0.0 to 1.0)
     */
    fun calculateProgress(
        createdEpochMillis: Long,
        targetEpochMillis: Long,
        nowEpochMillis: Long = System.currentTimeMillis()
    ): Float {
        if (targetEpochMillis <= createdEpochMillis) return 1.0f
        val totalSpan = (targetEpochMillis - createdEpochMillis).toFloat()
        val elapsed = (nowEpochMillis - createdEpochMillis).toFloat()
        return (elapsed / totalSpan).coerceIn(0.0f, 1.0f)
    }
}
