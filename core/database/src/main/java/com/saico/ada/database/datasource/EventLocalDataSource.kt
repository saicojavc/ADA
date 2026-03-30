package com.saico.ada.database.datasource

import com.saico.ada.database.entity.EventEntity
import kotlinx.coroutines.flow.Flow

interface EventLocalDataSource {
    fun getAllEvents(): Flow<List<EventEntity>>
    fun getEventsForDay(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>
    suspend fun getEventById(id: Long): EventEntity?
    suspend fun insertEvent(event: EventEntity): Long
    suspend fun updateEvent(event: EventEntity)
    suspend fun deleteEvent(event: EventEntity)
}
