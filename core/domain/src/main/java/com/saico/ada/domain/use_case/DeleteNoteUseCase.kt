package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.model.Nota
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NotaRepository
) {
    suspend operator fun invoke(nota: Nota) {
        repository.deleteNota(nota)
    }
}
