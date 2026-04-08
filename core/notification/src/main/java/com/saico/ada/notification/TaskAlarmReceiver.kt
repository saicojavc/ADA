package com.saico.ada.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.saico.ada.domain.alarm.AlarmScheduler
import com.saico.ada.domain.repository.TareaExcepcionRepository
import com.saico.ada.domain.repository.TareaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class TaskAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tareaExcepcionRepository: TareaExcepcionRepository

    @Inject
    lateinit var tareaRepository: TareaRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("EXTRA_TASK_ID", -1)
        val isRepeating = intent.getBooleanExtra("EXTRA_IS_REPEATING", false)
        val isEarly = intent.getBooleanExtra("EXTRA_IS_EARLY", false)
        
        if (taskId == -1) return

        val pendingResult = goAsync()
        val hoy = LocalDate.now()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isRepeating) {
                    // 1. Verificar si hay una excepción para hoy (completada o saltada)
                    val excepciones = tareaExcepcionRepository.getAllExcepciones().first()
                    val estaCanceladaHoy = excepciones.any { 
                        it.plantillaId == taskId && it.fecha == hoy && (it.estaCompletada || it.estaSaltada) 
                    }

                    if (!estaCanceladaHoy) {
                        processNotification(context, intent)
                    }

                    // 2. Programar la SIGUIENTE ocurrencia si es la alarma exacta
                    // Usamos alarmas de un solo disparo para máxima precisión, 
                    // así que el Receiver debe encadenar la siguiente.
                    if (!isEarly) {
                        val todasLasTareas = tareaRepository.getAllTareas().first()
                        val plantilla = todasLasTareas.find { it.id == taskId }
                        plantilla?.let {
                            alarmScheduler.schedule(it)
                        }
                    }
                } else {
                    processNotification(context, intent)
                }
            } catch (e: Exception) {
                Log.e("ADA_ALARM", "Error procesando alarma: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun processNotification(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("EXTRA_TASK_ID", -1)
        val title = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "Tarea"
        val category = intent.getStringExtra("EXTRA_TASK_CATEGORY") ?: "General"
        val time = intent.getStringExtra("EXTRA_TASK_TIME") ?: ""
        val isEarly = intent.getBooleanExtra("EXTRA_IS_EARLY", false)

        showNotification(context, taskId, title, category, time, isEarly)
    }

    private fun showNotification(
        context: Context,
        taskId: Int,
        title: String,
        category: String,
        time: String,
        isEarly: Boolean
    ) {
        val channelId = "task_critical_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Avisos Críticos ADA"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                setBypassDnd(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
                
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val contentTitle = if (isEarly) "ADA: Tarea en 30 min" else "ADA: Es el momento"
        val formattedTime = if (time.contains(":")) {
            val parts = time.split(":")
            if (parts.size >= 2) "${parts[0]}:${parts[1]}" else time
        } else time

        val contentText = "[$category] $title ($formattedTime)"

        val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 
            if (isEarly) taskId + 10000 else taskId, 
            mainIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(!isEarly)
            .setColor(0xFF81B29A.toInt())
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        notificationManager.notify(if (isEarly) taskId + 10000 else taskId, notificationBuilder.build())
    }
}
