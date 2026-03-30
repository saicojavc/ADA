package com.saico.ada.data.repository

import com.saico.ada.data.local.dao.EventDao
import com.saico.ada.data.mapper.toDomain
import com.saico.ada.data.mapper.toEntity
import com.saico.ada.domain.model.Event
import com.saico.ada.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class EventRepositoryImpl(
    private val dao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> {
        return dao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventsForDay(date: LocalDate): Flow<List<Event>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getEventsForDay(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEventById(id: Long): Event? {
        return dao.getEventById(id)?.toDomain()
    }

    override suspend fun upsertEvent(event: Event) {
        dao.insertEvent(event.toEntity())
    }

    override suspend fun deleteEvent(event: Event) {
        dao.deleteEvent(event.toEntity())
    }
}
