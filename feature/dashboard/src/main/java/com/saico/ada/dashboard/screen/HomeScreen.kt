package com.saico.ada.dashboard.screen

import android.graphics.Color
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.util.copy
import com.saico.ada.dashboard.state.DashboardEvent
import com.saico.ada.ui.components.AdaSuggestionCard
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.BlancoPuro
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia

@Composable
fun HomeScreen() {
    // Datos estáticos para previsualizar el diseño
    val eventosHoy = listOf(
        DashboardEvent(
            1,
            "08:00",
            "Daily Scrum Team Alpha",
            "TRABAJO",
            AmbarNeutro,
            Icons.Default.Home
        ),
        DashboardEvent(2, "09:15", "Recoger materiales Lucas", "HOGAR", TerracotaSuave, Icons.Rounded.Home),
        DashboardEvent(3, "12:30", "Reunión Cliente Q2", "TRABAJO", AmbarNeutro, Icons.Rounded.Call),
        DashboardEvent(4, "17:30", "Cita Pediatra (Sofía)", "MATERNIDAD", TerracotaSuave, Icons.Rounded.Check)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 1. Cabecera Personalizada
        item {
            HeaderSection(nombre = "Jorge")
        }

        // 2. Tarjeta de Inteligencia (Heurística)
        item {
            AdaSuggestionCard(
                mensaje = "Tienes un hueco de 25 min a las 11:00 AM.",
                accion = "¿Es buen momento para tu rutina de Aloe?"
            )
        }

        // 3. Título de la Agenda
        item {
            Text(
                text = "Tu Día",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextoGrisOscuro,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
        }

        // 4. Timeline Dinámico
        items(eventosHoy) { evento ->
            // Insertamos visualmente el "hueco" antes de la reunión de las 12:30
            if (evento.hora == "12:30") {
                TimeGapDivider(duracion = "25 min")
            }

            TimelineItem(evento)
        }
    }
}

@Composable
fun HeaderSection(nombre: String) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Buenos días, $nombre",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif, // Le da el toque elegante de ADA
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro
        )
        Text(
            text = "Martes, 31 de Marzo",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoGrisOscuro.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun TimelineItem(evento: DashboardEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Indicador de tiempo y línea
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = evento.hora,
                style = MaterialTheme.typography.labelMedium,
                color = TextoGrisOscuro.copy(alpha = 0.5f)
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(2.dp)
                    .height(60.dp)
                    .background(evento.color.copy(alpha = 0.3f))
            )
        }

        // Tarjeta del Evento
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoPuro),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = evento.color.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = evento.icon,
                        contentDescription = null,
                        tint = evento.color,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = evento.titulo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoGrisOscuro
                    )
                    Text(
                        text = evento.categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = evento.color
                    )
                }
            }
        }
    }
}

@Composable
fun TimeGapDivider(duracion: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowUp, //AutoAwesome
            contentDescription = null,
            tint = VerdeSalvia,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = " Espacio libre: $duracion",
            style = MaterialTheme.typography.labelLarge,
            color = VerdeSalvia,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
            drawLine(
                color = VerdeSalvia.copy(alpha = 0.4f),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
    }
}
