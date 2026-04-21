package com.saico.ada.dashboard.state

import androidx.annotation.StringRes
import com.saico.ada.domain.use_case.SuggestionType
import com.saico.ada.model.Categoria
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val tareasHoy: List<Tarea>,
        val userName: String,
        @StringRes val greetingRes: Int,
        val isMother: Boolean,
        val adaSuggestionRes: Int,
        val adaActionRes: Int,
        val adaSuggestionArgs: List<Any> = emptyList(),
        val adaActionArgs: List<Any> = emptyList(),
        val suggestionType: SuggestionType,
        val horasSueno: Float,
        val categorias: List<Categoria> = emptyList(),
        val notas: List<Nota> = emptyList()
    ) : HomeState()

    data class Error(val message: String) : HomeState()
}
