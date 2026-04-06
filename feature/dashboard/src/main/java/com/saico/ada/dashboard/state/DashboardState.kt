package com.saico.ada.dashboard.state

import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val tareasHoy: List<Tarea>,
        val tareasAgenda: List<Tarea>,
        val todasLasTareas: List<Tarea>,
        val registrosBienestar: List<Bienestar>,
        val notas: List<Nota>,
        val userName: String,
        val greeting: String,
        val isMother: Boolean // Nueva propiedad
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
