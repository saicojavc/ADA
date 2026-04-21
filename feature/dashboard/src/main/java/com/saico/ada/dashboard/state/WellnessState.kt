package com.saico.ada.dashboard.state

import com.saico.ada.model.Bienestar
import com.saico.ada.model.Tarea

sealed class WellnessState {
    object Loading : WellnessState()
    data class Success(
        val registrosBienestar: List<Bienestar>,
        val balanceScore: Int,
        val horasSueno: Float,
        val tareasHoy: List<Tarea>,
        val isMother: Boolean
    ) : WellnessState()

    data class Error(val message: String) : WellnessState()
}
