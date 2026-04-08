package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.use_case.*
import com.saico.ada.model.*
import com.saico.ada.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val userPrefs: UserPrefs,
    private val getSmartSuggestionUseCase: GetSmartSuggestionUseCase,
    private val marcarTareaCompletadaUseCase: MarcarTareaCompletadaUseCase,
    private val getBalanceScoreUseCase: GetBalanceScoreUseCase,
    private val getGreetingUseCase: GetGreetingUseCase,
    private val getTasksForDateUseCase: GetTasksForDateUseCase,
    private val getTasksForMonthUseCase: GetTasksForMonthUseCase,
    private val addTareaUseCase: AddTareaUseCase,
    private val deleteTareaUseCase: DeleteTareaUseCase,
    private val toggleRitualUseCase: ToggleRitualUseCase,
    private val addRitualUseCase: AddRitualUseCase
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedAgendaDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedAgendaDate: StateFlow<LocalDate> = _selectedAgendaDate.asStateFlow()

    private val _agendaViewMode = MutableStateFlow(AgendaViewMode.SEMANAL)
    val agendaViewMode: StateFlow<AgendaViewMode> = _agendaViewMode.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val state: StateFlow<DashboardState> = combine(
        getDashboardDataUseCase(), 
        _selectedAgendaDate, 
        userPrefs.userName, 
        userPrefs.isMother,
        getBalanceScoreUseCase()
    ) { data, agendaDate, userName, isMother, balanceScore ->
        val today = LocalDate.now()
        
        val greetingRes = when (getGreetingUseCase()) {
            GreetingTime.MORNING -> R.string.home_greeting_morning
            GreetingTime.AFTERNOON -> R.string.home_greeting_afternoon
            GreetingTime.EVENING -> R.string.home_greeting_evening
        }

        val tareasHoyFinal = getTasksForDateUseCase(data.tareas, data.excepciones, today)
        val tareasAgendaFinal = getTasksForDateUseCase(data.tareas, data.excepciones, agendaDate)
        val todasLasTareasVisibles = getTasksForMonthUseCase(data.tareas, data.excepciones, agendaDate)

        val suggestion = getSmartSuggestionUseCase(tareasHoyFinal)

        DashboardState.Success(
            tareasHoy = tareasHoyFinal,
            tareasAgenda = tareasAgendaFinal,
            todasLasTareas = todasLasTareasVisibles,
            registrosBienestar = data.registrosBienestar,
            notas = data.notas,
            userName = userName ?: "",
            greetingRes = greetingRes,
            isMother = isMother,
            adaSuggestionRes = suggestion.mensajeRes,
            adaActionRes = suggestion.accionRes,
            adaSuggestionArgs = suggestion.mensajeArgs,
            adaActionArgs = suggestion.accionArgs,
            suggestionType = suggestion.tipo,
            balanceScore = balanceScore
        ) as DashboardState
    }.catch { e -> emit(DashboardState.Error(e.message ?: "Error desconocido")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAgendaDateSelected(date: LocalDate) {
        _selectedAgendaDate.value = date
    }

    fun onAgendaViewModeChanged(mode: AgendaViewMode) {
        _agendaViewMode.value = mode
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTareaCompletada(tarea: Tarea) {
        viewModelScope.launch {
            marcarTareaCompletadaUseCase(tarea, !tarea.estaCompletada)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteTarea(tarea: Tarea, cancelarFuturas: Boolean = false) {
        viewModelScope.launch {
            deleteTareaUseCase(tarea, cancelarFuturas)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTarea(tarea: Tarea) {
        viewModelScope.launch {
            addTareaUseCase(tarea)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(titulo: String, contenido: String, colorHex: String) {
        viewModelScope.launch { 
            addNoteUseCase(
                Nota(
                    titulo = titulo, 
                    contenido = contenido, 
                    colorEtiquetaHex = colorHex, 
                    fechaCreacion = LocalDateTime.now()
                )
            ) 
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleRitual(ritual: Bienestar) {
        viewModelScope.launch {
            toggleRitualUseCase(ritual)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addRitualPersonalizado(nombre: String, hora: LocalTime?) {
        viewModelScope.launch { 
            addRitualUseCase(nombre, hora)
        }
    }
}
