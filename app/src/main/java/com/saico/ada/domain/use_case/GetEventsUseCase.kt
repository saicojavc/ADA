package com.saico.ada.domain.use_case

import com.saico.ada.domain.model.Event
import com.saico.ada.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetEventsUseCase(
    private val repository: EventRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Event>> {
        return repository.getEventsForDay(date)
    }

    fun getAll(): Flow<List<Event>> {
        return repository.getAllEvents()
    }
}
