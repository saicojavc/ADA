package com.saico.ada.database.mapper

import com.saico.ada.database.entity.TareaEntity
import com.saico.ada.model.Tarea

fun TareaEntity.toDomain() = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fechaHoraInicio = fechaHoraInicio,
    fechaHoraFin = fechaHoraFin,
    categoria = categoria,
    colorHex = colorHex,
    estaCompletada = estaCompletada,
    esPrioridadIA = esPrioridadIA
)

fun Tarea.toEntity() = TareaEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fechaHoraInicio = fechaHoraInicio,
    fechaHoraFin = fechaHoraFin,
    categoria = categoria,
    colorHex = colorHex,
    estaCompletada = estaCompletada,
    esPrioridadIA = esPrioridadIA
)
