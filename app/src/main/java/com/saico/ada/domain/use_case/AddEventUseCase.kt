package com.saico.ada.domain.use_case

import com.saico.ada.domain.model.Event
import com.saico.ada.domain.model.EventCategory
import com.saico.ada.domain.repository.EventRepository
import com.saico.ada.domain.service.EventCategorizer

class AddEventUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) {
        val categorizedEvent = if (event.category == EventCategory.UNCATEGORIZED) {
            event.copy(category = EventCategorizer.categorize(event.title, event.description))
        } else {
            event
        }
        repository.upsertEvent(categorizedEvent)
    }
}
