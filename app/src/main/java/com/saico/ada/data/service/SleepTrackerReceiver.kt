package com.saico.ada.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.datastore.SleepPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class SleepTrackerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sleepPrefs: SleepPrefs

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val now = System.currentTimeMillis()
        val hour = LocalDateTime.now().hour

        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                // Registrar solo si es tarde en la noche (post 10 PM)
                if (hour >= 22 || hour < 4) {
                    CoroutineScope(Dispatchers.IO).launch {
                        sleepPrefs.saveScreenOff(now)
                    }
                }
            }
            Intent.ACTION_USER_PRESENT -> {
                // Registrar el primer desbloqueo de la mañana
                if (hour in 5..11) {
                    CoroutineScope(Dispatchers.IO).launch {
                        sleepPrefs.saveUserPresent(now)
                    }
                }
            }
        }
    }
}
