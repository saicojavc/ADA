package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.model.Nota
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesByTaskIdUseCase @Inject constructor(
    private val repository: NotaRepository
) {
    operator fun invoke(taskId: Int): Flow<List<Nota>> {
        return repository.getNotasByTareaId(taskId)
    }
}
