package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.use_case.AddNoteUseCase
import com.saico.ada.domain.use_case.AddRitualUseCase
import com.saico.ada.domain.use_case.AddTareaUseCase
import com.saico.ada.domain.use_case.DeleteNoteUseCase
import com.saico.ada.domain.use_case.DeleteTareaUseCase
import com.saico.ada.domain.use_case.GetBalanceScoreUseCase
import com.saico.ada.domain.use_case.GetDashboardDataUseCase
import com.saico.ada.domain.use_case.GetGreetingUseCase
import com.saico.ada.domain.use_case.GetSmartSuggestionUseCase
import com.saico.ada.domain.use_case.GetTasksForDateUseCase
import com.saico.ada.domain.use_case.GetTasksForMonthUseCase
import com.saico.ada.domain.use_case.GreetingTime
import com.saico.ada.domain.use_case.MarcarTareaCompletadaUseCase
import com.saico.ada.domain.use_case.ToggleRitualUseCase
import com.saico.ada.domain.use_case.UpdateNoteUseCase
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import com.saico.ada.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
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
        val todasLasTareasVisibles =
            getTasksForMonthUseCase(data.tareas, data.excepciones, agendaDate)

        val suggestion = getSmartSuggestionUseCase(tareasHoyFinal)

        // Calcular horas de sueño de las tareas completadas hoy
        val keywordsSueno = listOf("sueño", "descanso", "sleep", "rest")
        val horasSuenoCalculadas = tareasHoyFinal
            .filter {
                it.estaCompletada && keywordsSueno.any { kw ->
                    it.titulo.lowercase().contains(kw)
                }
            }
            .sumOf {
                val duracion = ChronoUnit.MINUTES.between(it.fechaHoraInicio, it.fechaHoraFin)
                duracion.toDouble() / 60.0
            }.toFloat()

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
            balanceScore = balanceScore,
            horasSueno = horasSuenoCalculadas
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
    fun addNote(titulo: String, contenido: String, colorHex: String, tareaId: Int? = null) {
        viewModelScope.launch {
            addNoteUseCase(
                Nota(
                    titulo = titulo,
                    contenido = contenido,
                    colorEtiquetaHex = colorHex,
                    fechaCreacion = LocalDateTime.now(),
                    tareaId = tareaId
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNote(nota: Nota) {
        viewModelScope.launch {
            updateNoteUseCase(nota)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteNote(nota: Nota) {
        viewModelScope.launch {
            deleteNoteUseCase(nota)
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
