package com.saico.ada.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Tarea(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String?,
    val fechaHoraInicio: LocalDateTime,
    val fechaHoraFin: LocalDateTime,
    val categoria: String,
    val colorHex: String,
    val estaCompletada: Boolean = false,
    val esPrioridadIA: Boolean = false,
    // Nuevos campos para repetición
    val esPlantilla: Boolean = false,
    val tipoRepeticion: TipoRepeticion = TipoRepeticion.NINGUNA,
    val diasRepeticion: List<DayOfWeek> = emptyList(),
    val horaInicio: LocalTime? = null,
    val duracionMinutos: Int = 60,
    val fechaInicioRepeticion: LocalDate? = null,
    val fechaFinRepeticion: LocalDate? = null,
    val plantillaId: Int? = null
)
