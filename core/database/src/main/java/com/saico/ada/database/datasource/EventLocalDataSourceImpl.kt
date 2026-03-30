package com.saico.ada.database.datasource

import com.saico.ada.database.dao.EventDao
import com.saico.ada.database.entity.EventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventLocalDataSourceImpl @Inject constructor(
    private val eventDao: EventDao
) : EventLocalDataSource {
    override fun getAllEvents(): Flow<List<EventEntity>> = eventDao.getAllEvents()

    override fun getEventsForDay(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>> =
        eventDao.getEventsForDay(startOfDay, endOfDay)

    override suspend fun getEventById(id: Long): EventEntity? = eventDao.getEventById(id)

    override suspend fun insertEvent(event: EventEntity): Long = eventDao.insertEvent(event)

    override suspend fun updateEvent(event: EventEntity) = eventDao.updateEvent(event)

    override suspend fun deleteEvent(event: EventEntity) = eventDao.deleteEvent(event)
}
