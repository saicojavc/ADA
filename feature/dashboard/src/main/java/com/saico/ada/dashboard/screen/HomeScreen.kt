package com.saico.ada.dashboard.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.components.AdaSuggestionCard
import com.saico.ada.dashboard.components.AddTareaDialog
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.model.Nota
import com.saico.ada.model.Tarea
import com.saico.ada.ui.R
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.BlancoPuro
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.util.toComposeColor
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    uiState: DashboardState,
    viewModel: DashboardViewModel
) {
    var tareaToEdit by remember { mutableStateOf<Tarea?>(null) }
    var tareaToDelete by remember { mutableStateOf<Tarea?>(null) }
    var tareaVerNotas by remember { mutableStateOf<Pair<Tarea, List<Nota>>?>(null) }
    val successState = uiState as? DashboardState.Success

    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000 * 60)
            currentDateTime = LocalDateTime.now()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            HeaderSection(
                saludoRes = successState?.greetingRes ?: R.string.home_greeting_morning,
                userName = successState?.userName ?: ""
            )
        }

        item {
            if (successState != null) {
                AdaSuggestionCard(
                    mensaje = stringResource(
                        successState.adaSuggestionRes,
                        *successState.adaSuggestionArgs.toTypedArray()
                    ),
                    accion = stringResource(
                        successState.adaActionRes,
                        *successState.adaActionArgs.toTypedArray()
                    ),
                    tipo = successState.suggestionType
                )
            } else {
                AdaSuggestionCard(
                    mensaje = stringResource(R.string.home_analyzing),
                    accion = stringResource(R.string.home_preparing_suggestions)
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.home_your_day),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextoGrisOscuro,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
        }

        if (successState != null) {
            if (successState.tareasHoy.isEmpty()) {
                item { EmptyDayState() }
            } else {
                val tareasHoy = successState.tareasHoy.sortedBy { it.fechaHoraInicio }
                items(tareasHoy) { tarea ->
                    val notasVinculadas = successState.notas.filter { it.tareaId == tarea.id }
                    TimelineItem(
                        tarea = tarea,
                        notasCount = notasVinculadas.size,
                        currentDateTime = currentDateTime,
                        onDelete = { tareaToDelete = tarea },
                        onEdit = { tareaToEdit = tarea },
                        onToggleCompletada = { viewModel.toggleTareaCompletada(it) },
                        onVerNotas = { tareaVerNotas = tarea to notasVinculadas }
                    )
                }
            }
        } else {
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

    if (tareaToEdit != null) {
        AddTareaDialog(
            tarea = tareaToEdit,
            isMother = successState?.isMother ?: false,
            onDismiss = { tareaToEdit = null },
            onConfirm = { editedTarea ->
                viewModel.addTarea(editedTarea)
                tareaToEdit = null
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

    if (tareaVerNotas != null) {
        NotasVinculadasDialog(
            tarea = tareaVerNotas!!.first,
            notas = tareaVerNotas!!.second,
            onDismiss = { tareaVerNotas = null }
        )
    }
}

@Composable
fun NotasVinculadasDialog(
    tarea: Tarea,
    notas: List<Nota>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column {
                Text(
                    text = stringResource(id = R.string.task_notes_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoGrisOscuro,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = VerdeSalvia
                )
            }
        },
        text = {
            if (notas.isEmpty()) {
                Text(
                    text = stringResource(R.string.task_notes_empty),
                    color = TextoGrisOscuro.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notas) { nota ->
                        val color = nota.colorEtiquetaHex.toComposeColor()
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = color.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                color.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = nota.titulo,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGrisOscuro
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = nota.contenido,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoGrisOscuro.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_close),
                    color = VerdeSalvia,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@SuppressLint("LocalContextConfigurationRead")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderSection(saludoRes: Int, userName: String) {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val currentDate = remember(locale) {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
    }
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = stringResource(saludoRes, userName),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro
        )
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoGrisOscuro.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun EmptyDayState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.WbSunny,
            contentDescription = null,
            tint = VerdeSalvia.copy(alpha = 0.2f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextoGrisOscuro.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.home_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoGrisOscuro.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimelineItem(
    tarea: Tarea,
    notasCount: Int,
    currentDateTime: LocalDateTime,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleCompletada: (Tarea) -> Unit,
    onVerNotas: () -> Unit
) {
    val color = tarea.colorHex.toComposeColor()
    val icon = when (tarea.categoria) {
        stringResource(R.string.cat_work) -> Icons.Rounded.BusinessCenter
        stringResource(R.string.cat_maternity) -> Icons.Rounded.CheckCircle
        stringResource(R.string.cat_wellbeing) -> Icons.Rounded.SelfImprovement
        else -> Icons.Rounded.Home
    }

    val isPast = currentDateTime.isAfter(tarea.fechaHoraFin)
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(if (isPast) 0.5f else 1f), verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = tarea.fechaHoraInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.labelMedium,
                color = TextoGrisOscuro.copy(alpha = 0.5f),
                textDecoration = if (isPast) TextDecoration.LineThrough else TextDecoration.None
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(2.dp)
                    .height(60.dp)
                    .background(color.copy(alpha = 0.3f))
            )
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .clickable { onToggleCompletada(tarea) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoPuro),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isPast) 0.dp else 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (tarea.estaCompletada) color else color.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (tarea.estaCompletada) Icons.Rounded.Check else icon,
                        contentDescription = null,
                        tint = if (tarea.estaCompletada) Color.White else color,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoGrisOscuro,
                        textDecoration = if (tarea.estaCompletada || isPast) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tarea.categoria,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
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
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = stringResource(R.string.action_options),
                            tint = TextoGrisOscuro.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(BlancoPuro)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.action_edit),
                                    color = TextoGrisOscuro
                                )
                            },
                            leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = VerdeSalvia) },
                            onClick = { showMenu = false; onEdit() })
                        if (notasCount > 0) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Ver notas ($notasCount)",
                                        color = TextoGrisOscuro
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Description,
                                        null,
                                        tint = VerdeSalvia
                                    )
                                },
                                onClick = { showMenu = false; onVerNotas() })
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.action_delete),
                                    color = Color.Red.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Delete,
                                    null,
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            },
                            onClick = { showMenu = false; onDelete() })
                    }
                }
            }
        }
    }
}
