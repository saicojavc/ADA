package com.saico.ada.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "bienestar_registros")
data class BienestarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,       // "Hidratación", "Pasos", "Skincare", "Sueño"
    val valorActual: Float, // ej: 600 (ml) u 8000 (pasos)
    val metaObjetivo: Float, // ej: 1000 (ml) o 10000 (pasos)
    val unidad: String,     // "ml", "pasos", "horas"
    val fecha: LocalDateTime = LocalDateTime.now(),
    val iconoNombre: String // Referencia al icono (ej: "water_drop")
)
