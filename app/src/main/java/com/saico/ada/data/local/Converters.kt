package com.saico.ada.data.local

import androidx.room.TypeConverter
import com.saico.ada.domain.model.EventCategory

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
