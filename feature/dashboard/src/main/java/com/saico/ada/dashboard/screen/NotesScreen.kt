package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.components.AddNotaDialog
import com.saico.ada.dashboard.state.NotesState
import com.saico.ada.model.Nota
import com.saico.ada.ui.R
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.util.toComposeColor
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesScreen(
    uiState: NotesState,
    viewModel: DashboardViewModel
) {
    var notaToEdit by remember { mutableStateOf<Nota?>(null) }
    val successState = uiState as? NotesState.Success

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.notes_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro,
            modifier = Modifier.padding(24.dp)
        )

        if (successState != null) {
            if (successState.notas.isEmpty()) {
                EmptyNotesState()
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 120.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(successState.notas) { nota ->
                        NoteCard(
                            nota = nota,
                            onClick = { notaToEdit = nota }
                        )
                    }
                }
            }
        } else if (uiState is NotesState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VerdeSalvia)
            }
        }
    }

    if (notaToEdit != null) {
        AddNotaDialog(
            nota = notaToEdit,
            tareas = successState?.todasLasTareas ?: emptyList(),
            onDismiss = { notaToEdit = null },
            onConfirm = { titulo, contenido, tareaId ->
                viewModel.updateNote(
                    notaToEdit!!.copy(
                        titulo = titulo,
                        contenido = contenido,
                        tareaId = tareaId
                    )
                )
                notaToEdit = null
            },
            onDelete = {
                viewModel.deleteNote(notaToEdit!!)
                notaToEdit = null
            }
        )
    }
}

@Composable
fun EmptyNotesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.EditNote,
            contentDescription = null,
            tint = VerdeSalvia.copy(alpha = 0.3f),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.notes_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextoGrisOscuro.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.notes_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoGrisOscuro.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteCard(
    nota: Nota,
    onClick: () -> Unit
) {
    val color = nota.colorEtiquetaHex.toComposeColor()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = nota.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextoGrisOscuro,
                    modifier = Modifier.weight(1f)
                )
                if (nota.tareaId != null) {
                    Icon(
                        imageVector = Icons.Rounded.Assignment,
                        contentDescription = "Vinculada a tarea",
                        tint = TextoGrisOscuro.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nota.contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoGrisOscuro.copy(alpha = 0.8f),
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = nota.fechaCreacion.format(DateTimeFormatter.ofPattern("dd MMM")),
                style = MaterialTheme.typography.labelSmall,
                color = TextoGrisOscuro.copy(alpha = 0.4f)
            )
        }
    }
}
