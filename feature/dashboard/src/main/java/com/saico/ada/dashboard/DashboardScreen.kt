package com.saico.ada.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.ada.dashboard.components.*
import com.saico.ada.dashboard.components.NavigationBar
import com.saico.ada.dashboard.model.BottomAppBarItems
import com.saico.ada.dashboard.screen.AgendaScreen
import com.saico.ada.dashboard.screen.HomeScreen
import com.saico.ada.dashboard.screen.NotesScreen
import com.saico.ada.dashboard.screen.WellnessScreen
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.ui.components.AdaSpeedDialFab
import com.saico.ada.ui.components.FabAction
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val agendaSelectedDate by viewModel.selectedAgendaDate.collectAsStateWithLifecycle()
    val agendaViewMode by viewModel.agendaViewMode.collectAsStateWithLifecycle()
    Context(uiState, viewModel, agendaSelectedDate, agendaViewMode)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Context(
    uiState: DashboardState,
    viewModel: DashboardViewModel,
    agendaSelectedDate: LocalDate,
    agendaViewMode: AgendaViewMode
) {
    var selectedBottomAppBarItem by remember { mutableStateOf(BottomAppBarItems.HOME) }
    var isFabExpanded by remember { mutableStateOf(false) }
    
    var showAddTareaDialog by remember { mutableStateOf(false) }
    var showAddBienestarDialog by remember { mutableStateOf(false) }
    var showAddNotaDialog by remember { mutableStateOf(false) }

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
            Box(modifier = Modifier.fillMaxSize()) {
                AdaGravityBackground(modifier = Modifier.fillMaxSize())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedBottomAppBarItem) {
                        BottomAppBarItems.HOME -> HomeScreen(uiState, viewModel)
                        BottomAppBarItems.AGENDA -> AgendaScreen(
                            todasLasTareas = (uiState as? DashboardState.Success)?.todasLasTareas ?: emptyList(),
                            selectedDate = agendaSelectedDate,
                            agendaViewMode = agendaViewMode,
                            onDateSelected = viewModel::onAgendaDateSelected,
                            onViewModeChanged = viewModel::onAgendaViewModeChanged
                        )
                        BottomAppBarItems.WELLNES -> WellnessScreen(uiState, viewModel)
                        BottomAppBarItems.NOTES -> NotesScreen(uiState, viewModel)
                    }
                }
            }
        }

        if (isFabExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isFabExpanded = false }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AdaSpeedDialFab(
                isExpanded = isFabExpanded,
                onExpandedChange = { isFabExpanded = it },
                onActionSelected = { action ->
                    when (action) {
                        FabAction.Nota -> showAddNotaDialog = true
                        FabAction.Tarea -> showAddTareaDialog = true
                        FabAction.Cuidado -> showAddBienestarDialog = true
                    }
                    isFabExpanded = false
                }
            )
        }

        if (showAddTareaDialog) {
            AddTareaDialog(
                onDismiss = { showAddTareaDialog = false },
                onConfirm = { tarea ->
                    viewModel.addTarea(tarea)
                    showAddTareaDialog = false
                }
            )
        }

        if (showAddBienestarDialog) {
            AddBienestarDialog(
                onDismiss = { showAddBienestarDialog = false },
                onConfirm = { tipo, valor ->
                    val meta = when(tipo) {
                        "Hidratación" -> 2000f
                        "Pasos" -> 10000f
                        "Sueño" -> 8f
                        else -> 1f
                    }
                    viewModel.addBienestar(tipo, valor, meta, if(tipo=="Sueño") "h" else "u", "star")
                    showAddBienestarDialog = false
                }
            )
        }

        if (showAddNotaDialog) {
            AddNotaDialog(
                onDismiss = { showAddNotaDialog = false },
                onConfirm = { titulo, contenido ->
                    viewModel.addNote(titulo, contenido, "#F2CC8F")
                    showAddNotaDialog = false
                }
            )
        }
    }
}
