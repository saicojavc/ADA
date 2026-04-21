package com.saico.ada.dashboard.state

import com.saico.ada.dashboard.AgendaViewMode
import com.saico.ada.model.Categoria
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import java.time.LocalDate

sealed class AgendaState {
    object Loading : AgendaState()
    data class Success(
        val tareasDelDia: List<Tarea>,
        val todasLasTareas: List<Tarea>,
        val notas: List<Nota>,
        val categorias: List<Categoria>,
        val isMother: Boolean,
        val selectedDate: LocalDate,
        val viewMode: AgendaViewMode
    ) : AgendaState()

    data class Error(val message: String) : AgendaState()
}
