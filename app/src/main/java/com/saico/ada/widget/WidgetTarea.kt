package com.saico.ada.widget

data class WidgetTarea(
    val id: Int,
    val titulo: String,
    val hora: String,       // formatted as "HH:mm"
    val colorHex: String,
    val estaCompletada: Boolean,
    val categoria: String
)
