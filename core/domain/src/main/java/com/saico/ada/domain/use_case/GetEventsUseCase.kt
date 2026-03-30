package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.EventRepository
import com.saico.ada.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Event>> {
        return repository.getEventsForDay(date)
    }

    fun getAll(): Flow<List<Event>> {
        return repository.getAllEvents()
    }
}
