package com.saico.ada.database

import androidx.room.TypeConverter
import com.saico.ada.model.EventCategory

class Converters {
    @TypeConverter
    fun fromEventCategory(category: EventCategory): String {
        return category.name
    }

    @TypeConverter
    fun toEventCategory(category: String): EventCategory {
        return EventCategory.valueOf(category)
    }
}
