package com.saico.ada.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.saico.ada.model.EventCategory
import com.saico.ada.model.TipoRepeticion
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    fun fromTipoRepeticion(tipo: TipoRepeticion): String {
        return tipo.name
    }

    @TypeConverter
    fun toTipoRepeticion(value: String): TipoRepeticion {
        return TipoRepeticion.valueOf(value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun localTimeToValue(time: LocalTime?): String? {
        return time?.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun localDateToValue(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromDayOfWeekList(days: List<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekList(value: String?): List<DayOfWeek>? {
        return value?.let {
            if (it.isEmpty()) emptyList()
            else it.split(",").map { name -> DayOfWeek.valueOf(name) }
        }
    }
}
