package com.timezy.widget

import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.widget.WidgetUpdateHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class WidgetCountdownTimerTest {

    private val zone = ZoneId.systemDefault()

    @Test
    fun testDistantEventSubDayTimerFormatting() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        // 1466 days, 2 hours, 44 minutes, 18 seconds in future
        val durationMillis = (1466L * 86400L + 2L * 3600L + 44L * 60L + 18L) * 1000L
        val targetMillis = now + durationMillis

        val event = CountdownEvent(
            id = 1L,
            title = "Tesla roadster",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.OTHER,
            theme = EventTheme.NEON_CYAN
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(1466, remaining.calendarDays)
        assertEquals(2, remaining.hours)
        assertEquals(44, remaining.minutes)
        assertEquals(18, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("2h 44m 18s left", formattedTimer)
    }

    @Test
    fun testEventMinutesAway() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        val targetMillis = now + (5L * 60L + 30L) * 1000L // 5m 30s away

        val event = CountdownEvent(
            id = 2L,
            title = "Quick Meeting",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.CAREER,
            theme = EventTheme.EMERALD_MINT
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(0, remaining.calendarDays)
        assertEquals(0, remaining.hours)
        assertEquals(5, remaining.minutes)
        assertEquals(30, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("5m 30s left", formattedTimer)
    }

    @Test
    fun testEventSecondsAway() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        val targetMillis = now + 18L * 1000L // 18s away

        val event = CountdownEvent(
            id = 3L,
            title = "Rocket Launch",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.OTHER,
            theme = EventTheme.SUNSET_EMBER
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(18, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("18s left", formattedTimer)
    }

    @Test
    fun testZeroCountdownBoundary() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        val targetMillis = now // exact match

        val event = CountdownEvent(
            id = 4L,
            title = "Now Event",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.OTHER,
            theme = EventTheme.COSMIC_PURPLE
        )

        val remaining = event.calculateRemainingTime(now)
        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("0s left", formattedTimer)
    }

    @Test
    fun testExpiredEventFormatting() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        val targetMillis = now - (2L * 3600L + 15L * 60L + 10L) * 1000L // 2h 15m 10s ago

        val event = CountdownEvent(
            id = 5L,
            title = "Past Event",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.OTHER,
            theme = EventTheme.ROSE_VELVET
        )

        val remaining = event.calculateRemainingTime(now)
        assertTrue(remaining.isPast)
        assertEquals(2, remaining.hours)
        assertEquals(15, remaining.minutes)
        assertEquals(10, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("2h 15m 10s ago", formattedTimer)
    }

    @Test
    fun testDateOnlyTodayEvent() {
        val nowDate = LocalDate.now(zone)
        val nowMillis = nowDate.atTime(LocalTime.of(14, 30)).atZone(zone).toInstant().toEpochMilli()
        val targetMillis = nowDate.atStartOfDay(zone).toInstant().toEpochMilli()

        val event = CountdownEvent(
            id = 6L,
            title = "Today All-Day Event",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = false,
            category = EventCategory.HOLIDAY,
            theme = EventTheme.EMERALD_MINT
        )

        val remaining = event.calculateRemainingTime(nowMillis)
        assertTrue(remaining.isToday)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, nowMillis)
        assertEquals("Today!", formattedTimer)
    }

    @Test
    fun testSingleDigitSecondsFormatting() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        // 44 days, 3 hours, 37 minutes, 6 seconds in future
        val durationMillis = (44L * 86400L + 3L * 3600L + 37L * 60L + 6L) * 1000L
        val targetMillis = now + durationMillis

        val event = CountdownEvent(
            id = 7L,
            title = "Happy birthday",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            category = EventCategory.BIRTHDAY,
            theme = EventTheme.NEON_CYAN
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(44, remaining.calendarDays)
        assertEquals(3, remaining.hours)
        assertEquals(37, remaining.minutes)
        assertEquals(6, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("3h 37m 06s left", formattedTimer)
    }

    @Test
    fun testSeparateColorsPerWidgetInstance() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 101
            val widgetB = 102

            // 1. Set Widget A to Blue
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetA, EventTheme.NEON_CYAN)

            // 2. Set Widget B to Purple
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.COSMIC_PURPLE)

            // 3. Confirm Widget A remains Blue while Widget B is Purple
            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.COSMIC_PURPLE, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))

            // 4. Change Widget B to Green
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.EMERALD_MINT)

            // 5. Confirm Widget A remains Blue and Widget B becomes Green
            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testPersistenceAcrossAppRestarts() {
        val sharedStorage = mutableMapOf<String, Any?>()
        val firstSessionPrefs = FakeSharedPreferences(sharedStorage)
        WidgetUpdateHelper.prefsOverrideForTesting = firstSessionPrefs

        try {
            val widgetA = 101
            val widgetB = 102

            WidgetUpdateHelper.setWidgetColorTheme(null, widgetA, EventTheme.NEON_CYAN)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.EMERALD_MINT)

            // 6. Simulate app restart by initializing a new SharedPreferences over same persistence storage
            val restartSessionPrefs = FakeSharedPreferences(sharedStorage)
            WidgetUpdateHelper.prefsOverrideForTesting = restartSessionPrefs

            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testEditingEventDoesNotChangeUnrelatedWidgetColor() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 101
            val widgetB = 102

            WidgetUpdateHelper.setWidgetEventId(null, widgetA, 10L)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetA, EventTheme.NEON_CYAN)

            WidgetUpdateHelper.setWidgetEventId(null, widgetB, 20L)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.COSMIC_PURPLE)

            // 7. Edit the selected event in Widget A
            WidgetUpdateHelper.setWidgetEventId(null, widgetA, 99L)

            // Confirm Widget A has updated event and retains Blue
            assertEquals(99L, WidgetUpdateHelper.getWidgetEventId(null, widgetA))
            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))

            // Confirm Widget B remains event 20 and Purple
            assertEquals(20L, WidgetUpdateHelper.getWidgetEventId(null, widgetB))
            assertEquals(EventTheme.COSMIC_PURPLE, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testWidgetDeletionCleansUpPreferences() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 101
            val widgetB = 102

            WidgetUpdateHelper.setWidgetEventId(null, widgetA, 10L)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetA, EventTheme.NEON_CYAN)
            WidgetUpdateHelper.setWidgetStyle(null, widgetA, com.timezy.widget.data.model.CircleStyle.SMOKED_GLASS)

            WidgetUpdateHelper.setWidgetEventId(null, widgetB, 20L)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.SUNSET_EMBER)

            // Delete Widget A
            WidgetUpdateHelper.deleteWidgetPref(null, widgetA)

            // Verify Widget A cleaned up
            assertEquals(-1L, WidgetUpdateHelper.getWidgetEventId(null, widgetA))
            org.junit.Assert.assertFalse(WidgetUpdateHelper.hasWidgetColorTheme(null, widgetA))

            // Verify Widget B remains completely intact
            assertEquals(20L, WidgetUpdateHelper.getWidgetEventId(null, widgetB))
            assertEquals(EventTheme.SUNSET_EMBER, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testAllFiveThemesSupported() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val themes = listOf(
                EventTheme.NEON_CYAN,     // Blue
                EventTheme.COSMIC_PURPLE,  // Purple
                EventTheme.EMERALD_MINT,   // Green
                EventTheme.SUNSET_EMBER,   // Orange
                EventTheme.ROSE_VELVET     // Pink
            )

            themes.forEachIndexed { index, theme ->
                val widgetId = 200 + index
                WidgetUpdateHelper.setWidgetColorTheme(null, widgetId, theme)
                assertEquals(theme, WidgetUpdateHelper.getWidgetColorTheme(null, widgetId))
            }
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testPerWidgetStyleAndSecondsSeparation() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 101
            val widgetB = 102

            WidgetUpdateHelper.setWidgetStyle(null, widgetA, com.timezy.widget.data.model.CircleStyle.SMOKED_GLASS)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetA, false)

            WidgetUpdateHelper.setWidgetStyle(null, widgetB, com.timezy.widget.data.model.CircleStyle.PURE_WHITE)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetB, true)

            assertEquals(com.timezy.widget.data.model.CircleStyle.SMOKED_GLASS, WidgetUpdateHelper.getWidgetCircleStyle(null, widgetA))
            org.junit.Assert.assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetA))

            assertEquals(com.timezy.widget.data.model.CircleStyle.PURE_WHITE, WidgetUpdateHelper.getWidgetCircleStyle(null, widgetB))
            assertTrue(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testExactUserVerificationFlowPinkOrangeGreen() {
        val sharedStorage = mutableMapOf<String, Any?>()
        val prefs = FakeSharedPreferences(sharedStorage)
        WidgetUpdateHelper.prefsOverrideForTesting = prefs
        try {
            val widgetA = 57 // Widget #57 (e.g. Happy birthday)
            val widgetB = 58 // Widget #58 (e.g. Vacation)

            // Step 1: Set Widget A to Pink
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetA, EventTheme.ROSE_VELVET)

            // Step 2: Set Widget B to Orange
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.SUNSET_EMBER)

            // Step 3: Verify that Widget A remains Pink and Widget B becomes Orange
            assertEquals(EventTheme.ROSE_VELVET, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.SUNSET_EMBER, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))

            // Step 4: Change Widget B to Green
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.EMERALD_MINT)

            // Step 5: Verify that Widget A remains Pink and Widget B becomes Green
            assertEquals(EventTheme.ROSE_VELVET, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))

            // Step 6: Restart the app and verify both colors persist
            val restartedPrefs = FakeSharedPreferences(sharedStorage)
            WidgetUpdateHelper.prefsOverrideForTesting = restartedPrefs
            assertEquals(EventTheme.ROSE_VELVET, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))

            // Step 7: Edit Widget B's event and confirm its color remains Green
            WidgetUpdateHelper.setWidgetEventId(null, widgetB, 999L)
            assertEquals(999L, WidgetUpdateHelper.getWidgetEventId(null, widgetB))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
            assertEquals(EventTheme.ROSE_VELVET, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))

            // Step 8: Confirm live preview & saved configuration match
            assertEquals(EventTheme.EMERALD_MINT.ringColorHex, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB).ringColorHex)
            assertEquals(EventTheme.ROSE_VELVET.ringColorHex, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA).ringColorHex)
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testEventThemeIsAuthoritativeForAssignedWidget() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 301
            val widgetB = 302

            val eventA = CountdownEvent(
                id = 1001L,
                title = "Event A (Blue)",
                targetDateTimeEpochMillis = System.currentTimeMillis() + 86400000L,
                theme = EventTheme.NEON_CYAN // Blue
            )
            val eventB = CountdownEvent(
                id = 1002L,
                title = "Event B (Pink)",
                targetDateTimeEpochMillis = System.currentTimeMillis() + 86400000L * 2,
                theme = EventTheme.ROSE_VELVET // Pink
            )

            // Inject events into cache
            WidgetUpdateHelper.eventCache[widgetA] = eventA
            WidgetUpdateHelper.eventCache[widgetB] = eventB

            // Even if a stale override exists in SharedPreferences, event theme MUST take precedence!
            fakePrefs.edit().putString("appwidget_color_$widgetA", EventTheme.EMERALD_MINT.id).commit()
            fakePrefs.edit().putString("appwidget_color_$widgetB", EventTheme.SUNSET_EMBER.id).commit()

            // Verify Widget A is Blue and Widget B is Pink
            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.NEON_CYAN.ringColorHex, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA).ringColorHex)

            assertEquals(EventTheme.ROSE_VELVET, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
            assertEquals(EventTheme.ROSE_VELVET.ringColorHex, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB).ringColorHex)
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
            WidgetUpdateHelper.eventCache.clear()
        }
    }

    @Test
    fun testEditingEventThemeUpdatesOnlyLinkedWidgets() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 301
            val widgetB = 302

            val eventA = CountdownEvent(
                id = 1001L,
                title = "Event A (Blue)",
                targetDateTimeEpochMillis = System.currentTimeMillis() + 86400000L,
                theme = EventTheme.NEON_CYAN // Blue
            )
            var eventB = CountdownEvent(
                id = 1002L,
                title = "Event B (Pink)",
                targetDateTimeEpochMillis = System.currentTimeMillis() + 86400000L * 2,
                theme = EventTheme.ROSE_VELVET // Pink
            )

            WidgetUpdateHelper.eventCache[widgetA] = eventA
            WidgetUpdateHelper.eventCache[widgetB] = eventB

            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.ROSE_VELVET, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))

            // User edits Event B in Edit Countdown screen from Pink to Orange
            eventB = eventB.copy(theme = EventTheme.SUNSET_EMBER)
            WidgetUpdateHelper.eventCache[widgetB] = eventB

            // Confirm Widget B became Orange while Widget A strictly remains Blue
            assertEquals(EventTheme.SUNSET_EMBER, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
            assertEquals(EventTheme.SUNSET_EMBER.ringColorHex, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB).ringColorHex)

            assertEquals(EventTheme.NEON_CYAN, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.NEON_CYAN.ringColorHex, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA).ringColorHex)
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
            WidgetUpdateHelper.eventCache.clear()
        }
    }

    @Test
    fun testAllFiveThemesMatchRingColorHex() {
        val themes = listOf(
            EventTheme.NEON_CYAN,
            EventTheme.COSMIC_PURPLE,
            EventTheme.EMERALD_MINT,
            EventTheme.SUNSET_EMBER,
            EventTheme.ROSE_VELVET
        )

        themes.forEachIndexed { index, theme ->
            val widgetId = 400 + index
            val event = CountdownEvent(
                id = (5000 + index).toLong(),
                title = "Event for ${theme.displayName}",
                targetDateTimeEpochMillis = System.currentTimeMillis() + 86400000L,
                theme = theme
            )
            WidgetUpdateHelper.eventCache[widgetId] = event

            val resolved = WidgetUpdateHelper.getWidgetColorTheme(null, widgetId)
            assertEquals(theme, resolved)
            assertEquals(theme.ringColorHex, resolved.ringColorHex)
        }
        WidgetUpdateHelper.eventCache.clear()
    }

    @Test
    fun testMoreThanOneDaySubDayCountdown() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        // 44 days, 1 hour, 45 minutes, 28 seconds in future
        val durationMillis = (44L * 86400L + 1L * 3600L + 45L * 60L + 28L) * 1000L
        val targetMillis = now + durationMillis

        val event = CountdownEvent(
            id = 50L,
            title = "Happy birthday",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            theme = EventTheme.NEON_CYAN
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(44, remaining.calendarDays)
        assertEquals(1, remaining.hours)
        assertEquals(45, remaining.minutes)
        assertEquals(28, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("1h 45m 28s left", formattedTimer)
    }

    @Test
    fun testLessThanOneHourCountdown() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        // 12 minutes, 5 seconds away
        val durationMillis = (12L * 60L + 5L) * 1000L
        val targetMillis = now + durationMillis

        val event = CountdownEvent(
            id = 51L,
            title = "Boarding Call",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            theme = EventTheme.COSMIC_PURPLE
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(0, remaining.calendarDays)
        assertEquals(0, remaining.hours)
        assertEquals(12, remaining.minutes)
        assertEquals(5, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("12m 05s left", formattedTimer)
    }

    @Test
    fun testLessThanOneMinuteCountdown() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        // 35 seconds away
        val targetMillis = now + 35L * 1000L

        val event = CountdownEvent(
            id = 52L,
            title = "Final Countdown",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            theme = EventTheme.EMERALD_MINT
        )

        val remaining = event.calculateRemainingTime(now)
        assertEquals(0, remaining.calendarDays)
        assertEquals(0, remaining.hours)
        assertEquals(0, remaining.minutes)
        assertEquals(35, remaining.seconds)

        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("35s left", formattedTimer)
    }

    @Test
    fun testReachingZeroExactMatch() {
        val now = Instant.parse("2026-10-20T10:00:00Z").toEpochMilli()
        val targetMillis = now

        val event = CountdownEvent(
            id = 53L,
            title = "Zero Moment",
            targetDateTimeEpochMillis = targetMillis,
            hasSpecificTime = true,
            theme = EventTheme.SUNSET_EMBER
        )

        val remaining = event.calculateRemainingTime(now)
        val formattedTimer = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, now)
        assertEquals("0s left", formattedTimer)
    }

    @Test
    fun testIndependentShowSecondsPerWidgetInstance() {
        val fakePrefs = FakeSharedPreferences()
        WidgetUpdateHelper.prefsOverrideForTesting = fakePrefs
        try {
            val widgetA = 201
            val widgetB = 202

            // Initially set Widget A to true, Widget B to false
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetA, true)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetB, false)

            assertTrue(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetA))
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetB))

            // Turn Widget A to false: Widget B must remain unchanged (false)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetA, false)
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetA))
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetB))

            // Turn Widget B to true: Widget A must remain unchanged (false)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetB, true)
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetA))
            assertTrue(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testShowSecondsPersistenceAcrossAppRestarts() {
        val storage = mutableMapOf<String, Any?>()
        val session1 = FakeSharedPreferences(storage)
        WidgetUpdateHelper.prefsOverrideForTesting = session1
        try {
            val widget1 = 301
            val widget2 = 302

            WidgetUpdateHelper.setWidgetShowSeconds(null, widget1, true)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widget2, false)

            // Simulate new session with same underlying storage
            val session2 = FakeSharedPreferences(storage)
            WidgetUpdateHelper.prefsOverrideForTesting = session2

            assertTrue(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widget1))
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widget2))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }

    @Test
    fun testWidgetSizeBucketMapping_SmallSizes() {
        // 1x1 cell or narrow/short dimensions should select SMALL bucket
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.SMALL, WidgetUpdateHelper.getBucketForDimensions(70, 70))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.SMALL, WidgetUpdateHelper.getBucketForDimensions(80, 80))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.SMALL, WidgetUpdateHelper.getBucketForDimensions(150, 70)) // 2x1 cell
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.SMALL, WidgetUpdateHelper.getBucketForDimensions(70, 150)) // 1x2 cell
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.SMALL, WidgetUpdateHelper.getBucketForDimensions(100, 100))
    }

    @Test
    fun testWidgetSizeBucketMapping_MediumSizes() {
        // Standard 2x2 widgets (~140-170dp) should select MEDIUM bucket
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.MEDIUM, WidgetUpdateHelper.getBucketForDimensions(140, 140))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.MEDIUM, WidgetUpdateHelper.getBucketForDimensions(150, 150))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.MEDIUM, WidgetUpdateHelper.getBucketForDimensions(180, 150))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.MEDIUM, WidgetUpdateHelper.getBucketForDimensions(150, 180))
    }

    @Test
    fun testWidgetSizeBucketMapping_LargeSizes() {
        // 3x3 widgets (~185-259dp) should select LARGE bucket
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.LARGE, WidgetUpdateHelper.getBucketForDimensions(190, 190))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.LARGE, WidgetUpdateHelper.getBucketForDimensions(220, 220))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.LARGE, WidgetUpdateHelper.getBucketForDimensions(250, 250))
    }

    @Test
    fun testWidgetSizeBucketMapping_XLargeSizes() {
        // 4x4+ widgets (>= 260dp) should select XLARGE bucket
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.XLARGE, WidgetUpdateHelper.getBucketForDimensions(260, 260))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.XLARGE, WidgetUpdateHelper.getBucketForDimensions(320, 320))
        assertEquals(WidgetUpdateHelper.WidgetSizeBucket.XLARGE, WidgetUpdateHelper.getBucketForDimensions(400, 400))
    }

    @Test
    fun testWidgetResizingPreservesWidgetIdentityAndPreferences() {
        val storage = mutableMapOf<String, Any?>()
        val session = FakeSharedPreferences(storage)
        WidgetUpdateHelper.prefsOverrideForTesting = session
        try {
            val widgetA = 501
            val widgetB = 502

            WidgetUpdateHelper.setWidgetEventId(null, widgetA, 1001L)
            WidgetUpdateHelper.setWidgetEventId(null, widgetB, 1002L)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetA, EventTheme.EMERALD_MINT)
            WidgetUpdateHelper.setWidgetColorTheme(null, widgetB, EventTheme.SUNSET_EMBER)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetA, true)
            WidgetUpdateHelper.setWidgetShowSeconds(null, widgetB, false)

            // Verify initial configuration
            assertEquals(1001L, WidgetUpdateHelper.getWidgetEventId(null, widgetA))
            assertEquals(1002L, WidgetUpdateHelper.getWidgetEventId(null, widgetB))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.SUNSET_EMBER, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
            assertTrue(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetA))
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetB))

            // Simulate resizing Widget A to Small (e.g. 70x70) and Widget B to XLarge (e.g. 320x320)
            val bucketA = WidgetUpdateHelper.getBucketForDimensions(70, 70)
            val bucketB = WidgetUpdateHelper.getBucketForDimensions(320, 320)
            assertEquals(WidgetUpdateHelper.WidgetSizeBucket.SMALL, bucketA)
            assertEquals(WidgetUpdateHelper.WidgetSizeBucket.XLARGE, bucketB)

            // Resizing must NOT mutate or overwrite preferences of either widget
            assertEquals(1001L, WidgetUpdateHelper.getWidgetEventId(null, widgetA))
            assertEquals(1002L, WidgetUpdateHelper.getWidgetEventId(null, widgetB))
            assertEquals(EventTheme.EMERALD_MINT, WidgetUpdateHelper.getWidgetColorTheme(null, widgetA))
            assertEquals(EventTheme.SUNSET_EMBER, WidgetUpdateHelper.getWidgetColorTheme(null, widgetB))
            assertTrue(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetA))
            assertFalse(WidgetUpdateHelper.isWidgetShowSecondsEnabled(null, widgetB))
        } finally {
            WidgetUpdateHelper.prefsOverrideForTesting = null
        }
    }
}

class FakeSharedPreferences(val map: MutableMap<String, Any?> = mutableMapOf()) : android.content.SharedPreferences {
    override fun getAll(): MutableMap<String, *> = map
    override fun getString(key: String?, defValue: String?): String? = (map[key] as? String) ?: defValue
    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = (map[key] as? MutableSet<String>) ?: defValues
    override fun getInt(key: String?, defValue: Int): Int = (map[key] as? Int) ?: defValue
    override fun getLong(key: String?, defValue: Long): Long = (map[key] as? Long) ?: defValue
    override fun getFloat(key: String?, defValue: Float): Float = (map[key] as? Float) ?: defValue
    override fun getBoolean(key: String?, defValue: Boolean): Boolean = (map[key] as? Boolean) ?: defValue
    override fun contains(key: String?): Boolean = map.containsKey(key)
    override fun edit(): android.content.SharedPreferences.Editor = FakeEditor(map)
    override fun registerOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener?) {}

    class FakeEditor(private val map: MutableMap<String, Any?>) : android.content.SharedPreferences.Editor {
        private val temp = mutableMapOf<String, Any?>()
        private val toRemove = mutableSetOf<String>()
        private var clear = false

        override fun putString(key: String?, value: String?): android.content.SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }
        override fun putStringSet(key: String?, values: MutableSet<String>?): android.content.SharedPreferences.Editor {
            if (key != null) temp[key] = values
            return this
        }
        override fun putInt(key: String?, value: Int): android.content.SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }
        override fun putLong(key: String?, value: Long): android.content.SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }
        override fun putFloat(key: String?, value: Float): android.content.SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }
        override fun putBoolean(key: String?, value: Boolean): android.content.SharedPreferences.Editor {
            if (key != null) temp[key] = value
            return this
        }
        override fun remove(key: String?): android.content.SharedPreferences.Editor {
            if (key != null) toRemove.add(key)
            return this
        }
        override fun clear(): android.content.SharedPreferences.Editor {
            clear = true
            return this
        }
        override fun commit(): Boolean {
            apply()
            return true
        }
        override fun apply() {
            if (clear) map.clear()
            for (k in toRemove) map.remove(k)
            for ((k, v) in temp) map[k] = v
        }
    }
}
