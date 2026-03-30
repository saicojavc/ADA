package com.saico.ada.model

import java.time.LocalDateTime

data class TimeGap(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val durationMinutes: Long
)
