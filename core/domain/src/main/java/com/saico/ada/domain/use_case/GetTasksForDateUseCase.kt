package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
import java.time.LocalDate
import javax.inject.Inject

class GetTasksForDateUseCase @Inject constructor(
    private val generateTareaInstancesUseCase: GenerateTareaInstancesUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(tareas: List<Tarea>, excepciones: List<TareaExcepcion>, date: LocalDate): List<Tarea> {
        val plantillas = tareas.filter { it.esPlantilla }
        val normales = tareas.filter { !it.esPlantilla }
        val instancias = generateTareaInstancesUseCase(plantillas, excepciones, date)
        return (normales.filter { it.fechaHoraInicio.toLocalDate() == date } + instancias)
            .sortedBy { it.fechaHoraInicio }
    }
}
