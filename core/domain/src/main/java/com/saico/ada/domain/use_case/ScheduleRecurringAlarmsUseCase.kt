package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.model.Tarea
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ScheduleRecurringAlarmsUseCase @Inject constructor(
    private val generateTareaInstancesUseCase: GenerateTareaInstancesUseCase,
    private val alarmScheduler: AlarmScheduler
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(plantilla: Tarea) {
        val today = LocalDate.now()
        val start = plantilla.fechaInicioRepeticion ?: today
        val endLimit = today.plusDays(30)
        val end = if (plantilla.fechaFinRepeticion != null && plantilla.fechaFinRepeticion!!.isBefore(endLimit)) {
            plantilla.fechaFinRepeticion!!
        } else {
            endLimit
        }

        var current = start
        while (!current.isAfter(end)) {
            val instances = generateTareaInstancesUseCase(listOf(plantilla), emptyList(), current)
            instances.forEach { instance ->
                if (instance.fechaHoraInicio.isAfter(LocalDateTime.now())) {
                    val dayOffset = ChronoUnit.DAYS.between(start, current)
                    val uniqueId = (plantilla.id * 1000 + dayOffset).toInt()
                    // Set esPlantilla to false so the scheduler treats it as a single instance for that specific date
                    alarmScheduler.schedule(instance.copy(id = uniqueId, esPlantilla = false))
                }
            }
            current = current.plusDays(1)
        }
    }
}
