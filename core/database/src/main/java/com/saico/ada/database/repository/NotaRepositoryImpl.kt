package com.saico.ada.database.repository

import com.saico.ada.database.dao.NotaDao
import com.saico.ada.database.mapper.toDomain
import com.saico.ada.database.mapper.toEntity
import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.model.Nota
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotaRepositoryImpl @Inject constructor(
    private val notaDao: NotaDao
) : NotaRepository {
    override fun getAllNotas(): Flow<List<Nota>> {
        return notaDao.getAllNotas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotasByTareaId(taskId: Int): Flow<List<Nota>> {
        return notaDao.getNotasByTareaId(taskId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNota(nota: Nota) {
        notaDao.insertNota(nota.toEntity())
    }

    override suspend fun deleteNota(nota: Nota) {
        notaDao.deleteNota(nota.toEntity())
    }
}
