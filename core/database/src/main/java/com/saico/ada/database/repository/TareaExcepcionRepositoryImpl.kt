package com.saico.ada.database.repository

import com.saico.ada.database.dao.TareaExcepcionDao
import com.saico.ada.database.mapper.toDomain
import com.saico.ada.database.mapper.toEntity
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.model.TareaExcepcion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TareaExcepcionRepositoryImpl @Inject constructor(
    private val dao: TareaExcepcionDao
) : TareaExcepcionRepository {
    override fun getExcepcionesByPlantilla(plantillaId: Int): Flow<List<TareaExcepcion>> {
        return dao.getExcepcionesByPlantilla(plantillaId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllExcepciones(): Flow<List<TareaExcepcion>> {
        return dao.getAllExcepciones().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun upsertExcepcion(excepcion: TareaExcepcion) {
        dao.upsertExcepcion(excepcion.toEntity())
    }

    override suspend fun deleteExcepcionesFuturas(plantillaId: Int, desde: LocalDate) {
        dao.deleteExcepcionesFuturas(plantillaId, desde)
    }
}
