package com.saico.ada.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.saico.ada.model.Bienestar
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class AddRitualUseCase @Inject constructor(
    private val addBienestarUseCase: AddBienestarUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(nombre: String, hora: LocalTime?) {
        addBienestarUseCase(
            Bienestar(
                tipo = nombre,
                valorActual = 0f,
                metaObjetivo = 1f,
                unidad = "u",
                iconoNombre = "star",
                fecha = LocalDateTime.now(),
                horaProgramada = hora
            )
        )
    }
}
