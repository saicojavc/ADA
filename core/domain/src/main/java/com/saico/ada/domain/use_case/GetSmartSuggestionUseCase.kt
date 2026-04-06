package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Tarea
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.min

// ─────────────────────────────────────────
//  Constantes configurables
// ─────────────────────────────────────────

private const val VENTANA_VENCIDA_MINUTOS = 120L   // solo alerta si venció hace menos de 2h

// ─────────────────────────────────────────
//  Modelos de salida enriquecidos
// ─────────────────────────────────────────

enum class SuggestionType { ACTION, REST, FOCUS, WELLBEING, CELEBRATION }

data class AdaSuggestion(
    val mensaje: String,
    val accion: String,
    val tipo: SuggestionType = SuggestionType.ACTION,
    val prioridad: Int = 0          // mayor = más relevante (debug / logging)
)

// ─────────────────────────────────────────
//  ScoreVector – lo que cada analizador produce
// ─────────────────────────────────────────

private data class ScoreVector(
    val urgencia: Float  = 0f,   // carga de trabajo, tareas vencidas
    val energia: Float   = 0f,   // fatiga estimada, franja horaria
    val foco: Float      = 0f,   // gaps aprovechables, bloque continuo
    val contexto: Float  = 0f,   // momentum, racha activa
    val tipo: SuggestionType,
    val mensaje: String,
    val accion: String
) {
    val score: Float
        get() = urgencia * 0.35f + energia * 0.25f + foco * 0.25f + contexto * 0.15f
}

// ─────────────────────────────────────────
//  Historial liviano
//  (in-memory — reemplaza con Room/DataStore si quieres persistencia)
// ─────────────────────────────────────────

object AdaHistory {
    // Cuántas veces se completó una tarea por hora del día (índice = hora 0..23)
    val completionsByHour = IntArray(24)

    // Días consecutivos con al menos una tarea completada
    var currentStreak: Int = 0
    var lastActiveDate: LocalDate? = null

    // Tipos de sugerencia mostrados recientemente (evita repetir)
    val recentTypes = ArrayDeque<SuggestionType>(5)

    fun recordCompletion(hour: Int) {
        completionsByHour[hour.coerceIn(0, 23)]++
    }

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

// ─────────────────────────────────────────
//  Extensión: ¿es realmente una tarea urgente?
//
//  La UI tacha visualmente las tareas cuya hora ya pasó, dando a entender
//  que el usuario "ya las cumplió". Por eso NO las contamos como vencidas
//  a menos que:
//    1. Su fecha de fin sea hoy.
//    2. Hayan vencido hace MENOS de VENTANA_VENCIDA_MINUTOS (2h por defecto).
//    3. No estén marcadas explícitamente como completadas.
//
//  Una tarea que venció hace 3+ horas y el usuario no la tocó ya no es
//  accionable — ADA no debe seguir alertando por ella.
// ─────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
private fun Tarea.esRealmenteVencida(now: LocalTime, today: LocalDate): Boolean {
    val horaFin = fechaHoraFin.toLocalTime()
    val diaFin  = fechaHoraFin.toLocalDate()
    val minutosDesdeVencimiento = Duration.between(horaFin, now).toMinutes()

    return diaFin == today
            && horaFin.isBefore(now)
            && minutosDesdeVencimiento <= VENTANA_VENCIDA_MINUTOS
            && !estaCompletada
}

// ─────────────────────────────────────────
//  Use Case principal
// ─────────────────────────────────────────

class GetSmartSuggestionUseCase @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(tareas: List<Tarea>): AdaSuggestion {

        val now   = LocalTime.now()
        val today = LocalDate.now()

        // ── Datos base ───────────────────────────────────────────────────────

        // Tareas que el usuario NO ha marcado como completadas
        val pendientes = tareas.filter { !it.estaCompletada }

        // Vencidas: pasaron su hora HOY, hace menos de 2h, sin completar.
        // Las tareas que la UI tacha automáticamente por hora (sin acción del usuario)
        // quedan excluidas una vez superada la ventana, evitando falsas alertas.
        val vencidas = pendientes.filter { it.esRealmenteVencida(now, today) }

        // Futuras: aún no han llegado su hora de inicio
        val futuras = pendientes
            .filter { it.fechaHoraInicio.toLocalTime().isAfter(now) }
            .sortedBy { it.fechaHoraInicio }

        // Completadas hoy: solo las marcadas explícitamente por el usuario
        val completadasHoy = tareas.count {
            it.estaCompletada && it.fechaHoraInicio.toLocalDate() == today
        }

        // ── Analizadores (todos corren siempre) ─────────────────────────────
        val candidatos = mutableListOf<ScoreVector>()

        candidatos += analizarCarga(pendientes, vencidas, now)
        candidatos += analizarGaps(futuras, now)
        candidatos += analizarFranjaHoraria(now)
        candidatos += analizarBienestar(completadasHoy, pendientes.size)
        candidatos += analizarMomentum(completadasHoy, today)

        // ── Diversidad: penalizar tipos vistos recientemente ─────────────────
        val recentPenalty = 0.18f
        val scored = candidatos.map { v ->
            val penalizaciones = AdaHistory.recentTypes.count { it == v.tipo }
            v.copy(urgencia = v.urgencia - recentPenalty * penalizaciones)
        }

        // ── Elegir el ganador ────────────────────────────────────────────────
        val ganador = scored.maxByOrNull { it.score } ?: scored.first()

        AdaHistory.recordShown(ganador.tipo)

        return AdaSuggestion(
            mensaje   = ganador.mensaje,
            accion    = ganador.accion,
            tipo      = ganador.tipo,
            prioridad = (ganador.score * 100).toInt()
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ANALIZADOR 1 – Carga de trabajo
    //
    //  Evalúa cuántas tareas pendientes hay y cuántas están realmente vencidas
    //  (dentro de la ventana de 2h). Produce un score de URGENCIA.
    // ────────────────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarCarga(
        pendientes: List<Tarea>,
        vencidas: List<Tarea>,
        now: LocalTime
    ): ScoreVector {

        val totalPendientes = pendientes.size
        val totalVencidas   = vencidas.size

        // Score escalonado: sube suave hasta 5 tareas, luego sube con fuerza
        val cargaBase = when {
            totalPendientes == 0  -> 0.0f
            totalPendientes <= 3  -> 0.2f
            totalPendientes <= 5  -> 0.45f
            totalPendientes <= 8  -> 0.75f
            else                  -> 0.95f
        }

        // Las vencidas aumentan la urgencia drásticamente
        val urgenciaPorVencidas = min(totalVencidas * 0.15f, 0.5f)
        val urgenciaFinal       = min(cargaBase + urgenciaPorVencidas, 1.0f)

        val (mensaje, accion) = when {
            totalVencidas > 0 && totalPendientes > 6 ->
                "Tienes $totalVencidas tarea(s) vencida(s) y $totalPendientes pendientes." to
                        "ADA recomienda: atiende las vencidas primero y descarta o delega 3."

            totalVencidas > 0 ->
                "Hay $totalVencidas tarea(s) que ya pasaron su hora." to
                        "Revísalas ahora — ¿siguen siendo relevantes?"

            totalPendientes > 6 ->
                "Hoy tienes $totalPendientes tareas en la lista." to
                        "Elige tus 3 más importantes. El resto puede esperar."

            totalPendientes in 4..6 ->
                "Carga moderada: $totalPendientes tareas por delante." to
                        "Ordénalas por impacto antes de empezar."

            else ->
                "Lista ligera hoy — $totalPendientes tarea(s)." to
                        "Aprovecha el ritmo pausado para trabajar con profundidad."
        }

        return ScoreVector(
            urgencia = urgenciaFinal,
            energia  = 0f,
            foco     = 0f,
            contexto = 0f,
            tipo     = SuggestionType.ACTION,
            mensaje  = mensaje,
            accion   = accion
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ANALIZADOR 2 – Gaps de tiempo
    //
    //  Detecta huecos libres antes de la próxima tarea y sugiere cómo usarlos.
    //  El gap ideal es entre 20-50 min (suficiente para hacer algo sin perderse).
    // ────────────────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarGaps(futuras: List<Tarea>, now: LocalTime): ScoreVector {

        if (futuras.isEmpty()) return ScoreVector(
            tipo = SuggestionType.FOCUS, mensaje = "", accion = ""
        )

        val proxima    = futuras.first()
        val minutosGap = Duration.between(now, proxima.fechaHoraInicio.toLocalTime()).toMinutes()

        val valorGap = when (minutosGap) {
            in 0..9    -> 0.0f    // demasiado poco
            in 10..19  -> 0.3f    // alcanza para algo micro
            in 20..50  -> 0.85f   // gap ideal
            in 51..90  -> 0.6f    // gap largo, puede dispersarse
            else       -> 0.2f    // mucho tiempo, no hay urgencia por usarlo
        }

        val sugerenciaGap = when (minutosGap) {
            in 10..19 ->
                "Tienes $minutosGap min antes de '${proxima.titulo}'." to
                        "Haz algo micro: responde un mensaje, bebe agua, estira el cuello."

            in 20..50 ->
                "$minutosGap min libres antes de '${proxima.titulo}'." to
                        "Gap ideal para una sesión Pomodoro corta. ¿Qué quieres avanzar?"

            in 51..90 ->
                "Tienes $minutosGap min hasta '${proxima.titulo}'." to
                        "Bloque largo: entra en modo deep work. Cierra notificaciones."

            else ->
                "Tu próxima tarea, '${proxima.titulo}', está aún lejos." to
                        "Sin presión de tiempo — planifica el día con calma."
        }

        return ScoreVector(
            urgencia = 0f,
            energia  = 0f,
            foco     = valorGap,
            contexto = 0f,
            tipo     = SuggestionType.FOCUS,
            mensaje  = sugerenciaGap.first,
            accion   = sugerenciaGap.second
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ANALIZADOR 3 – Franja horaria + energía circadiana
    //
    //  Estima la energía disponible según la curva circadiana típica (10 tramos).
    //  Genera sugerencias contextuales de bienestar, foco o acción según la hora.
    // ────────────────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarFranjaHoraria(now: LocalTime): ScoreVector {

        val energiaEstimada = when (now.hour) {
            in 5..6   -> 0.40f   // despertar, energía baja
            in 7..9   -> 0.80f   // pico matutino
            in 10..11 -> 0.90f   // máximo cognitivo
            in 12..13 -> 0.65f   // post-almuerzo bajando
            in 14..15 -> 0.35f   // valle de la tarde
            in 16..18 -> 0.70f   // segundo pico
            in 19..20 -> 0.50f   // declinando
            in 21..22 -> 0.30f   // modo nocturno
            23, 0     -> 0.15f   // muy tarde
            else      -> 0.10f   // madrugada
        }

        val (tipo, mensaje, accion) = when (now.hour) {
            in 5..6   -> Triple(
                SuggestionType.WELLBEING,
                "El día acaba de comenzar.",
                "Activa el cuerpo antes que la mente — 5 min de movimiento."
            )
            in 7..9   -> Triple(
                SuggestionType.ACTION,
                "Mañana temprana: tu cerebro está en su mejor momento.",
                "Ataca la tarea más difícil ahora, antes de que lleguen interrupciones."
            )
            in 10..11 -> Triple(
                SuggestionType.FOCUS,
                "Pico cognitivo. Hora de trabajo profundo.",
                "Modo deep work: silencia todo y entra en flujo."
            )
            in 12..13 -> Triple(
                SuggestionType.WELLBEING,
                "Pausa de mediodía.",
                "Come sin pantallas y recuerda hidratarte bien."
            )
            in 14..15 -> Triple(
                SuggestionType.REST,
                "Valle de la tarde — energía baja.",
                "Nap de 10-20 min o un paseo corto lo revierten."
            )
            in 16..18 -> Triple(
                SuggestionType.ACTION,
                "Segundo pico de la tarde.",
                "Buen momento para reuniones, revisiones o tareas creativas."
            )
            in 19..20 -> Triple(
                SuggestionType.WELLBEING,
                "El día productivo termina.",
                "Cierra pendientes y prepara tu lista de mañana."
            )
            in 21..22 -> Triple(
                SuggestionType.REST,
                "Hora de desconectar.",
                "Brain dump rápido: vacía la mente antes de dormir."
            )
            else      -> Triple(
                SuggestionType.REST,
                "Deberías estar descansando.",
                "Duerme — tu productividad mañana depende de esto."
            )
        }

        return ScoreVector(
            urgencia = 0f,
            energia  = energiaEstimada,
            foco     = 0f,
            contexto = 0f,
            tipo     = tipo,
            mensaje  = mensaje,
            accion   = accion
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ANALIZADOR 4 – Bienestar basado en ratio completadas/pendientes
    //
    //  Motiva al usuario según cuánto ha avanzado en el día.
    //  Score de CONTEXTO: celebra logros o empuja a empezar.
    // ────────────────────────────────────────────────────────────────────────

    private fun analizarBienestar(completadas: Int, pendientes: Int): ScoreVector {

        val total = completadas + pendientes
        if (total == 0) return ScoreVector(
            tipo = SuggestionType.WELLBEING, mensaje = "", accion = ""
        )

        val ratio = completadas.toFloat() / total

        val (mensaje, accion) = when {
            completadas == 0 && pendientes > 0 ->
                "Aún no has tachado nada hoy." to
                        "Empieza por la tarea más pequeña — el momentum comienza con una."

            ratio >= 0.8f ->
                "Llevas $completadas de $total tareas completadas hoy." to
                        "Estás arrasando. Tómate un momento para reconocerlo."

            ratio >= 0.5f ->
                "Vas a la mitad — $completadas de $total listas." to
                        "Buen ritmo. Mantén el foco en las siguientes más importantes."

            else ->
                "Todavía queda terreno por recorrer hoy." to
                        "Una tarea a la vez. ¿Cuál es la siguiente acción concreta?"
        }

        val bienestarScore = when {
            completadas == 0 -> 0.3f   // empujar a empezar
            ratio >= 0.8f    -> 0.7f   // celebrar
            ratio >= 0.5f    -> 0.2f   // está bien, no urge
            else             -> 0.4f   // motivar
        }

        return ScoreVector(
            urgencia = 0f,
            energia  = 0f,
            foco     = 0f,
            contexto = bienestarScore,
            tipo     = SuggestionType.WELLBEING,
            mensaje  = mensaje,
            accion   = accion
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ANALIZADOR 5 – Momentum y racha histórica
    //
    //  Premia rachas de días consecutivos activos.
    //  A partir de 2 días seguidos genera un ScoreVector de CELEBRATION.
    // ────────────────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analizarMomentum(completadasHoy: Int, today: LocalDate): ScoreVector {

        AdaHistory.recordDayActive(today)
        val streak = AdaHistory.currentStreak

        if (streak < 2) return ScoreVector(
            tipo = SuggestionType.ACTION, mensaje = "", accion = ""
        )

        val momentumScore = min(streak * 0.08f, 0.75f)

        val (mensaje, accion) = when {
            streak >= 7 ->
                "¡$streak días consecutivos activo!" to
                        "Eres imparable. Protege esa racha — haz aunque sea una tarea hoy."

            streak >= 3 ->
                "$streak días seguidos avanzando." to
                        "El hábito se está formando. No rompas la cadena."

            else ->
                "Llevas $streak días activos." to
                        "Buen comienzo de racha. Sigue así."
        }

        return ScoreVector(
            urgencia = 0f,
            energia  = 0f,
            foco     = 0f,
            contexto = momentumScore,
            tipo     = SuggestionType.CELEBRATION,
            mensaje  = mensaje,
            accion   = accion
        )
    }
}