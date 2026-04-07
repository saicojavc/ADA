package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.domain.use_case.*
import com.saico.ada.model.*
import com.saico.ada.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
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
    private val excepcionRepository: TareaExcepcionRepository,
    private val tareaRepository: TareaRepository
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
        val tareasHoyFinal = (tareasNormales.filter { it.fechaHoraInicio.toLocalDate() == today } + instanciasHoy)

        val instanciasAgenda = generateTareaInstancesUseCase(plantillas, data.excepciones, agendaDate)
        val tareasAgendaFinal = (tareasNormales.filter { it.fechaHoraInicio.toLocalDate() == agendaDate } + instanciasAgenda)

        val firstDayOfMonth = agendaDate.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = agendaDate.with(TemporalAdjusters.lastDayOfMonth())

        val todasLasInstanciasDelMes = mutableListOf<Tarea>()
        var currentLoopDate = firstDayOfMonth
        while (!currentLoopDate.isAfter(lastDayOfMonth)) {
            todasLasInstanciasDelMes.addAll(generateTareaInstancesUseCase(plantillas, data.excepciones, currentLoopDate))
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
            val pId = tarea.plantillaId
            if (pId != null && pId != 0) {
                if (cancelarFuturas) {
                    val hoy = LocalDate.now()
                    // 1. Borrar excepciones futuras que ya no sirven
                    excepcionRepository.deleteExcepcionesFuturas(pId, hoy)
                    // 2. Acortar la plantilla para que termine ayer (así deja de aparecer desde hoy)
                    val rawData = tareaRepository.getAllTareas().first()
                    val plantilla = rawData.find { it.id == pId }
                    plantilla?.let {
                        updateTaskUseCase(it.copy(fechaFinRepeticion = hoy.minusDays(1)))
                    }
                } else {
                    // Solo saltar este día específico
                    excepcionRepository.upsertExcepcion(
                        TareaExcepcion(
                            plantillaId = pId,
                            fecha = tarea.fechaHoraInicio.toLocalDate(),
                            estaSaltada = true
                        )
                    )
                }
            } else {
                // Tarea normal o borrar la plantilla completa del sistema
                deleteEntityUseCase.deleteTarea(tarea)
                alarmScheduler.cancel(tarea)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTarea(tarea: Tarea) {
        viewModelScope.launch {
            val taskToSave = if (tarea.plantillaId != null && tarea.plantillaId != 0 && tarea.id == 0) {
                tarea.copy(id = tarea.plantillaId!!)
            } else {
                tarea
            }
            val generatedId = updateTaskUseCase(taskToSave)
            alarmScheduler.cancel(taskToSave)
            alarmScheduler.schedule(taskToSave.copy(id = generatedId.toInt()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(titulo: String, contenido: String, colorHex: String) {
        viewModelScope.launch { addNoteUseCase(Nota(titulo = titulo, contenido = contenido, colorEtiquetaHex = colorHex, fechaCreacion = LocalDateTime.now())) }
    }

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
        viewModelScope.launch { addBienestarUseCase(Bienestar(tipo = nombre, valorActual = 0f, metaObjetivo = 1f, unidad = "u", iconoNombre = "star", fecha = LocalDateTime.now(), horaProgramada = hora)) }
    }
}
