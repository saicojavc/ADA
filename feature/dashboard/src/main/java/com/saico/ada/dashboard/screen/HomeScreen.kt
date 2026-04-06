package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.components.AddTareaDialog
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.model.Tarea
import com.saico.ada.ui.components.AdaSuggestionCard
import com.saico.ada.ui.theme.*
import com.saico.ada.ui.util.toComposeColor
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    uiState: DashboardState,
    viewModel: DashboardViewModel
) {
    var tareaToEdit by remember { mutableStateOf<Tarea?>(null) }
    val successState = uiState as? DashboardState.Success
    
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(1000 * 60)
            currentTime = LocalTime.now()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            HeaderSection(
                nombre = successState?.userName ?: "Jorge",
                saludo = successState?.greeting ?: "Buenos días"
            )
        }

        item {
            // --- INTELIGENCIA APLICADA EN LA CARD ---
            if (successState != null) {
                AdaSuggestionCard(
                    mensaje = successState.adaSuggestion,
                    accion = successState.adaAction,
                    tipo = successState.suggestionType
                )
            } else {
                // Placeholder mientras carga
                AdaSuggestionCard(
                    mensaje = "Analizando tu día...",
                    accion = "ADA está preparando tus sugerencias."
                )
            }
        }

        item {
            Text(
                text = "Tu Día",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextoGrisOscuro,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
        }

        if (successState != null) {
            val tareasHoy = successState.tareasHoy.sortedBy { it.fechaHoraInicio.toLocalTime() }

            items(tareasHoy) { tarea ->
                TimelineItem(
                    tarea = tarea,
                    currentTime = currentTime,
                    onDelete = { viewModel.deleteTarea(tarea) },
                    onEdit = { tareaToEdit = tarea }
                )
            }
        } else {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdeSalvia)
                }
            }
        }
    }

    if (tareaToEdit != null) {
        AddTareaDialog(
            isMother = successState?.isMother ?: false,
            onDismiss = { tareaToEdit = null },
            onConfirm = { editedTarea ->
                viewModel.addTarea(editedTarea)
                tareaToEdit = null
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderSection(nombre: String, saludo: String) {
    val currentDate = remember {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "$saludo, $nombre",
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimelineItem(
    tarea: Tarea,
    currentTime: LocalTime,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val color = tarea.colorHex.toComposeColor()
    val icon = when (tarea.categoria) {
        "Trabajo" -> Icons.Rounded.BusinessCenter
        "Maternidad" -> Icons.Rounded.CheckCircle
        "Bienestar" -> Icons.Rounded.SelfImprovement
        else -> Icons.Rounded.Home
    }

    val isPast = currentTime.isAfter(tarea.fechaHoraFin.toLocalTime()) || currentTime == tarea.fechaHoraFin.toLocalTime()
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(if (isPast) 0.5f else 1f),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(50.dp)) {
            Text(
                text = tarea.fechaHoraInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.labelMedium,
                color = TextoGrisOscuro.copy(alpha = 0.5f),
                textDecoration = if (isPast) TextDecoration.LineThrough else TextDecoration.None
            )
            Box(modifier = Modifier.padding(top = 4.dp).width(2.dp).height(60.dp).background(color.copy(alpha = 0.3f)))
        }

        Card(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoPuro),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isPast) 0.dp else 2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoGrisOscuro,
                        textDecoration = if (isPast) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = tarea.categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        textDecoration = if (isPast) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "Opciones", tint = TextoGrisOscuro.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(BlancoPuro)) {
                        DropdownMenuItem(
                            text = { Text("Editar", color = TextoGrisOscuro) },
                            leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = VerdeSalvia) },
                            onClick = { showMenu = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Rounded.Delete, null, tint = Color.Red.copy(alpha = 0.7f)) },
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}
