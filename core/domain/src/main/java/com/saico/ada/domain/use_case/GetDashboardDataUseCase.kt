package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.domain.repository.CategoriaRepository
import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Categoria
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import com.saico.ada.model.TareaExcepcion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DashboardData(
    val tareas: List<Tarea>,
    val registrosBienestar: List<Bienestar>,
    val notas: List<Nota>,
    val excepciones: List<TareaExcepcion>,
    val categorias: List<Categoria>
)

class GetDashboardDataUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val bienestarRepository: BienestarRepository,
    private val notaRepository: NotaRepository,
    private val excepcionRepository: TareaExcepcionRepository,
    private val categoriaRepository: CategoriaRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            tareaRepository.getAllTareas(),
            bienestarRepository.getAllRegistros(),
            notaRepository.getAllNotas(),
            excepcionRepository.getAllExcepciones(),
            categoriaRepository.getAllCategorias()
        ) { tareas, bienestar, notas, excepciones, categorias ->
            DashboardData(tareas, bienestar, notas, excepciones, categorias)
        }
    }
}
