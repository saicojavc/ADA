package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
import com.saico.ada.model.TipoRepeticion
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GenerateTareaInstancesUseCase @Inject constructor() {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(
        plantillas: List<Tarea>,
        excepciones: List<TareaExcepcion>,
        fecha: LocalDate
    ): List<Tarea> {
        return plantillas.filter { it.esPlantilla }.mapNotNull { plantilla ->
            // Verificar si la fecha está dentro del rango de la plantilla
            val inicio = plantilla.fechaInicioRepeticion ?: return@mapNotNull null
            val fin = plantilla.fechaFinRepeticion ?: inicio.plusYears(1)

            if (fecha.isBefore(inicio) || fecha.isAfter(fin)) return@mapNotNull null

            // Verificar si el día de la semana coincide si es DIAS_ESPECIFICOS
            when (plantilla.tipoRepeticion) {
                TipoRepeticion.TODOS_LOS_DIAS -> { /* Aplica */ }
                TipoRepeticion.DIAS_ESPECIFICOS -> {
                    if (fecha.dayOfWeek !in plantilla.diasRepeticion) return@mapNotNull null
                }
                TipoRepeticion.NINGUNA -> return@mapNotNull null
            }

            // Verificar excepciones
            val excepcion = excepciones.find { it.plantillaId == plantilla.id && it.fecha == fecha }
            if (excepcion?.estaSaltada == true) return@mapNotNull null

            val horaInicio = plantilla.horaInicio ?: plantilla.fechaHoraInicio.toLocalTime()
            val fechaHoraInicio = LocalDateTime.of(fecha, horaInicio)
            
            Tarea(
                id = 0, // Instancia en memoria
                titulo = plantilla.titulo,
                descripcion = plantilla.descripcion,
                fechaHoraInicio = fechaHoraInicio,
                fechaHoraFin = fechaHoraInicio.plusMinutes(plantilla.duracionMinutos.toLong()),
                categoria = plantilla.categoria,
                colorHex = plantilla.colorHex,
                estaCompletada = excepcion?.estaCompletada ?: false,
                esPrioridadIA = plantilla.esPrioridadIA,
                esPlantilla = false,
                plantillaId = plantilla.id
            )
        }
    }
}
