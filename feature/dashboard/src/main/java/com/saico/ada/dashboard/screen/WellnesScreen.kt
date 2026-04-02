package com.saico.ada.dashboard.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.model.Bienestar
import com.saico.ada.ui.theme.*

@Composable
fun WellnessScreen(
    uiState: DashboardState,
    viewModel: DashboardViewModel
) {
    val successState = uiState as? DashboardState.Success
    val registros = successState?.registrosBienestar ?: emptyList()

    // Calculamos un equilibrio promedio basado en las métricas
    val promedioEquilibrio = if (registros.isNotEmpty()) {
        (registros.map { if (it.metaObjetivo > 0) (it.valorActual / it.metaObjetivo) * 100 else 0f }.average()).toInt()
            .coerceIn(0, 100)
    } else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Cabecera de Bienestar (Siempre visible)
        item {
            WellnessHeader(equilibrio = promedioEquilibrio)
        }

        // 2. Widget de Hidratación (Visible si existe el registro o mensaje de vacío)
        val hidratacion = registros.find { it.tipo == "Hidratación" }
        item {
            if (hidratacion != null) {
                AloeHydrationCard(hidratacion)
            } else {
                // Placeholder o mensaje si no hay registro de hidratación aún
                EmptySectionMessage("No hay registros de hidratación para hoy.")
            }
        }

        // 3. Grid de Métricas (Siempre visible, con valores en 0 si no hay datos)
        item {
            MetricsGrid(registros)
        }

        // 4. Lista de Hábitos
        item {
            Text(
                text = "Tus Rituales",
                style = MaterialTheme.typography.titleMedium,
                color = TextoGrisOscuro,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )
        }

        val rituales = registros.filter { it.tipo != "Hidratación" && it.tipo != "Pasos" && it.tipo != "Sueño" }
        
        if (rituales.isEmpty()) {
            item {
                EmptySectionMessage("Aún no tienes rituales registrados.")
            }
        } else {
            items(rituales) { registro ->
                HabitRow(registro)
            }
        }
    }
}

@Composable
fun EmptySectionMessage(mensaje: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoGrisOscuro.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WellnessHeader(equilibrio: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tu Bienestar",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                color = VerdeSalvia.copy(alpha = 0.1f),
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = equilibrio / 100f,
                modifier = Modifier.fillMaxSize(),
                color = VerdeSalvia,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$equilibrio%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = TextoGrisOscuro
                )
                Text(
                    text = "Equilibrio",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoGrisOscuro.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            color = Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = "¡Vas por muy buen camino! ADA analiza tu equilibrio diario.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoGrisOscuro.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun AloeHydrationCard(registro: Bienestar) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeSalviaClaro),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                val progress = if (registro.metaObjetivo > 0) registro.valorActual / registro.metaObjetivo else 0f
                CircularProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    color = VerdeSalvia,
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(60.dp),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )
                Icon(Icons.Rounded.Warning, null, tint = VerdeSalvia)
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text("Hidratación de Aloe", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("${registro.valorActual.toInt()}${registro.unidad} / ${registro.metaObjetivo.toInt()}${registro.unidad} hoy", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { /* TODO: Implementar suma en ViewModel */ }) {
                    Text("+ Añadir 200ml", color = VerdeSalvia, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(registros: List<Bienestar>) {
    val pasos = registros.find { it.tipo == "Pasos" }
    val sueno = registros.find { it.tipo == "Sueño" }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MetricSmallCard(
            label = "Pasos",
            value = pasos?.valorActual?.toInt()?.toString() ?: "0",
            icon = Icons.Rounded.Warning,
            color = AmbarNeutro,
            modifier = Modifier.weight(1f)
        )
        MetricSmallCard(
            label = "Sueño",
            value = sueno?.valorActual?.let { "${it.toInt()}h" } ?: "0h",
            icon = Icons.Rounded.Check,
            color = Color(0xFF945FFB).copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HabitRow(registro: Bienestar) {
    val completado = registro.valorActual >= registro.metaObjetivo
    val color = when(registro.tipo) {
        "Skincare" -> TerracotaSuave
        "Lectura" -> AmbarNeutro
        else -> VerdeSalvia
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.7f),
        border = BorderStroke(
            width = 1.dp,
            color = if (completado) color.copy(alpha = 0.3f) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (completado) Icons.Rounded.Check else Icons.Rounded.Star,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = registro.tipo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoGrisOscuro,
                        textDecoration = if (completado) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = if (completado) "¡Completado!" else "Ritual pendiente",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoGrisOscuro.copy(alpha = 0.5f)
                    )
                }
            }

            IconButton(
                onClick = { /* TODO: Toggle en ViewModel */ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (completado) color else Color.Transparent)
                    .border(1.dp, color, CircleShape)
                    .size(28.dp)
            ) {
                if (completado) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricSmallCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextoGrisOscuro
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoGrisOscuro.copy(alpha = 0.6f)
                )
            }
        }
    }
}
