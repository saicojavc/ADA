package com.saico.ada.model

import java.time.LocalDateTime

data class Event(
    val id: Long = 0,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val category: EventCategory,
    val isAllDay: Boolean = false
)
