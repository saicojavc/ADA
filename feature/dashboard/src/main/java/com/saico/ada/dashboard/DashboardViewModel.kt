package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.use_case.AddBienestarUseCase
import com.saico.ada.domain.use_case.AddNoteUseCase
import com.saico.ada.domain.use_case.DeleteEntityUseCase
import com.saico.ada.domain.use_case.GenerateTareaInstancesUseCase
import com.saico.ada.domain.use_case.GetDashboardDataUseCase
import com.saico.ada.domain.use_case.GetSmartSuggestionUseCase
import com.saico.ada.domain.use_case.MarcarTareaCompletadaUseCase
import com.saico.ada.domain.use_case.UpdateTaskUseCase
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
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
import java.time.temporal.TemporalAdjusters
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
    private val getSmartSuggestionUseCase: GetSmartSuggestionUseCase,
    private val marcarTareaCompletadaUseCase: MarcarTareaCompletadaUseCase,
    private val generateTareaInstancesUseCase: GenerateTareaInstancesUseCase,
    private val excepcionRepository: TareaExcepcionRepository
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedAgendaDate = MutableStateFlow(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedAgendaDate: StateFlow<LocalDate> = _selectedAgendaDate.asStateFlow()

    private val _agendaViewMode = MutableStateFlow(AgendaViewMode.SEMANAL)
    val agendaViewMode: StateFlow<AgendaViewMode> = _agendaViewMode.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val state: StateFlow<DashboardState> = combine(
        getDashboardDataUseCase(), _selectedAgendaDate, userPrefs.userName, userPrefs.isMother
    ) { data, agendaDate, userName, isMother ->
        val today = LocalDate.now()
        val now = LocalTime.now()

        val greetingRes = when (now.hour) {
            in 5..11 -> R.string.home_greeting_morning
            in 12..18 -> R.string.home_greeting_afternoon
            else -> R.string.home_greeting_evening
        }

        val plantillas = data.tareas.filter { it.esPlantilla }
        val tareasNormales = data.tareas.filter { !it.esPlantilla }

        val instanciasHoy = generateTareaInstancesUseCase(plantillas, data.excepciones, today)
        val tareasHoyFinal =
            (tareasNormales.filter { it.fechaHoraInicio.toLocalDate() == today } + instanciasHoy)

        val instanciasAgenda =
            generateTareaInstancesUseCase(plantillas, data.excepciones, agendaDate)
        val tareasAgendaFinal =
            (tareasNormales.filter { it.fechaHoraInicio.toLocalDate() == agendaDate } + instanciasAgenda)

        val firstDayOfMonth = agendaDate.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = agendaDate.with(TemporalAdjusters.lastDayOfMonth())

        val todasLasInstanciasDelMes = mutableListOf<Tarea>()
        var currentLoopDate = firstDayOfMonth
        while (!currentLoopDate.isAfter(lastDayOfMonth)) {
            todasLasInstanciasDelMes.addAll(
                generateTareaInstancesUseCase(
                    plantillas,
                    data.excepciones,
                    currentLoopDate
                )
            )
            currentLoopDate = currentLoopDate.plusDays(1)
        }

        val todasLasTareasVisibles = (tareasNormales.filter {
            !it.fechaHoraInicio.toLocalDate().isBefore(firstDayOfMonth) &&
                    !it.fechaHoraInicio.toLocalDate().isAfter(lastDayOfMonth)
        } + todasLasInstanciasDelMes)

        val suggestion = getSmartSuggestionUseCase(tareasHoyFinal)

        DashboardState.Success(
            tareasHoy = tareasHoyFinal,
            tareasAgenda = tareasAgendaFinal,
            todasLasTareas = todasLasTareasVisibles,
            registrosBienestar = data.registrosBienestar,
            notas = data.notas,
            userName = userName ?: "Jorge",
            greetingRes = greetingRes,
            isMother = isMother,
            adaSuggestionRes = suggestion.mensajeRes,
            adaActionRes = suggestion.accionRes,
            adaSuggestionArgs = suggestion.mensajeArgs,
            adaActionArgs = suggestion.accionArgs,
            suggestionType = suggestion.tipo
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
            if (tarea.plantillaId != null && tarea.plantillaId != 0) {
                if (cancelarFuturas) {
                    val hoy = LocalDate.now()
                    excepcionRepository.deleteExcepcionesFuturas(tarea.plantillaId!!, hoy)
                    val currentData = (state.value as? DashboardState.Success)
                    val plantilla = currentData?.todasLasTareas?.find { it.id == tarea.plantillaId }
                    if (plantilla != null) {
                        updateTaskUseCase(plantilla.copy(fechaFinRepeticion = hoy))
                    }
                } else {
                    excepcionRepository.upsertExcepcion(
                        TareaExcepcion(
                            plantillaId = tarea.plantillaId!!,
                            fecha = tarea.fechaHoraInicio.toLocalDate(),
                            estaSaltada = true
                        )
                    )
                }
            } else {
                deleteEntityUseCase.deleteTarea(tarea)
                alarmScheduler.cancel(tarea)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTarea(tarea: Tarea) {
        viewModelScope.launch {
            // Asegurar que si estamos editando una instancia, actualizamos la plantilla
            val taskToSave =
                if (tarea.plantillaId != null && tarea.plantillaId != 0 && tarea.id == 0) {
                    tarea.copy(id = tarea.plantillaId!!)
                } else {
                    tarea
                }

            val generatedId = updateTaskUseCase(taskToSave)

            // Reprogramar alarmas: cancelar las viejas y poner las nuevas
            alarmScheduler.cancel(taskToSave)
            alarmScheduler.schedule(taskToSave.copy(id = generatedId.toInt()))
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
                    tipo = nombre,
                    valorActual = 0f,
                    metaObjetivo = 1f,
                    unidad = "u",
                    iconoNombre = "star",
                    fecha = LocalDateTime.now(),
                    horaProgramada = hora
                )
            )
        }
    }
}
