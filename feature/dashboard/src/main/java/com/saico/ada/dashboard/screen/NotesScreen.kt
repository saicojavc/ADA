package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.model.Nota
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.util.toComposeColor
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesScreen(
    uiState: DashboardState,
    viewModel: DashboardViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Mis Pensamientos",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro,
            modifier = Modifier.padding(24.dp)
        )

        if (uiState is DashboardState.Success) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(uiState.notas) { nota ->
                    NoteCard(nota)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VerdeSalvia)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteCard(nota: Nota) {
    val color = nota.colorEtiquetaHex.toComposeColor()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = nota.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextoGrisOscuro
            )
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
