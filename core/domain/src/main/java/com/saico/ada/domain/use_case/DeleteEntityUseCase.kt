package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import javax.inject.Inject

class DeleteEntityUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val bienestarRepository: BienestarRepository,
    private val notaRepository: NotaRepository
) {
    suspend fun deleteTarea(tarea: Tarea) = tareaRepository.deleteTarea(tarea)
    suspend fun deleteBienestar(bienestar: Bienestar) = bienestarRepository.deleteRegistro(bienestar)
    suspend fun deleteNota(nota: Nota) = notaRepository.deleteNota(nota)
}
