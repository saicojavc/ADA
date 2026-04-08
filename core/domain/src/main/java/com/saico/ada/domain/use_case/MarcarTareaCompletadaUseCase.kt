package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
import java.time.LocalDate
import javax.inject.Inject

class MarcarTareaCompletadaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val excepcionRepository: TareaExcepcionRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(tarea: Tarea, completada: Boolean = true) {
        val plantillaId = tarea.plantillaId
        if (plantillaId != null && plantillaId != 0) {
            // Es una instancia de una plantilla
            excepcionRepository.upsertExcepcion(
                TareaExcepcion(
                    plantillaId = plantillaId,
                    fecha = tarea.fechaHoraInicio.toLocalDate(),
                    estaCompletada = completada,
                    estaSaltada = false
                )
            )
        } else {
            // Es una tarea normal
            tareaRepository.upsertTarea(tarea.copy(estaCompletada = completada))
        }
    }
}
