package com.saico.ada.database.mapper

import com.saico.ada.database.entity.BienestarEntity
import com.saico.ada.model.Bienestar

fun BienestarEntity.toDomain() = Bienestar(
    id = id,
    tipo = tipo,
    valorActual = valorActual,
    metaObjetivo = metaObjetivo,
    unidad = unidad,
    fecha = fecha,
    iconoNombre = iconoNombre
)

fun Bienestar.toEntity() = BienestarEntity(
    id = id,
    tipo = tipo,
    valorActual = valorActual,
    metaObjetivo = metaObjetivo,
    unidad = unidad,
    fecha = fecha,
    iconoNombre = iconoNombre
)
