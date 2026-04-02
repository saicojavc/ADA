package com.saico.ada.model

import java.time.LocalDateTime

data class Bienestar(
    val id: Int = 0,
    val tipo: String,
    val valorActual: Float,
    val metaObjetivo: Float,
    val unidad: String,
    val fecha: LocalDateTime,
    val iconoNombre: String
)
