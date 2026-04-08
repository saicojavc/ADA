package com.saico.ada.model

import java.time.LocalDate

data class TareaExcepcion(
    val id: Int = 0,
    val plantillaId: Int,         // FK a la Tarea plantilla
    val fecha: LocalDate,         // qué día es la excepción
    val estaCompletada: Boolean = false,
    val estaSaltada: Boolean = false
)
