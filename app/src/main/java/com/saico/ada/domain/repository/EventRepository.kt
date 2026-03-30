package com.saico.ada.domain.repository

import com.saico.ada.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsForDay(date: LocalDate): Flow<List<Event>>
    suspend fun getEventById(id: Long): Event?
    suspend fun upsertEvent(event: Event)
    suspend fun deleteEvent(event: Event)
}
