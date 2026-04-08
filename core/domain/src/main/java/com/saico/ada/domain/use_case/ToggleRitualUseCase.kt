package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Bienestar
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class ToggleRitualUseCase @Inject constructor(
    private val addBienestarUseCase: AddBienestarUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(ritual: Bienestar) {
        val hoy = LocalDate.now()
        val nuevoValor = if (ritual.valorActual >= ritual.metaObjetivo) 0f else ritual.metaObjetivo
        addBienestarUseCase(
            ritual.copy(
                valorActual = nuevoValor,
                fecha = hoy.atTime(ritual.horaProgramada ?: LocalTime.now())
            )
        )
    }
}
