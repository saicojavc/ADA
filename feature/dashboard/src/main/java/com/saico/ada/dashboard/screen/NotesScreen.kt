package com.saico.ada.dashboard.screen


import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
// 1. Foundation para la rejilla escalonada (Staggered Grid)
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape

// 2. Material 3 para los componentes de texto y tarjetas
import androidx.compose.material3.*

// 3. Utilidades de UI y Composable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia

// 4. Tus Colores Personalizados (Asegúrate de que estén definidos)
// BaseCrema, TextoGrisOscuro, TerracotaSuave, AmbarNeutro, VerdeSalvia

@Composable
fun NotesScreen() {
    val notas = getMockNotes()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Cabecera con estilo de diario
        Text(
            text = "Mis Pensamientos",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro,
            modifier = Modifier.padding(24.dp)
        )

        // Rejilla de Notas (Estilo Post-it)
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            items(notas) { nota ->
                NoteCard(nota)
            }
        }
    }
}
@Composable
fun NoteCard(nota: NoteItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = nota.color.copy(alpha = 0.2f) // Fondo pastel muy suave
        ),
        border = BorderStroke(1.dp, nota.color.copy(alpha = 0.3f))
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
                text = nota.fecha,
                style = MaterialTheme.typography.labelSmall,
                color = TextoGrisOscuro.copy(alpha = 0.4f)
            )
        }
    }
}


data class NoteItem(
    val titulo: String,
    val contenido: String,
    val fecha: String,
    val color: Color
)

fun getMockNotes() = listOf(
    NoteItem("Idea Regalo", "Comprar algo de madera artesanal para el cumple de Lucas.", "Hoy", TerracotaSuave),
    NoteItem("Receta Ajiaco", "No olvidar la mazorca tierna y las guascas. Preguntar a tía sobre el punto de la papa.", "Ayer", AmbarNeutro),
    NoteItem("Proyecto ADA", "Definir la heurística de fragmentación de tareas para mujeres con alta carga mental.", "28 Mar", VerdeSalvia),
    NoteItem("Cuidado Aloe", "La planta del balcón necesita cambio de maceta pronto.", "25 Mar", VerdeSalvia),
    NoteItem("Lista de Compra", "Leche, pañales, café, aguacate, miel para el skincare.", "Justo ahora", AmbarNeutro)
)