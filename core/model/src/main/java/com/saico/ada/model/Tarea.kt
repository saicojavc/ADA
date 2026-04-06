package com.saico.ada.model

import java.time.LocalDateTime

data class Tarea(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String?,
    val fechaHoraInicio: LocalDateTime,
    val fechaHoraFin: LocalDateTime,
    val categoria: String,
    val colorHex: String,
    val estaCompletada: Boolean = false,
    val esPrioridadIA: Boolean = false
)
