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
import androidx.core.app.NotificationCompat

class TaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ADA_ALARM", "Alarma recibida en el Receiver")
        
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
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                setBypassDnd(true) // Ignorar No Molestar
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
        val formattedTime = if (time.contains("T")) time.split("T")[1].substring(0, 5) else time
        val contentText = "[$category] $title ($formattedTime)"

        val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 
            taskId, 
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
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // IMPORTANTE: Pantalla completa para Android 10+
        notificationBuilder.setFullScreenIntent(pendingIntent, true)

        notificationManager.notify(taskId, notificationBuilder.build())
    }
}
