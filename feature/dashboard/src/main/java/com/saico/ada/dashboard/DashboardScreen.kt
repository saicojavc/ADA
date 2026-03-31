package com.saico.ada.dashboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.components.AdaGravityBackground
import com.saico.ada.dashboard.components.NavigationBar
import com.saico.ada.dashboard.model.BottomAppBarItems
import com.saico.ada.dashboard.screen.AgendaScreen
import com.saico.ada.dashboard.screen.HomeScreen
import com.saico.ada.dashboard.screen.NotesScreen
import com.saico.ada.dashboard.screen.WellnessScreen
import com.saico.ada.ui.components.AdaSpeedDialFab

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen() {
    Context()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Context() {
    var selectedBottomAppBarItem by remember { mutableStateOf(BottomAppBarItems.HOME) }
    var isFabExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {},
            bottomBar = {
                NavigationBar(
                    selectedBottomAppBarItem = selectedBottomAppBarItem,
                    onItemSelected = { selectedBottomAppBarItem = it },
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AdaGravityBackground(modifier = Modifier.fillMaxSize())
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedBottomAppBarItem) {
                        BottomAppBarItems.HOME -> HomeScreen()
                        BottomAppBarItems.AGENDA -> AgendaScreen()
                        BottomAppBarItems.WELLNES -> WellnessScreen()
                        BottomAppBarItems.NOTES -> NotesScreen()
                    }
                }
            }
        }

        // 1. Fondo oscurecido (Capa intermedia)
        if (isFabExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isFabExpanded = false
                    }
            )
        }

        // 2. FAB Manual (Capa superior)
        // Lo posicionamos manualmente para que coincida con la posición estándar del FAB
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, end = 16.dp), // Ajustado según la altura de tu NavigationBar + margen
            contentAlignment = Alignment.BottomEnd
        ) {
            AdaSpeedDialFab(
                isExpanded = isFabExpanded,
                onExpandedChange = { isFabExpanded = it },
                onActionSelected = { action ->
                    Log.d("ADA_FAB", "Acción seleccionada: ${action.name}")
                    isFabExpanded = false
                }
            )
        }
    }
}
