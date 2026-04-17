package com.saico.ada.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.model.Tarea
import com.saico.ada.model.TipoRepeticion
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    companion object {
        private const val REMINDER_MINUTES = 30L
        private const val OFFSET_EXACT = 0
        private const val OFFSET_EARLY = 1
        private const val OFFSET_CUSTOM = 100 // Alarms starting from 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun schedule(tarea: Tarea) {
        // Cancelar previas para evitar duplicados si se edita
        cancel(tarea)

        if (tarea.esPlantilla) {
            scheduleNextRepeatingOccurrence(tarea)
        } else {
            scheduleSingleAlarm(tarea)
        }

        // Programar alarmas personalizadas
        tarea.alarmasPersonalizadas.forEachIndexed { index, dateTime ->
            val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if (triggerTime > System.currentTimeMillis()) {
                val intent = createBaseIntent(tarea, "ACTION_TASK_CUSTOM_${tarea.id}_$index")
                scheduleExact(triggerTime, (tarea.id * 1000) + OFFSET_CUSTOM + index, intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleSingleAlarm(tarea: Tarea) {
        val timeExact = tarea.fechaHoraInicio.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (timeExact > System.currentTimeMillis()) {
            val intent = createBaseIntent(tarea, "ACTION_TASK_EXACT_${tarea.id}")
            scheduleExact(timeExact, (tarea.id * 1000) + OFFSET_EXACT, intent)
        }

        val timeEarly = tarea.fechaHoraInicio.minusMinutes(REMINDER_MINUTES).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (timeEarly > System.currentTimeMillis()) {
            val earlyIntent = createBaseIntent(tarea, "ACTION_TASK_EARLY_${tarea.id}").apply {
                putExtra("EXTRA_IS_EARLY", true)
            }
            scheduleExact(timeEarly, (tarea.id * 1000) + OFFSET_EARLY, earlyIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNextRepeatingOccurrence(tarea: Tarea) {
        val horaBase = tarea.horaInicio ?: tarea.fechaHoraInicio.toLocalTime()
        val nextOccurrence = when (tarea.tipoRepeticion) {
            TipoRepeticion.TODOS_LOS_DIAS -> getNextOccurrence(LocalDate.now(), horaBase)
            TipoRepeticion.DIAS_ESPECIFICOS -> getNextOccurrenceOfSpecificDays(tarea.diasRepeticion, horaBase)
            else -> return
        }

        val triggerTime = nextOccurrence.toInstant().toEpochMilli()
        val intent = createBaseIntent(tarea, "ACTION_REP_EXACT_${tarea.id}")
        scheduleExact(triggerTime, (tarea.id * 1000) + OFFSET_EXACT, intent)

        val earlyTrigger = nextOccurrence.minusMinutes(REMINDER_MINUTES).toInstant().toEpochMilli()
        if (earlyTrigger > System.currentTimeMillis()) {
            val earlyIntent = createBaseIntent(tarea, "ACTION_REP_EARLY_${tarea.id}").apply {
                putExtra("EXTRA_IS_EARLY", true)
            }
            scheduleExact(earlyTrigger, (tarea.id * 1000) + OFFSET_EARLY, earlyIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBaseIntent(tarea: Tarea, action: String): Intent {
        return Intent(context, TaskAlarmReceiver::class.java).apply {
            this.action = action
            putExtra("EXTRA_TASK_ID", tarea.id)
            putExtra("EXTRA_TASK_TITLE", tarea.titulo)
            putExtra("EXTRA_TASK_CATEGORY", tarea.categoria)
            putExtra("EXTRA_TASK_TIME", (tarea.horaInicio ?: tarea.fechaHoraInicio.toLocalTime()).toString())
            putExtra("EXTRA_IS_REPEATING", tarea.esPlantilla)
        }
    }

    private fun scheduleExact(time: Long, requestCode: Int, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNextOccurrence(startDate: LocalDate, time: LocalTime): ZonedDateTime {
        var next = LocalDateTime.of(startDate, time).atZone(ZoneId.systemDefault())
        if (next.toInstant().toEpochMilli() <= System.currentTimeMillis()) {
            next = next.plusDays(1)
        }
        return next
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNextOccurrenceOfSpecificDays(days: List<DayOfWeek>, time: LocalTime): ZonedDateTime {
        var checkDate = LocalDate.now()
        for (i in 0..7) {
            if (checkDate.dayOfWeek in days) {
                val zdt = LocalDateTime.of(checkDate, time).atZone(ZoneId.systemDefault())
                if (zdt.toInstant().toEpochMilli() > System.currentTimeMillis()) {
                    return zdt
                }
            }
            checkDate = checkDate.plusDays(1)
        }
        return LocalDateTime.of(checkDate, time).atZone(ZoneId.systemDefault())
    }

    override fun cancel(tarea: Tarea) {
        val intent = Intent(context, TaskAlarmReceiver::class.java)

        // Cancelar alarmas fijas (exacta y previa)
        val pExact = PendingIntent.getBroadcast(context, (tarea.id * 1000) + OFFSET_EXACT, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        val pEarly = PendingIntent.getBroadcast(context, (tarea.id * 1000) + OFFSET_EARLY, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        pExact?.let { alarmManager.cancel(it) }
        pEarly?.let { alarmManager.cancel(it) }

        // Cancelar alarmas personalizadas (asumimos un máximo razonable para limpiar, o guardamos cuántas había)
        // Por simplicidad, limpiamos las primeras 10 posibles
        for (i in 0..10) {
            val pCustom = PendingIntent.getBroadcast(context, (tarea.id * 1000) + OFFSET_CUSTOM + i, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            pCustom?.let { alarmManager.cancel(it) }
        }
    }
}
