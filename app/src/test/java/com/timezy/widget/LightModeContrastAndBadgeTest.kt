package com.timezy.widget

import androidx.compose.ui.graphics.Color
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.ui.theme.LightBackground
import com.timezy.widget.ui.theme.LightCard
import com.timezy.widget.ui.theme.LightMutedText
import com.timezy.widget.ui.theme.LightPrimaryAccent
import com.timezy.widget.ui.theme.LightPrimaryText
import com.timezy.widget.ui.theme.LightSecondaryText
import com.timezy.widget.ui.theme.LightSubtleBorder
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class LightModeContrastAndBadgeTest {

    @Test
    fun testLightModePaletteTokens() {
        // Suggested Light Mode palette:
        // Main background: #F6F7FB
        // Card background: #FFFFFF
        // Primary text: #171923
        // Secondary text: #606575
        // Muted text: #747B8A
        // Borders: #E0E5EE
        // Primary accent: #C28A2C
        assertEquals(Color(0xFFF6F7FB), LightBackground)
        assertEquals(Color(0xFFFFFFFF), LightCard)
        assertEquals(Color(0xFF171923), LightPrimaryText)
        assertEquals(Color(0xFF606575), LightSecondaryText)
        assertEquals(Color(0xFF747B8A), LightMutedText)
        assertEquals(Color(0xFFE0E5EE), LightSubtleBorder)
        assertEquals(Color(0xFFC28A2C), LightPrimaryAccent)
    }

    @Test
    fun testBadgeSingularAndPluralDaysLeft() {
        val now = LocalDate.of(2026, 10, 22)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // 1 Day Left
        val eventTomorrow = CountdownEvent(
            id = 1L,
            title = "Tomorrow Event",
            targetDateTimeEpochMillis = LocalDate.of(2026, 10, 23)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            category = EventCategory.BIRTHDAY,
            theme = EventTheme.NEON_CYAN,
            recurrence = RecurrenceType.NONE,
            createdAtEpochMillis = now
        )
        val remaining1 = eventTomorrow.calculateRemainingTime(now)
        assertEquals(1, remaining1.calendarDays)
        assertEquals("1", remaining1.primaryNumberText)
        assertEquals("DAY LEFT", remaining1.primaryLabelText)

        // 43 Days Left
        val event43 = CountdownEvent(
            id = 2L,
            title = "Future Event",
            targetDateTimeEpochMillis = LocalDate.of(2026, 12, 4)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            category = EventCategory.BIRTHDAY,
            theme = EventTheme.NEON_CYAN,
            recurrence = RecurrenceType.NONE,
            createdAtEpochMillis = now
        )
        val remaining43 = event43.calculateRemainingTime(now)
        assertEquals(43, remaining43.calendarDays)
        assertEquals("43", remaining43.primaryNumberText)
        assertEquals("DAYS LEFT", remaining43.primaryLabelText)
    }

    @Test
    fun testBadgeSingularAndPluralDaysAgo() {
        val now = LocalDate.of(2026, 10, 22)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // 1 Day Ago
        val eventYesterday = CountdownEvent(
            id = 3L,
            title = "Yesterday Event",
            targetDateTimeEpochMillis = LocalDate.of(2026, 10, 21)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            category = EventCategory.HOLIDAY,
            theme = EventTheme.NEON_CYAN,
            recurrence = RecurrenceType.NONE,
            createdAtEpochMillis = now - 86400000L * 2
        )
        val remainingYesterday = eventYesterday.calculateRemainingTime(now)
        assertEquals(-1, remainingYesterday.calendarDays)
        assertEquals("1", remainingYesterday.primaryNumberText)
        assertEquals("DAY AGO", remainingYesterday.primaryLabelText)
        assertEquals("1 day ago", remainingYesterday.displayDaysText)

        // 5 Days Ago
        val event5Ago = CountdownEvent(
            id = 4L,
            title = "Past Event",
            targetDateTimeEpochMillis = LocalDate.of(2026, 10, 17)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            category = EventCategory.HOLIDAY,
            theme = EventTheme.NEON_CYAN,
            recurrence = RecurrenceType.NONE,
            createdAtEpochMillis = now - 86400000L * 10
        )
        val remaining5Ago = event5Ago.calculateRemainingTime(now)
        assertEquals(-5, remaining5Ago.calendarDays)
        assertEquals("5", remaining5Ago.primaryNumberText)
        assertEquals("DAYS AGO", remaining5Ago.primaryLabelText)
        assertEquals("5 days ago", remaining5Ago.displayDaysText)
    }

    @Test
    fun testTodayBadge() {
        val now = LocalDate.of(2026, 10, 22)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val eventToday = CountdownEvent(
            id = 5L,
            title = "Today Event",
            targetDateTimeEpochMillis = now,
            category = EventCategory.TRAVEL,
            theme = EventTheme.NEON_CYAN,
            recurrence = RecurrenceType.NONE,
            createdAtEpochMillis = now
        )
        val remainingToday = eventToday.calculateRemainingTime(now)
        assertEquals(0, remainingToday.calendarDays)
        assertEquals("0", remainingToday.primaryNumberText)
        assertEquals("TODAY", remainingToday.primaryLabelText)
        assertEquals("Today!", remainingToday.displayDaysText)
    }
}
