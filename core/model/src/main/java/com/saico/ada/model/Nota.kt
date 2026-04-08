package com.saico.ada.model

import java.time.LocalDateTime

data class Nota(
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val fechaCreacion: LocalDateTime,
    val colorEtiquetaHex: String,
    val esIdeaBrillante: Boolean = false,
    val tareaId: Int? = null
)
