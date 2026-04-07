package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetTasksForMonthUseCase @Inject constructor(
    private val generateTareaInstancesUseCase: GenerateTareaInstancesUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(tareas: List<Tarea>, excepciones: List<TareaExcepcion>, referenceDate: LocalDate): List<Tarea> {
        val firstDayOfMonth = referenceDate.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = referenceDate.with(TemporalAdjusters.lastDayOfMonth())
        
        val plantillas = tareas.filter { it.esPlantilla }
        val normales = tareas.filter { !it.esPlantilla }
        
        val todasLasInstanciasDelMes = mutableListOf<Tarea>()
        var currentLoopDate = firstDayOfMonth
        while (!currentLoopDate.isAfter(lastDayOfMonth)) {
            todasLasInstanciasDelMes.addAll(generateTareaInstancesUseCase(plantillas, excepciones, currentLoopDate))
            currentLoopDate = currentLoopDate.plusDays(1)
        }

        val tareasNormalesMes = normales.filter {
            !it.fechaHoraInicio.toLocalDate().isBefore(firstDayOfMonth) &&
            !it.fechaHoraInicio.toLocalDate().isAfter(lastDayOfMonth)
        }

        return (tareasNormalesMes + todasLasInstanciasDelMes).sortedBy { it.fechaHoraInicio }
    }
}
