package com.saico.ada.database.mapper

import com.saico.ada.database.entity.TareaExcepcionEntity
import com.saico.ada.model.TareaExcepcion

fun TareaExcepcionEntity.toDomain() = TareaExcepcion(
    id = id,
    plantillaId = plantillaId,
    fecha = fecha,
    estaCompletada = estaCompletada,
    estaSaltada = estaSaltada
)

fun TareaExcepcion.toEntity() = TareaExcepcionEntity(
    id = id,
    plantillaId = plantillaId,
    fecha = fecha,
    estaCompletada = estaCompletada,
    estaSaltada = estaSaltada
)
