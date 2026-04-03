package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteEntityUseCase: DeleteEntityUseCase,
    private val addBienestarUseCase: AddBienestarUseCase
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val state: StateFlow<DashboardState> = getDashboardDataUseCase()
        .map { data ->
            val now = LocalTime.now()
            val today = LocalDate.now()

            // Filtro para HomeScreen: Solo hoy y que no hayan terminado
            val tareasFiltradas = data.tareas.filter {
                it.fechaHoraInicio.toLocalDate() == today &&
                        it.fechaHoraFin.toLocalTime().isAfter(now)
            }

            DashboardState.Success(
                tareas = tareasFiltradas,
                todasLasTareas = data.tareas, // Para AgendaScreen si fuera necesario
                registrosBienestar = data.registrosBienestar,
                notas = data.notas
            ) as DashboardState
        }
        .catch { e ->
            emit(DashboardState.Error(e.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState.Loading
        )

    // --- Estado de la Agenda ---

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedAgendaDate = MutableStateFlow(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedAgendaDate: StateFlow<LocalDate> = _selectedAgendaDate.asStateFlow()

    private val _agendaViewMode = MutableStateFlow(AgendaViewMode.SEMANAL)
    val agendaViewMode: StateFlow<AgendaViewMode> = _agendaViewMode.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAgendaDateSelected(date: LocalDate) {
        _selectedAgendaDate.value = date
    }

    fun onAgendaViewModeChanged(mode: AgendaViewMode) {
        _agendaViewMode.value = mode
    }

    // --- Acciones ---

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(titulo: String, contenido: String, colorHex: String) {
        viewModelScope.launch {
            addNoteUseCase(
                Nota(
                    titulo = titulo,
                    contenido = contenido,
                    colorEtiquetaHex = colorHex,
                    fechaCreacion = java.time.LocalDateTime.now()
                )
            )
        }
    }

    fun addTarea(tarea: Tarea) {
        viewModelScope.launch {
            updateTaskUseCase(tarea)
        }
    }

    fun deleteTarea(tarea: Tarea) {
        viewModelScope.launch {
            deleteEntityUseCase.deleteTarea(tarea)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addBienestar(tipo: String, valor: Float, meta: Float, unidad: String, icono: String) {
        viewModelScope.launch {
            addBienestarUseCase(
                Bienestar(
                    tipo = tipo,
                    valorActual = valor,
                    metaObjetivo = meta,
                    unidad = unidad,
                    iconoNombre = icono,
                    fecha = java.time.LocalDateTime.now()
                )
            )
        }
    }

    fun updateBienestar(registro: Bienestar, nuevoValor: Float) {
        viewModelScope.launch {
            addBienestarUseCase(registro.copy(valorActual = nuevoValor))
        }
    }
}
