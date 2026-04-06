package com.saico.ada.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String?,
    val fechaHoraInicio: LocalDateTime,
    val fechaHoraFin: LocalDateTime,
    val categoria: String, // "Trabajo", "Hogar", "Maternidad"
    val colorHex: String,   // Guardamos el color en Hex (ej: "#E2725B" para Terracota)
    val estaCompletada: Boolean = false,
    val esPrioridadIA: Boolean = false // Si ADA sugirió esta tarea como prioritaria
)
