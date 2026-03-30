package com.saico.ada

import android.app.Application
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.saico.ada.data.local.AdaDatabase
import com.saico.ada.data.repository.EventRepositoryImpl
import com.saico.ada.data.worker.WellnessWorker
import com.saico.ada.domain.use_case.AddEventUseCase
import com.saico.ada.domain.use_case.DeleteEventUseCase
import com.saico.ada.domain.use_case.GetEventsUseCase
import java.util.concurrent.TimeUnit

class AdaApp : Application() {

    lateinit var database: AdaDatabase
    lateinit var getEventsUseCase: GetEventsUseCase
    lateinit var addEventUseCase: AddEventUseCase
    lateinit var deleteEventUseCase: DeleteEventUseCase

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AdaDatabase::class.java,
            AdaDatabase.DATABASE_NAME
        ).build()

        val repository = EventRepositoryImpl(database.eventDao)
        getEventsUseCase = GetEventsUseCase(repository)
        addEventUseCase = AddEventUseCase(repository)
        deleteEventUseCase = DeleteEventUseCase(repository)

        setupWellnessWork()
    }

    private fun setupWellnessWork() {
        val wellnessRequest = PeriodicWorkRequestBuilder<WellnessWorker>(
            4, TimeUnit.HOURS // Check every 4 hours
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WellnessWork",
            ExistingPeriodicWorkPolicy.KEEP,
            wellnessRequest
        )
    }
}
