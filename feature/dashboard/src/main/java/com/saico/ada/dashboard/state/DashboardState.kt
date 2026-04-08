package com.saico.ada.dashboard.state

import androidx.annotation.StringRes
import com.saico.ada.domain.use_case.SuggestionType
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
        @StringRes val greetingRes: Int,
        val isMother: Boolean,
        val adaSuggestionRes: Int,
        val adaActionRes: Int,
        val adaSuggestionArgs: List<Any> = emptyList(),
        val adaActionArgs: List<Any> = emptyList(),
        val suggestionType: SuggestionType,
        val balanceScore: Int,
        val horasSueno: Float = 0f
    ) : DashboardState()

    data class Error(val message: String) : DashboardState()
}
