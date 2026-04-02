package com.saico.ada.domain.use_case

import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.model.Bienestar
import javax.inject.Inject

class AddBienestarUseCase @Inject constructor(
    private val repository: BienestarRepository
) {
    suspend operator fun invoke(bienestar: Bienestar) {
        repository.insertRegistro(bienestar)
    }
}
