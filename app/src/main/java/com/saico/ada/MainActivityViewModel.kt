package com.saico.ada

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.ada.datastore.UserPrefs
import com.saico.ada.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.ada.ui.navigation.routes.onboarding.OnboardingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userPrefs: UserPrefs
) : ViewModel() {

    var firstScreen by mutableStateOf<String?>(null)
        private set

    init {
        checkStartDestination()
    }

    private fun checkStartDestination() {
        viewModelScope.launch {
            val completed = userPrefs.isOnboardingCompleted.first()
            firstScreen = if (completed) {
                DashboardRoute.RootRoute.route
            } else {
                OnboardingRoute.RootRoute.route
            }
        }
    }
}
