package com.saico.ada.domain.repository

import com.saico.ada.model.Bienestar
import kotlinx.coroutines.flow.Flow

interface BienestarRepository {
    fun getAllRegistros(): Flow<List<Bienestar>>
    suspend fun insertRegistro(registro: Bienestar)
    suspend fun deleteRegistro(registro: Bienestar)
}
