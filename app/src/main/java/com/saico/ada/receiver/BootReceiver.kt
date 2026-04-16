package com.saico.ada.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.domain.use_case.ScheduleRecurringAlarmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tareaRepository: TareaRepository

    @Inject
    lateinit var scheduleRecurringAlarmsUseCase: ScheduleRecurringAlarmsUseCase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val allTareas = tareaRepository.getAllTareas().first()
                val plantillas = allTareas.filter { 
                    it.esPlantilla && (it.fechaFinRepeticion == null || it.fechaFinRepeticion!!.isAfter(LocalDate.now())) 
                }
                
                plantillas.forEach { plantilla ->
                    scheduleRecurringAlarmsUseCase(plantilla)
                }
            }
        }
    }
}
