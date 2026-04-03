package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreVert
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.saico.ada.ui.theme.BlancoPuro
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.util.toComposeColor
import java.time.LocalDate
import java.time.LocalDateTime
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            HeaderSection(nombre = "Jorge")
        }

        item {
            AdaSuggestionCard(
                mensaje = "Tienes un hueco de 25 min a las 11:00 AM.",
                accion = "¿Es buen momento para tu rutina de Aloe?"
            )
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

        if (uiState is DashboardState.Success) {
            items(uiState.tareas) { tarea ->
                TimelineItem(
                    tarea = tarea,
                    onDelete = { viewModel.deleteTarea(tarea) },
                    onEdit = { tareaToEdit = tarea }
                )
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
fun HeaderSection(nombre: String) {
    val currentDate = remember {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Buenos días, $nombre",
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
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val color = tarea.colorHex.toComposeColor()
    val icon = when (tarea.categoria) {
        "Trabajo" -> Icons.Rounded.BusinessCenter
        "Maternidad" -> Icons.Rounded.CheckCircle
        else -> Icons.Rounded.Home
    }

    // Lógica para determinar si la tarea ya pasó
    val now = LocalTime.now()
    val isPast = tarea.fechaHoraFin.toLocalTime().isBefore(now)

    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(if (isPast) 0.5f else 1f), // Reducimos opacidad si ya pasó
        verticalAlignment = Alignment.Top
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
                .padding(start = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoPuro),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isPast) 0.dp else 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
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
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Opciones",
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
                            text = { Text("Editar", color = TextoGrisOscuro) },
                            leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = VerdeSalvia) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red.copy(alpha = 0.7f)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Delete,
                                    null,
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
