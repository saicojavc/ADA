package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Bienestar
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DashboardData(
    val tareas: List<Tarea>,
    val registrosBienestar: List<Bienestar>,
    val notas: List<Nota>
)

class GetDashboardDataUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val bienestarRepository: BienestarRepository,
    private val notaRepository: NotaRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            tareaRepository.getAllTareas(),
            bienestarRepository.getAllRegistros(),
            notaRepository.getAllNotas()
        ) { tareas, bienestar, notas ->
            DashboardData(tareas, bienestar, notas)
        }
    }
}
