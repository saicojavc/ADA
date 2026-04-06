package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.domain.use_case.*
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteEntityUseCase: DeleteEntityUseCase,
    private val addBienestarUseCase: AddBienestarUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val userPrefs: UserPrefs,
    private val getSmartSuggestionUseCase: GetSmartSuggestionUseCase
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
        userPrefs.isMother
    ) { data, agendaDate, userName, isMother ->
        val today = LocalDate.now()
        val now = LocalTime.now()

        val greeting = when (now.hour) {
            in 5..12 -> "Buenos días"
            in 13..19 -> "Buenas tardes"
            else -> "Buenas noches"
        }
        val fullGreeting = "$greeting, ${userName ?: ""}"

        // --- LÓGICA DE INTELIGENCIA ADA ---
        val suggestion = getSmartSuggestionUseCase(data.tareas.filter { it.fechaHoraInicio.toLocalDate() == today })

        val habitosUnicos = data.registrosBienestar
            .filter { it.horaProgramada != null }
            .distinctBy { it.tipo }

        fun getRitualForDate(tipo: String, date: LocalDate): Bienestar {
            val registroExistente = data.registrosBienestar.find { it.tipo == tipo && it.fecha.toLocalDate() == date }
            if (registroExistente != null) return registroExistente
            val configOriginal = data.registrosBienestar.find { it.tipo == tipo }!!
            return configOriginal.copy(id = 0, valorActual = 0f, fecha = date.atTime(configOriginal.horaProgramada ?: LocalTime.now()))
        }

        val ritualesHoy = habitosUnicos.map { getRitualForDate(it.tipo, today) }
        val ritualesAgenda = habitosUnicos.map { getRitualForDate(it.tipo, agendaDate) }
        val tareasHoy = data.tareas.filter { it.fechaHoraInicio.toLocalDate() == today }
        val tareasAgenda = data.tareas.filter { it.fechaHoraInicio.toLocalDate() == agendaDate }

        DashboardState.Success(
            tareasHoy = tareasHoy,
            tareasAgenda = tareasAgenda,
            todasLasTareas = data.tareas,
            registrosBienestar = data.registrosBienestar,
            notas = data.notas,
            userName = userName ?: "Jorge",
            greeting = fullGreeting,
            isMother = isMother,
            adaSuggestion = suggestion.mensaje,
            adaAction = suggestion.accion,
            suggestionType = suggestion.tipo // Parámetro añadido para corregir el error
        ) as DashboardState
    }
    .catch { e -> emit(DashboardState.Error(e.message ?: "Error desconocido")) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAgendaDateSelected(date: LocalDate) { _selectedAgendaDate.value = date }
    fun onAgendaViewModeChanged(mode: AgendaViewMode) { _agendaViewMode.value = mode }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleRitual(ritual: Bienestar) {
        viewModelScope.launch {
            val hoy = LocalDate.now()
            val nuevoValor = if (ritual.valorActual >= ritual.metaObjetivo) 0f else ritual.metaObjetivo
            addBienestarUseCase(ritual.copy(valorActual = nuevoValor, fecha = hoy.atTime(ritual.horaProgramada ?: LocalTime.now())))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addRitualPersonalizado(nombre: String, hora: LocalTime?) {
        viewModelScope.launch {
            addBienestarUseCase(Bienestar(
                tipo = nombre, valorActual = 0f, metaObjetivo = 1f, unidad = "u",
                iconoNombre = "star", fecha = LocalDateTime.now(), horaProgramada = hora
            ))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(titulo: String, contenido: String, colorHex: String) {
        viewModelScope.launch { addNoteUseCase(Nota(titulo = titulo, contenido = contenido, colorEtiquetaHex = colorHex, fechaCreacion = LocalDateTime.now())) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTarea(tarea: Tarea) { 
        viewModelScope.launch { 
            val generatedId = updateTaskUseCase(tarea)
            val tareaWithId = tarea.copy(id = generatedId.toInt())
            alarmScheduler.schedule(tareaWithId) 
        } 
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteTarea(tarea: Tarea) { 
        viewModelScope.launch { 
            deleteEntityUseCase.deleteTarea(tarea)
            alarmScheduler.cancel(tarea)
        }
    }
}
