package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.CategoriaRepository
import com.saico.ada.model.Categoria
import javax.inject.Inject

class AddCategoriaUseCase @Inject constructor(
    private val repository: CategoriaRepository
) {
    suspend operator fun invoke(categoria: Categoria) {
        repository.insertCategoria(categoria)
    }
}
