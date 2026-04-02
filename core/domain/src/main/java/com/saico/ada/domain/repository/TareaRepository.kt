package com.saico.ada.domain.repository

import com.saico.ada.model.Tarea
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun getAllTareas(): Flow<List<Tarea>>
    suspend fun upsertTarea(tarea: Tarea)
    suspend fun deleteTarea(tarea: Tarea)
}
