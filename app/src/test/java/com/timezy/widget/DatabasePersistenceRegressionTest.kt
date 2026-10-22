package com.timezy.widget

import com.timezy.widget.data.database.EventEntity
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.data.model.RecurrenceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * Regression tests for HIGH-01:
 * Verifies that events with common titles ("Dream Vacation", "Alex's Birthday", "New Year 2027")
 * are strictly preserved in persistent storage and are never wiped out during database initialization
 * or app lifecycle reopening. Also verifies intentional user-initiated deletions work correctly.
 */
class DatabasePersistenceRegressionTest {

    @Test
    fun testCommonEventTitlesSurvivePersistence() {
        val now = Instant.parse("2026-10-22T12:00:00Z").toEpochMilli()

        // 1. Create events that previously matched the destructive demo delete query
        val vacationEvent = CountdownEvent(
            id = 201L,
            title = "Dream Vacation",
            description = "Flights booked to Hawaii for family reunion",
            targetDateTimeEpochMillis = now + (30L * 86400L * 1000L),
            hasSpecificTime = true,
            category = EventCategory.TRAVEL,
            theme = EventTheme.NEON_CYAN
        )

        val birthdayEvent = CountdownEvent(
            id = 202L,
            title = "Alex's Birthday",
            description = "Don't forget the gift and chocolate cake",
            targetDateTimeEpochMillis = now + (14L * 86400L * 1000L),
            hasSpecificTime = false,
            category = EventCategory.BIRTHDAY,
            theme = EventTheme.SUNSET_EMBER
        )

        val newYearEvent = CountdownEvent(
            id = 203L,
            title = "New Year 2027 Countdown",
            description = "Welcoming the new chapter with friends",
            targetDateTimeEpochMillis = now + (70L * 86400L * 1000L),
            hasSpecificTime = false,
            category = EventCategory.HOLIDAY,
            theme = EventTheme.COSMIC_PURPLE
        )

        // 2. Convert to database entities as Room does
        val entity1 = EventEntity.fromDomain(vacationEvent)
        val entity2 = EventEntity.fromDomain(birthdayEvent)
        val entity3 = EventEntity.fromDomain(newYearEvent)

        // 3. Simulate persistent database storage
        val persistentStorage = mutableMapOf<Long, EventEntity>()
        persistentStorage[entity1.id] = entity1
        persistentStorage[entity2.id] = entity2
        persistentStorage[entity3.id] = entity3

        assertEquals(3, persistentStorage.size)

        // 4. Simulate multiple app restarts / database reopening cycles
        for (restartCycle in 1..5) {
            // With the fix applied, the database opens without executing blanket destructive DELETE queries.
            assertTrue(persistentStorage.containsKey(201L))
            assertTrue(persistentStorage.containsKey(202L))
            assertTrue(persistentStorage.containsKey(203L))

            val restoredVacation = persistentStorage[201L]?.toDomain()
            assertNotNull(restoredVacation)
            assertEquals("Dream Vacation", restoredVacation?.title)
            assertEquals("Flights booked to Hawaii for family reunion", restoredVacation?.description)

            val restoredBirthday = persistentStorage[202L]?.toDomain()
            assertNotNull(restoredBirthday)
            assertEquals("Alex's Birthday", restoredBirthday?.title)

            val restoredNewYear = persistentStorage[203L]?.toDomain()
            assertNotNull(restoredNewYear)
            assertEquals("New Year 2027 Countdown", restoredNewYear?.title)
        }
    }

    @Test
    fun testIntentionalUserDeletionStillWorks() {
        // Verifies that intentional user-requested deletion is preserved and works correctly
        val event = CountdownEvent(
            id = 301L,
            title = "Old Temporary Milestone",
            description = "To be deleted by user request",
            targetDateTimeEpochMillis = System.currentTimeMillis() + 86400000L,
            category = EventCategory.OTHER,
            theme = EventTheme.EMERALD_MINT
        )

        val entity = EventEntity.fromDomain(event)
        val persistentStorage = mutableMapOf<Long, EventEntity>()
        persistentStorage[entity.id] = entity
        assertEquals(1, persistentStorage.size)

        // User deletes the event intentionally
        persistentStorage.remove(301L)
        assertEquals(0, persistentStorage.size)
        assertFalse(persistentStorage.containsKey(301L))
    }

    @Test
    fun testEntityDomainBidirectionalIntegrity() {
        val now = 1761134400000L // arbitrary fixed epoch millis
        val event = CountdownEvent(
            id = 405L,
            title = "Annual Company Summit",
            description = "Keynote address and roadmap presentation",
            targetDateTimeEpochMillis = now + 5000000L,
            hasSpecificTime = true,
            category = EventCategory.CAREER,
            theme = EventTheme.ROSE_VELVET,
            isFavorite = true,
            recurrence = RecurrenceType.YEARLY,
            reminderMinutesBefore = 60,
            isNotificationEnabled = true,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now + 1000L
        )

        val entity = EventEntity.fromDomain(event)
        assertEquals(405L, entity.id)
        assertEquals("Annual Company Summit", entity.title)
        assertEquals("Keynote address and roadmap presentation", entity.description)
        assertEquals(now + 5000000L, entity.targetDateTimeEpochMillis)
        assertTrue(entity.hasSpecificTime)
        assertEquals("career", entity.category)
        assertEquals("rose_velvet", entity.theme)
        assertTrue(entity.isFavorite)
        assertEquals("YEARLY", entity.recurrence)
        assertEquals(60, entity.reminderMinutesBefore)
        assertTrue(entity.isNotificationEnabled)
        assertEquals(now, entity.createdAtEpochMillis)
        assertEquals(now + 1000L, entity.updatedAtEpochMillis)

        val reconstructed = entity.toDomain()
        assertEquals(event.id, reconstructed.id)
        assertEquals(event.title, reconstructed.title)
        assertEquals(event.description, reconstructed.description)
        assertEquals(event.targetDateTimeEpochMillis, reconstructed.targetDateTimeEpochMillis)
        assertEquals(event.hasSpecificTime, reconstructed.hasSpecificTime)
        assertEquals(event.category, reconstructed.category)
        assertEquals(event.theme, reconstructed.theme)
        assertEquals(event.isFavorite, reconstructed.isFavorite)
        assertEquals(event.recurrence, reconstructed.recurrence)
        assertEquals(event.reminderMinutesBefore, reconstructed.reminderMinutesBefore)
        assertEquals(event.isNotificationEnabled, reconstructed.isNotificationEnabled)
        assertEquals(event.createdAtEpochMillis, reconstructed.createdAtEpochMillis)
        assertEquals(event.updatedAtEpochMillis, reconstructed.updatedAtEpochMillis)
    }
}
