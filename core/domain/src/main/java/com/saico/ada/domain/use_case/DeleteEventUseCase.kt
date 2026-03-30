package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.EventRepository
import com.saico.ada.model.Event
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) {
        repository.deleteEvent(event)
    }
}
