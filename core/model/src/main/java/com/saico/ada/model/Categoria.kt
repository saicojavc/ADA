package com.saico.ada.model

data class Categoria(
    val id: Int = 0,
    val nombre: String,
    val colorHex: String,
    val esPersonalizada: Boolean = true
)
