package com.saico.ada.service

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.datastore.SleepPrefs
import com.saico.ada.model.Bienestar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var repository: BienestarRepository

    @Inject
    lateinit var sleepPrefs: SleepPrefs

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var initialSteps = -1f
    private var lastRecordedDay: LocalDate? = null

    private val screenReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    serviceScope.launch {
                        sleepPrefs.saveScreenOff(System.currentTimeMillis())
                    }
                }
                Intent.ACTION_USER_PRESENT -> {
                    serviceScope.launch {
                        val screenOffTime = sleepPrefs.lastScreenOff.first()
                        val now = System.currentTimeMillis()
                        
                        if (screenOffTime > 0) {
                            val diffMs = now - screenOffTime
                            val diffHours = diffMs / (1000f * 60 * 60)
                            
                            val currentHour = LocalDateTime.now().hour
                            if (diffHours >= 3f && currentHour in 5..12) {
                                val hoy = LocalDateTime.now().toLocalDate()
                                val registros = repository.getAllRegistros().first()
                                val registroHoy = registros.find { r -> 
                                    r.tipo == "Sueño" && r.fecha.toLocalDate() == hoy 
                                }
                                
                                if (registroHoy != null) {
                                    if (diffHours > registroHoy.valorActual) {
                                        repository.insertRegistro(registroHoy.copy(valorActual = diffHours))
                                    }
                                } else {
                                    repository.insertRegistro(
                                        Bienestar(
                                            tipo = "Sueño",
                                            valorActual = diffHours,
                                            metaObjetivo = 8f,
                                            unidad = "h",
                                            fecha = LocalDateTime.now(),
                                            iconoNombre = "nights_stay"
                                        )
                                    )
                                }
                            }
                        }
                        sleepPrefs.saveUserPresent(now)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, filter)
        
        startForeground(1, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val totalStepsSinceBoot = it.values[0]
            val today = LocalDate.now()
            
            // 1. Detección de cambio de día para resetear initialSteps
            if (lastRecordedDay != null && lastRecordedDay != today) {
                initialSteps = totalStepsSinceBoot
            }
            lastRecordedDay = today

            serviceScope.launch {
                val registros = repository.getAllRegistros().first()
                val registroHoy = registros.find { r -> 
                    r.tipo == "Pasos" && r.fecha.toLocalDate() == today 
                }

                // 2. Inicialización o recuperación de la marca base
                if (initialSteps == -1f) {
                    initialSteps = totalStepsSinceBoot - (registroHoy?.valorActual ?: 0f)
                }

                val stepsToday = totalStepsSinceBoot - initialSteps

                if (registroHoy != null) {
                    // Actualizar solo si el contador aumentó
                    if (stepsToday > registroHoy.valorActual) {
                        repository.insertRegistro(registroHoy.copy(valorActual = stepsToday))
                    }
                } else {
                    // Nuevo día detectado por primera vez en la BD
                    repository.insertRegistro(
                        Bienestar(
                            tipo = "Pasos",
                            valorActual = if (stepsToday < 0) 0f else stepsToday,
                            metaObjetivo = 10000f,
                            unidad = "pasos",
                            fecha = LocalDateTime.now(),
                            iconoNombre = "directions_run"
                        )
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "step_counter_channel")
            .setContentTitle("ADA Bienestar")
            .setContentText("Cuidando de tu movimiento y descanso.")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
        unregisterReceiver(screenReceiver)
    }
}
