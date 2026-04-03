package com.saico.ada.model

import java.time.LocalDateTime
import java.time.LocalTime

data class Bienestar(
    val id: Int = 0,
    val tipo: String,
    val valorActual: Float,
    val metaObjetivo: Float,
    val unidad: String,
    val fecha: LocalDateTime,
    val iconoNombre: String,
    val horaProgramada: LocalTime? = null // Nueva propiedad para la hora del ritual
)
