package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Tarea
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(tarea: Tarea) {
        repository.upsertTarea(tarea)
    }
}
