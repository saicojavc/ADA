package com.saico.ada.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notas_rapidas")
data class NotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val fechaCreacion: LocalDateTime = LocalDateTime.now(),
    val colorEtiquetaHex: String, // Color del "Post-it"
    val esIdeaBrillante: Boolean = false // Un flag para notas que resaltan
)
