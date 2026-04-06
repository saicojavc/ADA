package com.saico.ada.database.repository

import com.saico.ada.database.dao.TareaDao
import com.saico.ada.database.mapper.toDomain
import com.saico.ada.database.mapper.toEntity
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Tarea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TareaRepositoryImpl @Inject constructor(
    private val tareaDao: TareaDao
) : TareaRepository {
    override fun getAllTareas(): Flow<List<Tarea>> {
        return tareaDao.getAllTareas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsertTarea(tarea: Tarea): Long {
        return if (tarea.id == 0) {
            tareaDao.insertTarea(tarea.toEntity())
        } else {
            tareaDao.updateTarea(tarea.toEntity())
            tarea.id.toLong()
        }
    }

    override suspend fun deleteTarea(tarea: Tarea) {
        tareaDao.deleteTarea(tarea.toEntity())
    }
}
