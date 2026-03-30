package com.saico.ada.domain.use_case

import com.saico.ada.domain.model.Event
import com.saico.ada.domain.model.TimeGap
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration
import java.util.stream.Collectors

class GapFinderUseCase {
    operator fun invoke(
        events: List<Event>,
        date: LocalDate,
        startHour: Int = 8,
        endHour: Int = 20,
        minGapMinutes: Long = 20
    ): List<TimeGap> {
        val dayStart = LocalDateTime.of(date, LocalTime.of(startHour, 0))
        val dayEnd = LocalDateTime.of(date, LocalTime.of(endHour, 0))

        val sortedEvents = events
            .filter { !it.isAllDay }
            .sortedBy { it.startTime }

        val gaps = mutableListOf<TimeGap>()
        var lastEndTime = dayStart

        for (event in sortedEvents) {
            if (event.startTime.isAfter(lastEndTime)) {
                val duration = Duration.between(lastEndTime, event.startTime).toMinutes()
                if (duration >= minGapMinutes) {
                    gaps.add(TimeGap(lastEndTime, event.startTime, duration))
                }
            }
            if (event.endTime.isAfter(lastEndTime)) {
                lastEndTime = event.endTime
            }
        }

        if (lastEndTime.isBefore(dayEnd)) {
            val duration = Duration.between(lastEndTime, dayEnd).toMinutes()
            if (duration >= minGapMinutes) {
                gaps.add(TimeGap(lastEndTime, dayEnd, duration))
            }
        }

        return gaps
    }
}
