package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Tarea
import com.saico.ada.ui.R // Importamos los recursos de UI
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.min

private const val VENTANA_VENCIDA_MINUTOS = 120L

enum class SuggestionType { ACTION, REST, FOCUS, WELLBEING, CELEBRATION }

data class AdaSuggestion(
    val mensajeRes: Int,
    val accionRes: Int,
    val mensajeArgs: List<Any> = emptyList(),
    val accionArgs: List<Any> = emptyList(),
    val tipo: SuggestionType = SuggestionType.ACTION,
    val prioridad: Int = 0
)

private data class ScoreVector(
    val urgencia: Float = 0f,
    val energia: Float = 0f,
    val foco: Float = 0f,
    val contexto: Float = 0f,
    val tipo: SuggestionType,
    val mensajeRes: Int,
    val accionRes: Int,
    val mensajeArgs: List<Any> = emptyList(),
    val accionArgs: List<Any> = emptyList()
) {
    val score: Float get() = urgencia * 0.35f + energia * 0.25f + foco * 0.25f + contexto * 0.15f
}

object AdaHistory {
    val completionsByHour = IntArray(24)
    var currentStreak: Int = 0
    var lastActiveDate: LocalDate? = null
    val recentTypes = ArrayDeque<SuggestionType>(5)

    fun recordCompletion(hour: Int) { completionsByHour[hour.coerceIn(0, 23)]++ }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recordDayActive(date: LocalDate) {
        val last = lastActiveDate
        currentStreak = if (last != null && date.minusDays(1) == last) currentStreak + 1 else 1
        lastActiveDate = date
    }

    fun recordShown(tipo: SuggestionType) {
        if (recentTypes.size >= 5) recentTypes.removeFirst()
        recentTypes.addLast(tipo)
    }
}

private fun <T> ArrayDeque<T>.addLast(element: T): Unit = this.add(element).let {}
private fun <T> ArrayDeque<T>.removeFirst(): T = this.removeAt(0)

@RequiresApi(Build.VERSION_CODES.O)
private fun Tarea.esRealmenteVencida(now: LocalTime, today: LocalDate): Boolean {
    val horaFin = fechaHoraFin.toLocalTime()
    val minutosDesdeVencimiento = Duration.between(horaFin, now).toMinutes()
    return fechaHoraFin.toLocalDate() == today && horaFin.isBefore(now) && minutosDesdeVencimiento <= VENTANA_VENCIDA_MINUTOS && !estaCompletada
}

class GetSmartSuggestionUseCase @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(tareas: List<Tarea>): AdaSuggestion {
        val now = LocalTime.now()
        val today = LocalDate.now()
        val pendientes = tareas.filter { !it.estaCompletada }
        val vencidas = pendientes.filter { it.esRealmenteVencida(now, today) }
        val futuras = pendientes.filter { it.fechaHoraInicio.toLocalTime().isAfter(now) }.sortedBy { it.fechaHoraInicio }
        val completadasHoy = tareas.count { it.estaCompletada && it.fechaHoraInicio.toLocalDate() == today }

        val candidatos = mutableListOf<ScoreVector>()
        candidatos += analizarCarga(pendientes, vencidas)
        candidatos += analizarGaps(futuras, now)
        candidatos += analizarFranjaHoraria(now)
        candidatos += analizarBienestar(completadasHoy, pendientes.size)
        candidatos += analizarMomentum(today)

        val recentPenalty = 0.18f
        val scored = candidatos.map { v ->
            val penalizaciones = AdaHistory.recentTypes.count { it == v.tipo }
            v.copy(urgencia = v.urgencia - recentPenalty * penalizaciones)
        }

        val ganador = scored.maxByOrNull { it.score } ?: scored.first()
        AdaHistory.recordShown(ganador.tipo)

        return AdaSuggestion(
            mensajeRes = ganador.mensajeRes,
            accionRes = ganador.accionRes,
            mensajeArgs = ganador.mensajeArgs,
            accionArgs = ganador.accionArgs,
            tipo = ganador.tipo,
            prioridad = (ganador.score * 100).toInt()
        )
    }

    private fun analizarCarga(pendientes: List<Tarea>, vencidas: List<Tarea>): ScoreVector {
        val totalPendientes = pendientes.size
        val totalVencidas = vencidas.size
        val cargaBase = when {
            totalPendientes == 0 -> 0.0f
            totalPendientes <= 3 -> 0.2f
            totalPendientes <= 5 -> 0.45f
            totalPendientes <= 8 -> 0.75f
            else -> 0.95f
        }
        val urgenciaFinal = min(cargaBase + min(totalVencidas * 0.15f, 0.5f), 1.0f)

        val (msg, acc, msgArgs) = when {
            totalVencidas > 0 && totalPendientes > 6 -> Triple(R.string.sug_load_vencidas_pendientes, R.string.sug_load_vencidas_pendientes_action, listOf(totalVencidas, totalPendientes))
            totalVencidas > 0 -> Triple(R.string.sug_load_vencidas_only, R.string.sug_load_vencidas_only_action, listOf(totalVencidas))
            totalPendientes > 6 -> Triple(R.string.sug_load_high, R.string.sug_load_high_action, listOf(totalPendientes))
            totalPendientes in 4..6 -> Triple(R.string.sug_load_mid, R.string.sug_load_mid_action, listOf(totalPendientes))
            else -> Triple(R.string.sug_load_low, R.string.sug_load_low_action, listOf(totalPendientes))
        }

        return ScoreVector(urgencia = urgenciaFinal, tipo = SuggestionType.ACTION, mensajeRes = msg, accionRes = acc, mensajeArgs = msgArgs)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarGaps(futuras: List<Tarea>, now: LocalTime): ScoreVector {
        if (futuras.isEmpty()) return ScoreVector(tipo = SuggestionType.FOCUS, mensajeRes = R.string.home_analyzing, accionRes = R.string.home_preparing_suggestions)
        val proxima = futuras.first()
        val minGap = Duration.between(now, proxima.fechaHoraInicio.toLocalTime()).toMinutes()
        val valorGap = when (minGap) {
            in 0..9 -> 0.0f
            in 10..19 -> 0.3f
            in 20..50 -> 0.85f
            in 51..90 -> 0.6f
            else -> 0.2f
        }
        val (msg, acc) = when (minGap) {
            in 10..19 -> R.string.sug_gap_short to R.string.sug_gap_short_action
            in 20..50 -> R.string.sug_gap_ideal to R.string.sug_gap_ideal_action
            in 51..90 -> R.string.sug_gap_long to R.string.sug_gap_long_action
            else -> R.string.sug_gap_far to R.string.sug_gap_far_action
        }
        return ScoreVector(foco = valorGap, tipo = SuggestionType.FOCUS, mensajeRes = msg, accionRes = acc, mensajeArgs = listOf(minGap, proxima.titulo), accionArgs = listOf(proxima.titulo))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarFranjaHoraria(now: LocalTime): ScoreVector {
        val energia = when (now.hour) {
            in 5..6 -> 0.40f; in 7..9 -> 0.80f; in 10..11 -> 0.90f; in 12..13 -> 0.65f; in 14..15 -> 0.35f
            in 16..18 -> 0.70f; in 19..20 -> 0.50f; in 21..22 -> 0.30f; 23, 0 -> 0.15f; else -> 0.10f
        }
        val (tipo, msg, acc) = when (now.hour) {
            in 5..6 -> Triple(SuggestionType.WELLBEING, R.string.sug_time_early, R.string.sug_time_early_action)
            in 7..9 -> Triple(SuggestionType.ACTION, R.string.sug_time_morning, R.string.sug_time_morning_action)
            in 10..11 -> Triple(SuggestionType.FOCUS, R.string.sug_time_peak, R.string.sug_time_peak_action)
            in 12..13 -> Triple(SuggestionType.WELLBEING, R.string.sug_time_noon, R.string.sug_time_noon_action)
            in 14..15 -> Triple(SuggestionType.REST, R.string.sug_time_afternoon, R.string.sug_time_afternoon_action)
            in 16..18 -> Triple(SuggestionType.ACTION, R.string.sug_time_late, R.string.sug_time_late_action)
            in 19..20 -> Triple(SuggestionType.WELLBEING, R.string.sug_time_end, R.string.sug_time_end_action)
            in 21..22 -> Triple(SuggestionType.REST, R.string.sug_time_night, R.string.sug_time_night_action)
            else -> Triple(SuggestionType.REST, R.string.sug_time_sleep, R.string.sug_time_sleep_action)
        }
        return ScoreVector(energia = energia, tipo = tipo, mensajeRes = msg, accionRes = acc)
    }

    private fun analizarBienestar(completadas: Int, pendientes: Int): ScoreVector {
        val total = completadas + pendientes
        if (total == 0) return ScoreVector(tipo = SuggestionType.WELLBEING, mensajeRes = R.string.home_analyzing, accionRes = R.string.home_preparing_suggestions)
        val ratio = completadas.toFloat() / total
        val (msg, acc) = when {
            completadas == 0 -> R.string.sug_well_zero to R.string.sug_well_zero_action
            ratio >= 0.8f -> R.string.sug_well_high to R.string.sug_well_high_action
            ratio >= 0.5f -> R.string.sug_well_mid to R.string.sug_well_mid_action
            else -> R.string.sug_well_low to R.string.sug_well_low_action
        }
        val score = when { completadas == 0 -> 0.3f; ratio >= 0.8f -> 0.7f; ratio >= 0.5f -> 0.2f; else -> 0.4f }
        return ScoreVector(contexto = score, tipo = SuggestionType.WELLBEING, mensajeRes = msg, accionRes = acc, mensajeArgs = listOf(completadas, total))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarMomentum(today: LocalDate): ScoreVector {
        AdaHistory.recordDayActive(today)
        val streak = AdaHistory.currentStreak
        if (streak < 2) return ScoreVector(tipo = SuggestionType.ACTION, mensajeRes = R.string.home_analyzing, accionRes = R.string.home_preparing_suggestions)
        val (msg, acc) = when {
            streak >= 7 -> R.string.sug_mom_high to R.string.sug_mom_high_action
            streak >= 3 -> R.string.sug_mom_mid to R.string.sug_mom_mid_action
            else -> R.string.sug_mom_low to R.string.sug_mom_low_action
        }
        return ScoreVector(contexto = min(streak * 0.08f, 0.75f), tipo = SuggestionType.CELEBRATION, mensajeRes = msg, accionRes = acc, mensajeArgs = listOf(streak))
    }
}
