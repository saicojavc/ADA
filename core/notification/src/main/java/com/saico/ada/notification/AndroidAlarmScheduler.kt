package com.saico.ada.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.model.Tarea
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject

class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(tarea: Tarea) {
        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra("EXTRA_TASK_ID", tarea.id)
            putExtra("EXTRA_TASK_TITLE", tarea.titulo)
            putExtra("EXTRA_TASK_CATEGORY", tarea.categoria)
            putExtra("EXTRA_TASK_TIME", tarea.fechaHoraInicio.toString())
        }

        try {
            // 1. Alarma a la hora exacta
            val timeExact =
                tarea.fechaHoraInicio.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            scheduleAlarm(timeExact, tarea.id * 2, intent)

            // 2. Alarma 30 minutos antes
            val timeEarly =
                tarea.fechaHoraInicio.minusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli()
            if (timeEarly > System.currentTimeMillis()) {
                val earlyIntent = Intent(intent).apply {
                    action = "ACTION_TASK_EARLY_${tarea.id}"
                    putExtra("EXTRA_IS_EARLY", true)
                }
                scheduleAlarm(timeEarly, (tarea.id * 2) + 1, earlyIntent)
            }

            Log.d(
                "ADA_ALARM",
                "Alarmas programadas para la tarea: ${tarea.titulo} a las $timeExact"
            )
        } catch (e: Exception) {
            Log.e("ADA_ALARM", "Error al programar: ${e.message}")
        }
    }

    private fun scheduleAlarm(time: Long, requestCode: Int, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }

    override fun cancel(tarea: Tarea) {
        val intent = Intent(context, TaskAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id * 2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val earlyPendingIntent = PendingIntent.getBroadcast(
            context,
            (tarea.id * 2) + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        alarmManager.cancel(earlyPendingIntent)
    }
}
