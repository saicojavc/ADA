package com.saico.ada.database.repository

import com.saico.ada.database.datasource.EventLocalDataSource
import com.saico.ada.database.mapper.toDomain
import com.saico.ada.database.mapper.toEntity
import com.saico.ada.domain.repository.EventRepository
import com.saico.ada.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.collections.map

class EventRepositoryImpl @Inject constructor(
    private val localDataSource: EventLocalDataSource
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> {
        return localDataSource.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventsForDay(date: LocalDate): Flow<List<Event>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return localDataSource.getEventsForDay(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEventById(id: Long): Event? {
        return localDataSource.getEventById(id)?.toDomain()
    }

    override suspend fun upsertEvent(event: Event) {
        val entity = event.toEntity()
        if (event.id == 0L) {
            localDataSource.insertEvent(entity)
        } else {
            localDataSource.updateEvent(entity)
        }
    }

    override suspend fun deleteEvent(event: Event) {
        localDataSource.deleteEvent(event.toEntity())
    }
}
