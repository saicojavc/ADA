package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.AgendaState
import com.saico.ada.dashboard.state.HomeState
import com.saico.ada.dashboard.state.NotesState
import com.saico.ada.dashboard.state.WellnessState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.use_case.AddCategoriaUseCase
import com.saico.ada.domain.use_case.AddNoteUseCase
import com.saico.ada.domain.use_case.AddRitualUseCase
import com.saico.ada.domain.use_case.AddTareaUseCase
import com.saico.ada.domain.use_case.DeleteNoteUseCase
import com.saico.ada.domain.use_case.DeleteTareaUseCase
import com.saico.ada.domain.use_case.GetBalanceScoreUseCase
import com.saico.ada.domain.use_case.GetDashboardDataUseCase
import com.saico.ada.domain.use_case.GetGreetingUseCase
import com.saico.ada.domain.use_case.GetInactivitySleepUseCase
import com.saico.ada.domain.use_case.GetSmartSuggestionUseCase
import com.saico.ada.domain.use_case.GetTasksForDateUseCase
import com.saico.ada.domain.use_case.GetTasksForMonthUseCase
import com.saico.ada.domain.use_case.GreetingTime
import com.saico.ada.domain.use_case.MarcarTareaCompletadaUseCase
import com.saico.ada.domain.use_case.ToggleRitualUseCase
import com.saico.ada.domain.use_case.UpdateNoteUseCase
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Categoria
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import com.saico.ada.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
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
    private val addRitualUseCase: AddRitualUseCase,
    private val getInactivitySleepUseCase: GetInactivitySleepUseCase,
    private val addCategoriaUseCase: AddCategoriaUseCase
) : ViewModel() {

    private val dashboardData = getDashboardDataUseCase()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), replay = 1)

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedAgendaDate = MutableStateFlow(LocalDate.now())

    private val _agendaViewMode = MutableStateFlow(AgendaViewMode.SEMANAL)

    @RequiresApi(Build.VERSION_CODES.O)
    val homeState: StateFlow<HomeState> = combine(
        dashboardData,
        userPrefs.userName,
        userPrefs.isMother,
        getInactivitySleepUseCase()
    ) { data, userName, isMother, autoSleepHours ->
        val today = LocalDate.now()
        val greetingRes = when (getGreetingUseCase()) {
            GreetingTime.MORNING -> R.string.home_greeting_morning
            GreetingTime.AFTERNOON -> R.string.home_greeting_afternoon
            GreetingTime.EVENING -> R.string.home_greeting_evening
        }
        val tareasHoy = getTasksForDateUseCase(data.tareas, data.excepciones, today)
        val suggestion = getSmartSuggestionUseCase(tareasHoy)
        
        val totalSleepHours = calculateSleepHours(tareasHoy, autoSleepHours)

        HomeState.Success(
            tareasHoy = tareasHoy,
            userName = userName ?: "",
            greetingRes = greetingRes,
            isMother = isMother,
            adaSuggestionRes = suggestion.mensajeRes,
            adaActionRes = suggestion.accionRes,
            adaSuggestionArgs = suggestion.mensajeArgs,
            adaActionArgs = suggestion.accionArgs,
            suggestionType = suggestion.tipo,
            horasSueno = totalSleepHours,
            categorias = data.categorias,
            notas = data.notas
        )
    }.map { it as HomeState }
        .catch { e -> emit(HomeState.Error(e.message ?: "Error desconocido")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    val agendaState: StateFlow<AgendaState> = combine(
        dashboardData,
        _selectedAgendaDate,
        _agendaViewMode,
        userPrefs.isMother
    ) { data, date, viewMode, isMother ->
        val tareasDelDia = getTasksForDateUseCase(data.tareas, data.excepciones, date)
        val todasLasTareas = getTasksForMonthUseCase(data.tareas, data.excepciones, date)

        AgendaState.Success(
            tareasDelDia = tareasDelDia,
            todasLasTareas = todasLasTareas,
            notas = data.notas,
            categorias = data.categorias,
            isMother = isMother,
            selectedDate = date,
            viewMode = viewMode
        )
    }.map { it as AgendaState }
        .catch { e -> emit(AgendaState.Error(e.message ?: "Error desconocido")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgendaState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    val wellnessState: StateFlow<WellnessState> = combine(
        dashboardData,
        getBalanceScoreUseCase(),
        getInactivitySleepUseCase(),
        userPrefs.isMother
    ) { data, balanceScore, autoSleepHours, isMother ->
        val today = LocalDate.now()
        val tareasHoy = getTasksForDateUseCase(data.tareas, data.excepciones, today)
        val totalSleepHours = calculateSleepHours(tareasHoy, autoSleepHours)

        WellnessState.Success(
            registrosBienestar = data.registrosBienestar,
            balanceScore = balanceScore,
            horasSueno = totalSleepHours,
            tareasHoy = tareasHoy,
            isMother = isMother
        )
    }.map { it as WellnessState }
        .catch { e -> emit(WellnessState.Error(e.message ?: "Error desconocido")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WellnessState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    val notesState: StateFlow<NotesState> = combine(
        dashboardData,
        userPrefs.isMother
    ) { data, isMother ->
        val currentMonth = LocalDate.now()
        val todasLasTareas = getTasksForMonthUseCase(data.tareas, data.excepciones, currentMonth)

        NotesState.Success(
            notas = data.notas,
            todasLasTareas = todasLasTareas,
            isMother = isMother
        )
    }.map { it as NotesState }
        .catch { e -> emit(NotesState.Error(e.message ?: "Error desconocido")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateSleepHours(tareasHoy: List<Tarea>, autoSleepHours: Float): Float {
        val keywordsSueno = listOf("sueño", "descanso", "sleep", "rest")
        val manualSleepHours = tareasHoy
            .filter {
                it.estaCompletada && keywordsSueno.any { kw ->
                    it.titulo.lowercase().contains(kw)
                }
            }
            .sumOf {
                val duracion = ChronoUnit.MINUTES.between(it.fechaHoraInicio, it.fechaHoraFin)
                duracion.toDouble() / 60.0
            }.toFloat()

        return maxOf(manualSleepHours, autoSleepHours)
    }

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

    fun addCategoriaPersonalizada(nombre: String, colorHex: String) {
        viewModelScope.launch {
            addCategoriaUseCase(Categoria(nombre = nombre, colorHex = colorHex))
        }
    }
}
