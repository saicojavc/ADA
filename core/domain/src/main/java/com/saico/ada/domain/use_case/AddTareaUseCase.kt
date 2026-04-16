package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.model.Tarea
import javax.inject.Inject

class AddTareaUseCase @Inject constructor(
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val scheduleRecurringAlarmsUseCase: ScheduleRecurringAlarmsUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(tarea: Tarea) {
        val taskToSave = if (tarea.plantillaId != null && tarea.plantillaId != 0 && tarea.id == 0) {
            tarea.copy(id = tarea.plantillaId!!)
        } else {
            tarea
        }
        val generatedId = updateTaskUseCase(taskToSave)
        val finalTask = taskToSave.copy(id = generatedId.toInt())
        alarmScheduler.cancel(finalTask)
        alarmScheduler.schedule(finalTask)

        if (finalTask.esPlantilla) {
            scheduleRecurringAlarmsUseCase(finalTask)
        }
    }
}
