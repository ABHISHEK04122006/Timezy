package com.timezy.widget

import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.notifications.EventReminderReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.Instant

class EventNotificationDesignTest {

    @Test
    fun testNotificationWithDescriptionDisplaysEventTitleAndDescription() {
        val now = Instant.parse("2026-10-22T10:00:00Z").toEpochMilli()
        val targetMillis = now + (3L * 86400L * 1000L) // 3 days in future

        val event = CountdownEvent(
            id = 101L,
            title = "Happy Birthday",
            description = "Make this special day unforgettable!",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.OTHER,
            theme = EventTheme.SUNSET_EMBER
        )

        val (title, body) = EventReminderReceiver.getNotificationContent(event, now)

        // 1. Notification title must be the exact event name
        assertEquals("Happy Birthday", title)

        // 2. Notification body must be the saved event description
        assertEquals("Make this special day unforgettable!", body)

        // 3. Must not contain generic or redundant text
        assertFalse(body?.contains("Today is the big day") ?: false)
    }

    @Test
    fun testNotificationWithEmptyDescriptionDisplaysSensibleFallback() {
        val now = Instant.parse("2026-10-22T10:00:00Z").toEpochMilli()

        // Case 1: Event 5 days away with empty description
        val eventFuture = CountdownEvent(
            id = 102L,
            title = "Trip to Tokyo",
            description = "",
            targetDateTimeEpochMillis = now + (5L * 86400L * 1000L),
            hasSpecificTime = false,
            category = EventCategory.TRAVEL,
            theme = EventTheme.EMERALD_MINT
        )
        val (titleFuture, bodyFuture) = EventReminderReceiver.getNotificationContent(eventFuture, now)
        assertEquals("Trip to Tokyo", titleFuture)
        assertEquals("5 days left", bodyFuture)

        // Case 2: Event tomorrow with empty description
        val eventTomorrow = CountdownEvent(
            id = 103L,
            title = "Dentist Appointment",
            description = "   ",
            targetDateTimeEpochMillis = now + (1L * 86400L * 1000L),
            hasSpecificTime = false,
            category = EventCategory.OTHER,
            theme = EventTheme.NEON_CYAN
        )
        val (titleTomorrow, bodyTomorrow) = EventReminderReceiver.getNotificationContent(eventTomorrow, now)
        assertEquals("Dentist Appointment", titleTomorrow)
        assertEquals("Tomorrow", bodyTomorrow)

        // Case 3: Event today with empty description
        val eventToday = CountdownEvent(
            id = 104L,
            title = "Conference Day",
            description = "",
            targetDateTimeEpochMillis = now,
            hasSpecificTime = false,
            category = EventCategory.CAREER,
            theme = EventTheme.COSMIC_PURPLE
        )
        val (titleToday, bodyToday) = EventReminderReceiver.getNotificationContent(eventToday, now)
        assertEquals("Conference Day", titleToday)
        assertEquals("Today!", bodyToday)
    }

    @Test
    fun testNotificationWithWhitespaceTrimming() {
        val now = Instant.parse("2026-10-22T10:00:00Z").toEpochMilli()
        val event = CountdownEvent(
            id = 105L,
            title = "   Anniversary Dinner   ",
            description = "   Table reservation at 8:00 PM for two   ",
            targetDateTimeEpochMillis = now + (2L * 86400L * 1000L),
            hasSpecificTime = true
        )

        val (title, body) = EventReminderReceiver.getNotificationContent(event, now)
        assertEquals("Anniversary Dinner", title)
        assertEquals("Table reservation at 8:00 PM for two", body)
    }

    @Test
    fun testNotificationMultiLineDescriptionPreserved() {
        val now = Instant.parse("2026-10-22T10:00:00Z").toEpochMilli()
        val multiLineDesc = "First line of notes\nSecond line with location details\nThird line"
        val event = CountdownEvent(
            id = 106L,
            title = "Design Sprint",
            description = multiLineDesc,
            targetDateTimeEpochMillis = now + (4L * 86400L * 1000L),
            hasSpecificTime = true
        )

        val (title, body) = EventReminderReceiver.getNotificationContent(event, now)
        assertEquals("Design Sprint", title)
        assertEquals(multiLineDesc, body)
    }
}
