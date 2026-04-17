package com.saico.ada.database.mapper

import com.saico.ada.database.entity.CategoriaEntity
import com.saico.ada.model.Categoria

fun CategoriaEntity.toExternal() = Categoria(
    id = id,
    nombre = nombre,
    colorHex = colorHex,
    esPersonalizada = esPersonalizada
)

fun Categoria.toEntity() = CategoriaEntity(
    id = id,
    nombre = nombre,
    colorHex = colorHex,
    esPersonalizada = esPersonalizada
)
