package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.EventRepository
import com.saico.ada.domain.service.EventCategorizer
import com.saico.ada.model.Event
import com.saico.ada.model.EventCategory
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
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
