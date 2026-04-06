package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.domain.use_case.AddBienestarUseCase
import com.saico.ada.domain.use_case.AddNoteUseCase
import com.saico.ada.domain.use_case.DeleteEntityUseCase
import com.saico.ada.domain.use_case.GetDashboardDataUseCase
import com.saico.ada.domain.use_case.UpdateTaskUseCase
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
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
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteEntityUseCase: DeleteEntityUseCase,
    private val addBienestarUseCase: AddBienestarUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val userPrefs: UserPrefs
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedAgendaDate = MutableStateFlow(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedAgendaDate: StateFlow<LocalDate> = _selectedAgendaDate.asStateFlow()

    private val _agendaViewMode = MutableStateFlow(AgendaViewMode.SEMANAL)
    val agendaViewMode: StateFlow<AgendaViewMode> = _agendaViewMode.asStateFlow()

    // Rituales Base
    @RequiresApi(Build.VERSION_CODES.O)
    private val ritualesBase = listOf(
        Bienestar(
            tipo = "Baño de Luz",
            valorActual = 0f,
            metaObjetivo = 1f,
            unidad = "u",
            iconoNombre = "light_mode",
            fecha = LocalDateTime.now(),
            horaProgramada = LocalTime.of(7, 0)
        ),
        Bienestar(
            tipo = "Brain Dump",
            valorActual = 0f,
            metaObjetivo = 1f,
            unidad = "u",
            iconoNombre = "psychology",
            fecha = LocalDateTime.now(),
            horaProgramada = LocalTime.of(22, 0)
        ),
        Bienestar(
            tipo = "Estiramiento",
            valorActual = 0f,
            metaObjetivo = 1f,
            unidad = "u",
            iconoNombre = "self_improvement",
            fecha = LocalDateTime.now(),
            horaProgramada = LocalTime.of(11, 0)
        ),
        Bienestar(
            tipo = "Lectura Cuentos",
            valorActual = 0f,
            metaObjetivo = 1f,
            unidad = "u",
            iconoNombre = "child_care",
            fecha = LocalDateTime.now(),
            horaProgramada = LocalTime.of(20, 30)
        )
    )

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

        val todosLosTiposDeRituales = (ritualesBase.map { it.tipo } +
                data.registrosBienestar.filter { it.horaProgramada != null }.map { it.tipo })
            .distinct()

        fun getRitualForDate(tipo: String, date: LocalDate): Bienestar {
            val registroExistente =
                data.registrosBienestar.find { it.tipo == tipo && it.fecha.toLocalDate() == date }
            if (registroExistente != null) return registroExistente
            val configOriginal = ritualesBase.find { it.tipo == tipo }
                ?: data.registrosBienestar.find { it.tipo == tipo }!!
            return configOriginal.copy(
                id = 0,
                valorActual = 0f,
                fecha = date.atTime(configOriginal.horaProgramada ?: LocalTime.now())
            )
        }

        val ritualesHoy = todosLosTiposDeRituales.map { getRitualForDate(it, today) }
        val ritualesAgenda = todosLosTiposDeRituales.map { getRitualForDate(it, agendaDate) }
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
            isMother = isMother
        ) as DashboardState
    }
        .catch { e -> emit(DashboardState.Error(e.message ?: "Error desconocido")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAgendaDateSelected(date: LocalDate) {
        _selectedAgendaDate.value = date
    }

    fun onAgendaViewModeChanged(mode: AgendaViewMode) {
        _agendaViewMode.value = mode
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleRitual(ritual: Bienestar) {
        viewModelScope.launch {
            val hoy = LocalDate.now()
            val nuevoValor =
                if (ritual.valorActual >= ritual.metaObjetivo) 0f else ritual.metaObjetivo
            addBienestarUseCase(
                ritual.copy(
                    valorActual = nuevoValor,
                    fecha = hoy.atTime(ritual.horaProgramada ?: LocalTime.now())
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addRitualPersonalizado(nombre: String, hora: LocalTime?) {
        viewModelScope.launch {
            addBienestarUseCase(
                Bienestar(
                    tipo = nombre, valorActual = 0f, metaObjetivo = 1f, unidad = "u",
                    iconoNombre = "star", fecha = LocalDateTime.now(), horaProgramada = hora
                )
            )
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
