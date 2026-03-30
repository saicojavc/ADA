package com.saico.ada

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.saico.ada.ui.navigation.routes.dashboard.DashboardRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel(){

    var firstScreen by mutableStateOf(DashboardRoute.RootRoute.route)
}
