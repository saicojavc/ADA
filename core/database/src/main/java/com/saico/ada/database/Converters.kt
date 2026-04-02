package com.saico.ada.database

import androidx.room.TypeConverter
import com.saico.ada.model.EventCategory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromEventCategory(category: EventCategory): String {
        return category.name
    }

    @TypeConverter
    fun toEventCategory(category: String): EventCategory {
        return EventCategory.valueOf(category)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
}
