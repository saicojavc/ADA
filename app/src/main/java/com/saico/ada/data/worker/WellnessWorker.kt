package com.saico.ada.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.saico.ada.R
import com.saico.ada.data.local.AdaDatabase
import com.saico.ada.data.repository.EventRepositoryImpl
import com.saico.ada.domain.use_case.GapFinderUseCase
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class WellnessWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = Room.databaseBuilder(
            applicationContext,
            AdaDatabase::class.java,
            AdaDatabase.DATABASE_NAME
        ).build()

        val repository = EventRepositoryImpl(database.eventDao)
        val gapFinder = GapFinderUseCase()

        val today = LocalDate.now()
        val events = repository.getEventsForDay(today).first()
        val gaps = gapFinder(events, today)

        if (gaps.isNotEmpty()) {
            val suggestions = listOf(
                "ADA suggests a quick stretch for you!",
                "Perfect time for a glass of water!",
                "You have a free moment. How about a 2-minute meditation?",
                "Time to rest your eyes and look at something green.",
                "ADA thinks you deserve a small tea break."
            )
            showNotification("Time for a break?", suggestions.random())
        } else if (events.size > 5) {
            showNotification("Busy day!", "ADA reminds you to take deep breaths between meetings.")
        }

        database.close()
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "wellness_notifications"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Wellness Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
