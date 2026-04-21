package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.AgendaViewMode
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.components.AddTareaDialog
import com.saico.ada.dashboard.components.AnnualCalendarView
import com.saico.ada.dashboard.components.MonthlyCalendarGrid
import com.saico.ada.dashboard.components.ViewModeSelector
import com.saico.ada.dashboard.components.WeeklyCalendarStrip
import com.saico.ada.dashboard.state.AgendaState
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import com.saico.ada.ui.R
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.util.toComposeColor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(
    uiState: AgendaState,
    viewModel: DashboardViewModel
) {
    var tareaVerNotas by remember { mutableStateOf<Pair<Tarea, List<Nota>>?>(null) }
    var tareaToEdit by remember { mutableStateOf<Tarea?>(null) }
    var tareaToDelete by remember { mutableStateOf<Tarea?>(null) }
    val successState = uiState as? AgendaState.Success

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        if (successState != null) {
            item {
                ViewModeSelector(
                    currentMode = successState.viewMode,
                    onModeSelected = viewModel::onAgendaViewModeChanged
                )
            }

            item {
                when (successState.viewMode) {
                    AgendaViewMode.SEMANAL -> WeeklyCalendarStrip(
                        selectedDate = successState.selectedDate,
                        tareas = successState.todasLasTareas,
                        onDateSelected = viewModel::onAgendaDateSelected
                    )

                    AgendaViewMode.MENSUAL -> MonthlyCalendarGrid(
                        selectedDate = successState.selectedDate,
                        tareas = successState.todasLasTareas,
                        onDateSelected = viewModel::onAgendaDateSelected
                    )

                    AgendaViewMode.ANUAL -> AnnualCalendarView(
                        selectedDate = successState.selectedDate,
                        tareas = successState.todasLasTareas,
                        onDateSelected = viewModel::onAgendaDateSelected,
                        onSwitchToMonthly = { viewModel.onAgendaViewModeChanged(AgendaViewMode.MENSUAL) }
                    )
                }
            }

            item {
                AgendaDayHeader(
                    date = successState.selectedDate,
                    taskCount = successState.tareasDelDia.size,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            if (successState.tareasDelDia.isEmpty()) {
                item {
                    EmptyDayState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            } else {
                val itemsAgenda = successState.tareasDelDia.sortedBy { it.fechaHoraInicio.toLocalTime() }
                val now = LocalDateTime.now()
                items(
                    itemsAgenda,
                    key = { it.id.toString() + it.fechaHoraInicio.toString() }) { tarea ->
                    val notasVinculadas =
                        successState.notas.filter { it.tareaId == tarea.id || (tarea.plantillaId != null && it.tareaId == tarea.plantillaId) }

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                tareaToDelete = tarea
                                false
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> TerracotaSuave.copy(alpha = 0.8f)
                                    else -> Color.Transparent
                                }, label = "dismiss_color"
                            )
                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1.2f,
                                label = "dismiss_scale"
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Rounded.Delete,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier.scale(scale),
                                    tint = Color.White
                                )
                            }
                        },
                        modifier = Modifier.animateItem()
                    ) {
                        TareaAgendaCard(
                            tarea = tarea,
                            notasCount = notasVinculadas.size,
                            now = now,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            onVerNotas = { tareaVerNotas = tarea to notasVinculadas },
                            onEdit = { tareaToEdit = it },
                            onToggleCompletada = { viewModel.toggleTareaCompletada(it) }
                        )
                    }
                }
            }
        } else if (uiState is AgendaState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VerdeSalvia)
                }
            }
        }
    }

    if (tareaVerNotas != null) {
        NotasVinculadasDialog(
            tarea = tareaVerNotas!!.first,
            notas = tareaVerNotas!!.second,
            onDismiss = { tareaVerNotas = null }
        )
    }

    if (tareaToEdit != null) {
        AddTareaDialog(
            tarea = tareaToEdit,
            isMother = successState?.isMother ?: false,
            customCategorias = successState?.categorias ?: emptyList(),
            onDismiss = { tareaToEdit = null },
            onConfirm = { editedTarea ->
                viewModel.addTarea(editedTarea)
                tareaToEdit = null
            },
            onAddCustomCategory = { nombre, colorHex ->
                viewModel.addCategoriaPersonalizada(nombre, colorHex)
            }
        )
    }

    if (tareaToDelete != null) {
        if (tareaToDelete?.plantillaId != null && tareaToDelete?.plantillaId != 0) {
            AlertDialog(
                onDismissRequest = { tareaToDelete = null },
                containerColor = BaseCrema,
                title = {
                    Text(
                        text = stringResource(R.string.dialog_delete_repeatable_title),
                        color = TextoGrisOscuro,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.dialog_delete_repeatable_message),
                        color = TextoGrisOscuro
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTarea(tareaToDelete!!, true)
                        tareaToDelete = null
                    }) {
                        Text(
                            text = stringResource(R.string.dialog_delete_repeatable_cancel_all),
                            color = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.deleteTarea(tareaToDelete!!, false)
                        tareaToDelete = null
                    }) {
                        Text(
                            text = stringResource(R.string.dialog_delete_repeatable_only_once),
                            color = VerdeSalvia
                        )
                    }
                }
            )
        } else {
            viewModel.deleteTarea(tareaToDelete!!)
            tareaToDelete = null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaDayHeader(date: LocalDate, taskCount: Int, modifier: Modifier = Modifier) {
    val locale = LocalConfiguration.current.locales[0]
    val dayName =
        date.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }
    val dateFormatted = date.format(DateTimeFormatter.ofPattern("d 'de' MMMM", locale))
    val isToday = date == LocalDate.now()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = if (isToday) stringResource(
                    R.string.agenda_today_prefix,
                    dayName
                ) else dayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = dateFormatted.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = TextoGrisOscuro.copy(alpha = 0.5f)
            )
        }
        if (taskCount > 0) {
            Surface(shape = CircleShape, color = TerracotaSuave.copy(alpha = 0.12f)) {
                Text(
                    text = stringResource(R.string.agenda_items_count, taskCount),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TerracotaSuave,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TareaAgendaCard(
    tarea: Tarea,
    notasCount: Int,
    now: LocalDateTime,
    modifier: Modifier = Modifier,
    onVerNotas: () -> Unit,
    onEdit: (Tarea) -> Unit,
    onToggleCompletada: (Tarea) -> Unit
) {
    val color = tarea.colorHex.toComposeColor()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val isPast = tarea.fechaHoraInicio.isBefore(now)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isPast || tarea.estaCompletada) 0.6f else 1f)
            .combinedClickable(
                onClick = { onToggleCompletada(tarea) },
                onLongClick = { onEdit(tarea) }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPast || tarea.estaCompletada) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .clip(CircleShape)
                    .background(if (isPast || tarea.estaCompletada) color.copy(alpha = 0.4f) else color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextoGrisOscuro,
                    textDecoration = if (tarea.estaCompletada || isPast) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextoGrisOscuro.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tarea.fechaHoraInicio.format(timeFormatter)} – ${
                            tarea.fechaHoraFin.format(
                                timeFormatter
                            )
                        }",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoGrisOscuro.copy(alpha = 0.6f),
                        textDecoration = if (tarea.estaCompletada || isPast) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (notasCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = VerdeSalvia.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onVerNotas() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Description,
                                    contentDescription = null,
                                    tint = VerdeSalvia,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$notasCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerdeSalvia,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
