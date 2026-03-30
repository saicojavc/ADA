package com.saico.ada.domain.use_case

import com.saico.ada.domain.model.Event
import com.saico.ada.domain.repository.EventRepository

class DeleteEventUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) {
        repository.deleteEvent(event)
    }
}
