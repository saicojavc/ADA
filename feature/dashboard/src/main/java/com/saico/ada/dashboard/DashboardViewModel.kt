package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
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
    private val addBienestarUseCase: AddBienestarUseCase
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
        _selectedAgendaDate
    ) { data, agendaDate ->
        val today = LocalDate.now()

        val tareasHoy = data.tareas.filter { it.fechaHoraInicio.toLocalDate() == today }
        val tareasAgenda = data.tareas.filter { it.fechaHoraInicio.toLocalDate() == agendaDate }

        DashboardState.Success(
            tareasHoy = tareasHoy,
            tareasAgenda = tareasAgenda,
            todasLasTareas = data.tareas,
            registrosBienestar = data.registrosBienestar,
            notas = data.notas
        ) as DashboardState
    }
    .catch { e -> emit(DashboardState.Error(e.message ?: "Error desconocido")) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState.Loading)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAgendaDateSelected(date: LocalDate) { _selectedAgendaDate.value = date }
    fun onAgendaViewModeChanged(mode: AgendaViewMode) { _agendaViewMode.value = mode }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(titulo: String, contenido: String, colorHex: String) {
        viewModelScope.launch { addNoteUseCase(Nota(titulo = titulo, contenido = contenido, colorEtiquetaHex = colorHex, fechaCreacion = LocalDateTime.now())) }
    }

    fun addTarea(tarea: Tarea) { viewModelScope.launch { updateTaskUseCase(tarea) } }
    fun deleteTarea(tarea: Tarea) { viewModelScope.launch { deleteEntityUseCase.deleteTarea(tarea) } }

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
                    fecha = LocalDateTime.now()
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
