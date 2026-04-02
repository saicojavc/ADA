package com.saico.ada.dashboard.state

import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val tareas: List<Tarea>,
        val registrosBienestar: List<Bienestar>,
        val notas: List<Nota>
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
