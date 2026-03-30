package com.saico.ada.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.domain.model.Event
import com.saico.ada.domain.model.EventCategory
import com.saico.ada.domain.model.TimeGap
import com.saico.ada.domain.use_case.AddEventUseCase
import com.saico.ada.domain.use_case.DeleteEventUseCase
import com.saico.ada.domain.use_case.GapFinderUseCase
import com.saico.ada.domain.use_case.GetEventsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EventViewModel(
    private val getEventsUseCase: GetEventsUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val gapFinderUseCase: GapFinderUseCase = GapFinderUseCase()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val events: StateFlow<List<Event>> = _selectedDate
        .flatMapLatest { date ->
            getEventsUseCase(date)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gaps: StateFlow<List<TimeGap>> = combine(_selectedDate, events) { date, eventList ->
        gapFinderUseCase(eventList, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addEvent(
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        category: EventCategory = EventCategory.UNCATEGORIZED
    ) {
        viewModelScope.launch {
            val event = Event(
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                category = category
            )
            addEventUseCase(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            addEventUseCase(event) // upsert
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            deleteEventUseCase(event)
        }
    }
}
