package com.timezy.widget.data.repository

import com.timezy.widget.data.model.CountdownEvent
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEventsFlow(): Flow<List<CountdownEvent>>
    suspend fun getAllEvents(): List<CountdownEvent>
    fun getAllEventsSync(): List<CountdownEvent>
    fun getEventByIdFlow(id: Long): Flow<CountdownEvent?>
    suspend fun getEventById(id: Long): CountdownEvent?
    fun getEventByIdSync(id: Long): CountdownEvent?
    suspend fun insertEvent(event: CountdownEvent): Long
    suspend fun updateEvent(event: CountdownEvent)
    suspend fun deleteEvent(event: CountdownEvent)
    suspend fun deleteEventById(id: Long)
    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean)
    suspend fun clearAllEvents()
}
