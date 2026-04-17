package com.saico.ada.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.ada.model.TipoRepeticion
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String?,
    val fechaHoraInicio: LocalDateTime,
    val fechaHoraFin: LocalDateTime,
    val categoria: String,
    val colorHex: String,
    val estaCompletada: Boolean = false,
    val esPrioridadIA: Boolean = false,
    
    // Nuevos campos para repetición
    val esPlantilla: Boolean = false,
    val tipoRepeticion: TipoRepeticion = TipoRepeticion.NINGUNA,
    val diasRepeticion: List<DayOfWeek> = emptyList(),
    val horaInicio: LocalTime? = null,
    val duracionMinutos: Int = 60,
    val fechaInicioRepeticion: LocalDate? = null,
    val fechaFinRepeticion: LocalDate? = null,
    
    // Alarmas personalizadas
    val alarmasPersonalizadas: List<LocalDateTime> = emptyList()
)
