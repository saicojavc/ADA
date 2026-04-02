package com.saico.ada.database.mapper

import com.saico.ada.database.entity.NotaEntity
import com.saico.ada.model.Nota

fun NotaEntity.toDomain() = Nota(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaCreacion = fechaCreacion,
    colorEtiquetaHex = colorEtiquetaHex,
    esIdeaBrillante = esIdeaBrillante
)

fun Nota.toEntity() = NotaEntity(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaCreacion = fechaCreacion,
    colorEtiquetaHex = colorEtiquetaHex,
    esIdeaBrillante = esIdeaBrillante
)
