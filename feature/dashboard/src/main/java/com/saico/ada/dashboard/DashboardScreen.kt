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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.ada.dashboard.components.AddNotaDialog
import com.saico.ada.dashboard.components.AddTareaDialog
import com.saico.ada.dashboard.components.NavigationBar
import com.saico.ada.dashboard.model.BottomAppBarItems
import com.saico.ada.dashboard.screen.AgendaScreen
import com.saico.ada.dashboard.screen.HomeScreen
import com.saico.ada.dashboard.screen.NotesScreen
import com.saico.ada.dashboard.screen.WellnessScreen
import com.saico.ada.dashboard.state.AgendaState
import com.saico.ada.dashboard.state.HomeState
import com.saico.ada.dashboard.state.NotesState
import com.saico.ada.dashboard.state.WellnessState
import com.saico.ada.ui.components.AdaGravityBackground
import com.saico.ada.ui.components.AdaSpeedDialFab
import com.saico.ada.ui.components.FabAction

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val agendaState by viewModel.agendaState.collectAsStateWithLifecycle()
    val wellnessState by viewModel.wellnessState.collectAsStateWithLifecycle()
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()

    Context(
        homeState = homeState,
        agendaState = agendaState,
        wellnessState = wellnessState,
        notesState = notesState,
        viewModel = viewModel
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Context(
    homeState: HomeState,
    agendaState: AgendaState,
    wellnessState: WellnessState,
    notesState: NotesState,
    viewModel: DashboardViewModel
) {
    var selectedBottomAppBarItem by remember { mutableStateOf(BottomAppBarItems.HOME) }
    var isFabExpanded by remember { mutableStateOf(false) }

    var showAddTareaDialog by remember { mutableStateOf(false) }
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
                        BottomAppBarItems.HOME -> HomeScreen(homeState, viewModel)
                        BottomAppBarItems.AGENDA -> AgendaScreen(
                            uiState = agendaState,
                            viewModel = viewModel
                        )

                        BottomAppBarItems.WELLNES -> WellnessScreen(wellnessState, viewModel)
                        BottomAppBarItems.NOTES -> NotesScreen(notesState, viewModel)
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
                .padding(bottom = 120.dp, end = 20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AdaSpeedDialFab(
                isExpanded = isFabExpanded,
                onExpandedChange = { isFabExpanded = it },
                onActionSelected = { action ->
                    when (action) {
                        FabAction.Nota -> showAddNotaDialog = true
                        FabAction.Tarea -> showAddTareaDialog = true
                    }
                    isFabExpanded = false
                }
            )
        }

        if (showAddTareaDialog) {
            val successState = homeState as? HomeState.Success
            AddTareaDialog(
                isMother = successState?.isMother ?: false,
                customCategorias = successState?.categorias ?: emptyList(),
                onDismiss = { showAddTareaDialog = false },
                onConfirm = { tarea ->
                    viewModel.addTarea(tarea)
                    showAddTareaDialog = false
                },
                onAddCustomCategory = { nombre, colorHex ->
                    viewModel.addCategoriaPersonalizada(nombre, colorHex)
                }
            )
        }

        if (showAddNotaDialog) {
            val successState = notesState as? NotesState.Success
            AddNotaDialog(
                tareas = successState?.todasLasTareas ?: emptyList(),
                onDismiss = { showAddNotaDialog = false },
                onConfirm = { titulo, contenido, tareaId ->
                    viewModel.addNote(titulo, contenido, "#F2CC8F", tareaId)
                    showAddNotaDialog = false
                }
            )
        }
    }
}
