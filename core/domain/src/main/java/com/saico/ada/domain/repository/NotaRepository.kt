package com.saico.ada.domain.repository

import com.saico.ada.model.Nota
import kotlinx.coroutines.flow.Flow

interface NotaRepository {
    fun getAllNotas(): Flow<List<Nota>>
    suspend fun insertNota(nota: Nota)
    suspend fun deleteNota(nota: Nota)
}
