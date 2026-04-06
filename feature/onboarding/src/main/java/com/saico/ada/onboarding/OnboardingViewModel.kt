package com.saico.ada.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.datastore.UserPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPrefs: UserPrefs
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _isMother = MutableStateFlow(false)
    val isMother = _isMother.asStateFlow()

    private val _occupation = MutableStateFlow("")
    val occupation = _occupation.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted = _onboardingCompleted.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onMotherChange(isMother: Boolean) {
        _isMother.value = isMother
    }

    fun onOccupationChange(newOccupation: String) {
        _occupation.value = newOccupation
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPrefs.saveUserData(
                name = _name.value,
                isMother = _isMother.value,
                occupation = _occupation.value
            )
            _onboardingCompleted.value = true
        }
    }
}
