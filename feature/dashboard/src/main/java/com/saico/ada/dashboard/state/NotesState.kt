package com.saico.ada.dashboard.state

import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea

sealed class NotesState {
    object Loading : NotesState()
    data class Success(
        val notas: List<Nota>,
        val todasLasTareas: List<Tarea>,
        val isMother: Boolean
    ) : NotesState()

    data class Error(val message: String) : NotesState()
}
