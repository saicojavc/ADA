package com.saico.ada.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import com.saico.ada.dashboard.components.AdaGravityBackground
import com.saico.ada.dashboard.components.NavigationBar
import com.saico.ada.dashboard.model.BottomAppBarItems
import com.saico.ada.dashboard.screen.AgendaScreen
import com.saico.ada.dashboard.screen.HomeScreen
import com.saico.ada.dashboard.screen.NotesScreen
import com.saico.ada.dashboard.screen.WellnesScreen
import com.saico.ada.ui.theme.BaseCrema

@Composable
fun DashboardScreen() {

    Context()

}

@Composable
fun Context() {

    var selectedBottomAppBarItem by remember { mutableStateOf(BottomAppBarItems.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        },
        bottomBar = {
            NavigationBar(
                selectedBottomAppBarItem = selectedBottomAppBarItem,
                onItemSelected = { selectedBottomAppBarItem = it },
            )
        }

    ) {paddingValues ->
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
                    BottomAppBarItems.HOME -> {
                        HomeScreen()
                    }

                    BottomAppBarItems.AGENDA -> {
                        AgendaScreen()
                    }

                    BottomAppBarItems.WELLNES -> {
                        WellnesScreen()
                    }
                    BottomAppBarItems.NOTES -> {
                        NotesScreen()
                    }
                }
            }
        }
    }

}