package com.saico.ada.database.repository

import com.saico.ada.database.dao.BienestarDao
import com.saico.ada.database.mapper.toDomain
import com.saico.ada.database.mapper.toEntity
import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.model.Bienestar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BienestarRepositoryImpl @Inject constructor(
    private val bienestarDao: BienestarDao
) : BienestarRepository {
    override fun getAllRegistros(): Flow<List<Bienestar>> {
        return bienestarDao.getAllRegistros().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertRegistro(registro: Bienestar) {
        bienestarDao.insertRegistro(registro.toEntity())
    }

    override suspend fun deleteRegistro(registro: Bienestar) {
        bienestarDao.deleteRegistro(registro.toEntity())
    }
}
