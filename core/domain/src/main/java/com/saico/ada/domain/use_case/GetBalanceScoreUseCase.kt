package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetBalanceScoreUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val bienestarRepository: BienestarRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): Flow<Int> {
        return combine(
            tareaRepository.getAllTareas(),
            bienestarRepository.getAllRegistros()
        ) { tareas, registros ->
            val today = LocalDate.now()
            
            val tareasHoy = tareas.filter { it.fechaHoraInicio.toLocalDate() == today }
            val completedTasks = tareasHoy.count { it.estaCompletada }.toFloat()
            val totalTasks = tareasHoy.size.toFloat()

            val ritualesHoy = registros.filter { 
                it.fecha.toLocalDate() == today && 
                it.tipo !in listOf("Pasos", "Sueño") 
            }
            val completedRituals = ritualesHoy.count { it.valorActual >= it.metaObjetivo }.toFloat()
            val totalRituals = ritualesHoy.size.toFloat()

            // Fórmula de Equilibrio ADA: 
            // 50% peso en completar lo propuesto (Carga)
            // 50% peso en autocuidado realizado (Recuperación)
            val loadScore = if (totalTasks > 0) completedTasks / totalTasks else 1f
            val recoveryScore = if (totalRituals > 0) completedRituals / totalRituals else 0f
            
            // Si no hay tareas ni rituales, el equilibrio es neutro (50%)
            // A medida que haces rituales, el score sube.
            val finalScore = ((loadScore + recoveryScore) / 2f * 100f).toInt()
            finalScore.coerceIn(0, 100)
        }
    }
}
