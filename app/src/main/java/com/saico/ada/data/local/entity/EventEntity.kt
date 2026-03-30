package com.saico.ada.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.ada.domain.model.EventCategory

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val startTime: Long, // Epoch millis
    val endTime: Long,   // Epoch millis
    val category: EventCategory,
    val isAllDay: Boolean
)
