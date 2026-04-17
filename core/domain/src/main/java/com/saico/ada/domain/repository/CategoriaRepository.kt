package com.saico.ada.domain.repository

import com.saico.ada.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun getAllCategorias(): Flow<List<Categoria>>
    suspend fun insertCategoria(categoria: Categoria)
    suspend fun deleteCategoria(categoria: Categoria)
}
