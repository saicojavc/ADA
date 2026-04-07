package com.saico.ada.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "tarea_excepciones",
    foreignKeys = [ForeignKey(
        entity = TareaEntity::class,
        parentColumns = ["id"],
        childColumns = ["plantillaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("plantillaId")]
)
data class TareaExcepcionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantillaId: Int,
    val fecha: LocalDate,
    val estaCompletada: Boolean = false,
    val estaSaltada: Boolean = false
)
