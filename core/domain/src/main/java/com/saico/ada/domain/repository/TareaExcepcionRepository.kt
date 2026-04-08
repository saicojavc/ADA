package com.saico.ada.domain.repository

import com.saico.ada.model.TareaExcepcion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TareaExcepcionRepository {
    fun getExcepcionesByPlantilla(plantillaId: Int): Flow<List<TareaExcepcion>>
    fun getAllExcepciones(): Flow<List<TareaExcepcion>>
    suspend fun upsertExcepcion(excepcion: TareaExcepcion)
    suspend fun deleteExcepcionesFuturas(plantillaId: Int, desde: LocalDate)
}
