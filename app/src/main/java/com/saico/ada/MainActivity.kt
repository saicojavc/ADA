package com.saico.ada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saico.ada.ui.event.EventViewModel
import com.saico.ada.ui.event.EventViewModelFactory
import com.saico.ada.ui.event.screen.AgendaScreen
import com.saico.ada.ui.theme.ADATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as AdaApp
        val viewModelFactory = EventViewModelFactory(
            app.getEventsUseCase,
            app.addEventUseCase,
            app.deleteEventUseCase
        )

        setContent {
            ADATheme {
                val navController = rememberNavController()
                val viewModel: EventViewModel = viewModel(factory = viewModelFactory)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "agenda",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("agenda") {
                            AgendaScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
