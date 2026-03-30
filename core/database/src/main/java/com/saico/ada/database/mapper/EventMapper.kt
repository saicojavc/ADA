package com.saico.ada.database.mapper

import com.saico.ada.database.entity.EventEntity
import com.saico.ada.model.Event
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun EventEntity.toDomain(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()),
        endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()),
        category = category,
        isAllDay = isAllDay
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        startTime = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        endTime = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        category = category,
        isAllDay = isAllDay
    )
}
