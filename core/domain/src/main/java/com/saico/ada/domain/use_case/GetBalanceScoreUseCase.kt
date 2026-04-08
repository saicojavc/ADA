package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.model.Tarea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetBalanceScoreUseCase @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val bienestarRepository: BienestarRepository,
    private val excepcionRepository: TareaExcepcionRepository,
    private val generateTareaInstancesUseCase: GenerateTareaInstancesUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): Flow<Int> {
        return combine(
            tareaRepository.getAllTareas(),
            excepcionRepository.getAllExcepciones()
        ) { allTareas, excepciones ->
            val today = LocalDate.now()
            
            // 1. Obtener tareas normales de hoy
            val tareasNormalesHoy = allTareas.filter { !it.esPlantilla && it.fechaHoraInicio.toLocalDate() == today }
            
            // 2. Generar instancias de hoy a partir de plantillas
            val plantillas = allTareas.filter { it.esPlantilla }
            val instanciasHoy = generateTareaInstancesUseCase(plantillas, excepciones, today)
            
            // 3. Combinar todas las tareas de hoy
            val todasLasTareasHoy = tareasNormalesHoy + instanciasHoy

            // 4. Calcular balance basado en categorías
            // Normalizamos la comparación para evitar problemas con traducciones o ingresos manuales
            val bienestarKeywords = listOf("bienestar", "wellbeing")
            
            val tareasBienestar = todasLasTareasHoy.count { it.categoria.lowercase() in bienestarKeywords }
            val tareasCarga = todasLasTareasHoy.count { it.categoria.lowercase() !in bienestarKeywords }
            
            val total = (tareasCarga + tareasBienestar).coerceAtLeast(1)
            
            // Si hay un equilibrio (ej. 50/50), el score debería ser alto (100).
            val ratioBienestar = tareasBienestar.toFloat() / total.toFloat()
            
            val diff = Math.abs(0.5f - ratioBienestar)
            val score = (100 - (diff * 200)).toInt()
            
            score.coerceIn(0, 100)
        }
    }
}
