package com.saico.ada.common.util

/**
 * Normaliza un string eliminando tildes y convirtiéndolo a minúsculas
 * para facilitar comparaciones y búsquedas.
 */
fun String.normalize(): String {
    return this.lowercase()
        .replace('á', 'a').replace('é', 'e').replace('í', 'i')
        .replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
        .replace('ñ', 'n').replace('à', 'a').replace('è', 'e')
        .replace('ì', 'i').replace('ò', 'o').replace('ù', 'u')
}
