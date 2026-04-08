package com.saico.ada.database.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.database.entity.NotaEntity
import com.saico.ada.model.Nota

fun NotaEntity.toDomain() = Nota(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaCreacion = fechaCreacion,
    colorEtiquetaHex = colorEtiquetaHex,
    esIdeaBrillante = esIdeaBrillante,
    tareaId = tareaId
)

@RequiresApi(Build.VERSION_CODES.O)
fun Nota.toEntity() = NotaEntity(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaCreacion = fechaCreacion,
    colorEtiquetaHex = colorEtiquetaHex,
    esIdeaBrillante = esIdeaBrillante,
    tareaId = tareaId
)
