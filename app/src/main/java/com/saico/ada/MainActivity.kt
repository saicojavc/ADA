package com.saico.ada

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.saico.ada.dashboard.navigation.dashboardGraph
import com.saico.ada.ui.navigation.Navigator
import com.saico.ada.ui.theme.ADATheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    private val viewModel: MainActivityViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Aquí podrías manejar si el usuario deniega el permiso
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones para Android 13+
        checkNotificationPermission()

        setContent {
            ADATheme {
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    MainContainer(
                        startDestination = viewModel.firstScreen,
                        navigator = navigator,
                        navController = navController
                    )
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
private fun MainContainer(
    startDestination: String,
    navigator: Navigator,
    navController: NavHostController,
) {
    Column {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            dashboardGraph(navController = navController)
        }
    }
}
