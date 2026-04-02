package com.saico.ada.ui.util

import androidx.compose.ui.graphics.Color

/**
 * Convierte una cadena hexadecimal (ej: "#E2725B") en un objeto Color de Compose.
 * Si el formato es inválido, devuelve Gray por defecto.
 */
fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color.Gray
    }
}

/**
 * Convierte un objeto Color de Compose en una cadena hexadecimal.
 */
fun Color.toHex(): String {
    return String.format("#%06X", 0xFFFFFF and this.value.toInt())
}
