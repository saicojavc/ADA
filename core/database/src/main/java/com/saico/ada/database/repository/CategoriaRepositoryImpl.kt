package com.saico.ada.database.repository

import com.saico.ada.database.dao.CategoriaDao
import com.saico.ada.database.mapper.toEntity
import com.saico.ada.database.mapper.toExternal
import com.saico.ada.domain.repository.CategoriaRepository
import com.saico.ada.model.Categoria
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val dao: CategoriaDao
) : CategoriaRepository {
    override fun getAllCategorias(): Flow<List<Categoria>> {
        return dao.getAllCategorias().map { entities ->
            entities.map { it.toExternal() }
        }
    }

    override suspend fun insertCategoria(categoria: Categoria) {
         dao.insertCategoria(categoria.toEntity())
    }

    override suspend fun deleteCategoria(categoria: Categoria) {
        dao.deleteCategoria(categoria.toEntity())
    }
}
