package com.saico.ada.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.domain.use_case.AddNoteUseCase
import com.saico.ada.domain.use_case.DeleteEntityUseCase
import com.saico.ada.domain.use_case.GetDashboardDataUseCase
import com.saico.ada.domain.use_case.UpdateTaskUseCase
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteEntityUseCase: DeleteEntityUseCase
) : ViewModel() {

    val state: StateFlow<DashboardState> = getDashboardDataUseCase()
        .map { data ->
            DashboardState.Success(
                tareas = data.tareas,
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

    fun addNote(nota: Nota) {
        viewModelScope.launch {
            addNoteUseCase(nota)
        }
    }

    fun updateTarea(tarea: Tarea) {
        viewModelScope.launch {
            updateTaskUseCase(tarea)
        }
    }

    fun deleteTarea(tarea: Tarea) {
        viewModelScope.launch {
            deleteEntityUseCase.deleteTarea(tarea)
        }
    }

    fun deleteNota(nota: Nota) {
        viewModelScope.launch {
            deleteEntityUseCase.deleteNota(nota)
        }
    }

    fun deleteBienestar(bienestar: Bienestar) {
        viewModelScope.launch {
            deleteEntityUseCase.deleteBienestar(bienestar)
        }
    }
}
