package com.timezy.widget.data.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class CountdownEvent(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val targetDateTimeEpochMillis: Long,
    val hasSpecificTime: Boolean = false,
    val category: EventCategory = EventCategory.OTHER,
    val theme: EventTheme = EventTheme.NEON_CYAN,
    val isFavorite: Boolean = false,
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val reminderMinutesBefore: Int = 1440, // 1 day before by default
    val isNotificationEnabled: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) {
    /**
     * Calculates the effective next target time, handling recurrence (yearly, monthly, weekly, daily).
     */
    fun getEffectiveTargetEpochMillis(nowMillis: Long = System.currentTimeMillis()): Long {
        if (recurrence == RecurrenceType.NONE) {
            return targetDateTimeEpochMillis
        }

        val zoneId = ZoneId.systemDefault()
        val originalTarget = LocalDateTime.ofInstant(Instant.ofEpochMilli(targetDateTimeEpochMillis), zoneId)
        val now = LocalDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), zoneId)

        var nextOccurrence = originalTarget

        when (recurrence) {
            RecurrenceType.DAILY -> {
                while (nextOccurrence.isBefore(now)) {
                    nextOccurrence = nextOccurrence.plusDays(1)
                }
            }
            RecurrenceType.WEEKLY -> {
                while (nextOccurrence.isBefore(now)) {
                    nextOccurrence = nextOccurrence.plusWeeks(1)
                }
            }
            RecurrenceType.MONTHLY -> {
                while (nextOccurrence.isBefore(now)) {
                    nextOccurrence = nextOccurrence.plusMonths(1)
                }
            }
            RecurrenceType.YEARLY -> {
                // Adjust to this year or next year, correctly handling leap years (e.g. Feb 29)
                var year = now.year
                var candidate = try {
                    originalTarget.withYear(year)
                } catch (e: Exception) {
                    // Leap year edge case (Feb 29 -> Feb 28 if non-leap)
                    originalTarget.withDayOfMonth(28).withYear(year)
                }

                if (candidate.isBefore(now)) {
                    year++
                    candidate = try {
                        originalTarget.withYear(year)
                    } catch (e: Exception) {
                        originalTarget.withDayOfMonth(28).withYear(year)
                    }
                }
                nextOccurrence = candidate
            }
            RecurrenceType.NONE -> {
                nextOccurrence = originalTarget
            }
        }

        return nextOccurrence.atZone(zoneId).toInstant().toEpochMilli()
    }

    fun isPast(nowMillis: Long = System.currentTimeMillis()): Boolean {
        if (!hasSpecificTime) {
            val zone = ZoneId.systemDefault()
            val targetDate = Instant.ofEpochMilli(getEffectiveTargetEpochMillis(nowMillis)).atZone(zone).toLocalDate()
            val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
            return targetDate.isBefore(today)
        }
        return getEffectiveTargetEpochMillis(nowMillis) < nowMillis
    }

    fun isToday(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val zone = ZoneId.systemDefault()
        val targetDate = Instant.ofEpochMilli(getEffectiveTargetEpochMillis(nowMillis)).atZone(zone).toLocalDate()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return targetDate.isEqual(today)
    }

    fun isTomorrow(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val zone = ZoneId.systemDefault()
        val targetDate = Instant.ofEpochMilli(getEffectiveTargetEpochMillis(nowMillis)).atZone(zone).toLocalDate()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return targetDate.isEqual(today.plusDays(1))
    }

    /**
     * Complete remaining time breakdown.
     */
    fun calculateRemainingTime(nowMillis: Long = System.currentTimeMillis()): TimeRemaining {
        val effectiveTarget = getEffectiveTargetEpochMillis(nowMillis)
        val diffMillis = effectiveTarget - nowMillis
        val past = isPast(nowMillis)
        val absDiff = abs(diffMillis)

        val totalSeconds = absDiff / 1000
        val seconds = (totalSeconds % 60).toInt()
        val totalMinutes = totalSeconds / 60
        val minutes = (totalMinutes % 60).toInt()
        val totalHours = totalMinutes / 60
        val hours = (totalHours % 24).toInt()
        val days = (totalHours / 24).toInt()

        val zone = ZoneId.systemDefault()
        val targetDate = Instant.ofEpochMilli(effectiveTarget).atZone(zone).toLocalDate()
        val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val calendarDays = ChronoUnit.DAYS.between(nowDate, targetDate).toInt()

        return TimeRemaining(
            isPast = past,
            days = days,
            calendarDays = calendarDays,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            totalMillis = absDiff,
            isToday = targetDate.isEqual(nowDate),
            isTomorrow = targetDate.isEqual(nowDate.plusDays(1))
        )
    }
}

data class TimeRemaining(
    val isPast: Boolean,
    val days: Int,
    val calendarDays: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalMillis: Long,
    val isToday: Boolean,
    val isTomorrow: Boolean
) {
    val displayDaysText: String
        get() = when {
            isToday -> "Today!"
            isTomorrow -> "Tomorrow"
            isPast -> if (abs(calendarDays) == 1) "1 day ago" else "${abs(calendarDays)} days ago"
            calendarDays == 1 -> "1 day left"
            else -> "$calendarDays days left"
        }

    val primaryNumberText: String
        get() = when {
            isToday -> "0"
            else -> abs(calendarDays).toString()
        }

    val primaryLabelText: String
        get() = when {
            isToday -> "TODAY"
            abs(calendarDays) == 1 -> if (isPast) "DAY AGO" else "DAY LEFT"
            else -> if (isPast) "DAYS AGO" else "DAYS LEFT"
        }
}
