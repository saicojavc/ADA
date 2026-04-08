package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class DeleteTareaUseCase @Inject constructor(
    private val repository: TareaRepository,
    private val excepcionRepository: TareaExcepcionRepository,
    private val deleteEntityUseCase: DeleteEntityUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val alarmScheduler: AlarmScheduler
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(tarea: Tarea, cancelarFuturas: Boolean = false) {
        val pId = tarea.plantillaId
        if (pId != null && pId != 0) {
            if (cancelarFuturas) {
                val hoy = LocalDate.now()
                excepcionRepository.deleteExcepcionesFuturas(pId, hoy)
                val rawData = repository.getAllTareas().first()
                val plantilla = rawData.find { it.id == pId }
                plantilla?.let {
                    updateTaskUseCase(it.copy(fechaFinRepeticion = hoy.minusDays(1)))
                }
            } else {
                excepcionRepository.upsertExcepcion(
                    TareaExcepcion(
                        plantillaId = pId,
                        fecha = tarea.fechaHoraInicio.toLocalDate(),
                        estaSaltada = true
                    )
                )
            }
        } else {
            deleteEntityUseCase.deleteTarea(tarea)
            alarmScheduler.cancel(tarea)
        }
    }
}
