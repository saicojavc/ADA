package com.saico.ada.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saico.ada.domain.use_case.AddEventUseCase
import com.saico.ada.domain.use_case.DeleteEventUseCase
import com.saico.ada.domain.use_case.GetEventsUseCase

class EventViewModelFactory(
    private val getEventsUseCase: GetEventsUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(getEventsUseCase, addEventUseCase, deleteEventUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
