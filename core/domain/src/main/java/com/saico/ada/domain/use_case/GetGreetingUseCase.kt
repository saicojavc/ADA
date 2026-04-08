package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import javax.inject.Inject

enum class GreetingTime { MORNING, AFTERNOON, EVENING }

class GetGreetingUseCase @Inject constructor() {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): GreetingTime {
        val now = LocalTime.now()
        return when (now.hour) {
            in 5..11 -> GreetingTime.MORNING
            in 12..18 -> GreetingTime.AFTERNOON
            else -> GreetingTime.EVENING
        }
    }
}
