package com.saico.ada.database.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "bienestar_registros")
@RequiresApi(Build.VERSION_CODES.O)
data class BienestarEntity constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,
    val valorActual: Float,
    val metaObjetivo: Float,
    val unidad: String,
    val fecha: LocalDateTime = LocalDateTime.now(),
    val iconoNombre: String,
    val horaProgramada: LocalTime? = null // Nueva propiedad
)
