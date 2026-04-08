package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.datastore.SleepPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetInactivitySleepUseCase @Inject constructor(
    private val sleepPrefs: SleepPrefs
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): Flow<Float> {
        return combine(
            sleepPrefs.lastScreenOff,
            sleepPrefs.lastUserPresent
        ) { screenOff, userPresent ->
            if (screenOff == 0L || userPresent == 0L || userPresent <= screenOff) {
                0f
            } else {
                val diffMs = userPresent - screenOff
                val hours = diffMs.toFloat() / (1000f * 60f * 60f)
                
                // Limitar a un rango razonable (ej: máximo 12 horas para evitar errores)
                if (hours > 12f) 0f else hours
            }
        }
    }
}
