package com.saico.ada.dashboard.screen

import androidx.compose.runtime.Composable
// 1. Core de Compose y Layouts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

// 2. Componentes de Material 3 (Importante para el progreso y las tarjetas)
import androidx.compose.material3.*

// 3. Iconos Redondeados (La estética suave de ADA)
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.rounded.Check
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning

// 4. Utilidades de UI
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.ui.theme.BaseCrema
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.theme.VerdeSalviaClaro

// 5. Tus Colores Personalizados
// Asegúrate de tener acceso a: BaseCrema, TextoGrisOscuro, VerdeSalvia, VerdeSalviaClaro, etc.

@Composable
fun WellnessScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Cabecera de Bienestar
        item {
            WellnessHeader(equilibrio = 85) // 85% de equilibrio hoy
        }

        // 2. Widget de Hidratación (Tu sección de Aloe)
        item {
            AloeHydrationCard()
        }

        // 3. Grid de Métricas Rápidas (Pasos y Sueño)
        item {
            MetricsGrid()
        }

        // 4. Lista de Hábitos (Habit Tracker)
        item {
            Text(
                text = "Tus Rituales",
                style = MaterialTheme.typography.titleMedium,
                color = TextoGrisOscuro,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )
        }

        items(getMockHabits()) { habit ->
            HabitRow(habit)
        }
    }
}

@Composable
fun AloeHydrationCard() {
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
            // Un indicador de progreso circular sutil
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = 0.6f,
                    color = VerdeSalvia,
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(60.dp),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )
                Icon(Icons.Rounded.Warning, null, tint = VerdeSalvia) // icon WaterDrop
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text("Hidratación de Aloe", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("600ml / 1L hoy", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { /* Sumar agua */ }) {
                    Text("+ Añadir 200ml", color = VerdeSalvia, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
@Composable
fun MetricsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta de Pasos
        MetricSmallCard(
            label = "Pasos",
            value = "8,432",
            icon = Icons.Rounded.Warning, //DirectionsWalk
            color = AmbarNeutro,
            modifier = Modifier.weight(1f)
        )
        // Tarjeta de Descanso
        MetricSmallCard(
            label = "Sueño",
            value = "7h 20m",
            icon = Icons.Rounded.Check, //NightsStay
            color = Color(0xFF945FFB).copy(alpha = 0.5f), // Un violeta suave
            modifier = Modifier.weight(1f)
        )
    }
}

data class Habit(val nombre: String, val completado: Boolean, val color: Color)

fun getMockHabits() = listOf(
    Habit("Skincare Natural", true, TerracotaSuave),
    Habit("Lectura 15 min", false, AmbarNeutro),
    Habit("Caminata al aire libre", true, VerdeSalvia)
)
@Composable
fun WellnessHeader(equilibrio: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Título de la Sección
        Text(
            text = "Tu Bienestar",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextoGrisOscuro
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Anillo de Equilibrio (La métrica clave de ADA)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Círculo de fondo (Sombra suave o trazo tenue)
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                color = VerdeSalvia.copy(alpha = 0.1f),
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            // Círculo de progreso real
            CircularProgressIndicator(
                progress = equilibrio / 100f,
                modifier = Modifier.fillMaxSize(),
                color = VerdeSalvia,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )

            // Texto central (Porcentaje)
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

        // 3. Mensaje Motivador de ADA
        Surface(
            color = Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = "¡Vas por muy buen camino! Te falta poco para tu meta de hidratación.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoGrisOscuro.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
@Composable
fun HabitRow(habit: Habit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { /* Lógica para alternar estado */ },
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.7f), // Ligeramente transparente para ver el fondo crema
        border = BorderStroke(
            width = 1.dp,
            color = if (habit.completado) habit.color.copy(alpha = 0.3f) else Color.Transparent
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
                // Indicador de color lateral o icono
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(habit.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (habit.completado) Icons.Rounded.Check else Icons.Rounded.Star, // icon SelfCare
                        contentDescription = null,
                        tint = habit.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = habit.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoGrisOscuro,
                        textDecoration = if (habit.completado) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = if (habit.completado) "¡Completado!" else "Ritual pendiente",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoGrisOscuro.copy(alpha = 0.5f)
                    )
                }
            }

            // Checkbox personalizado estilo "Pill"
            IconButton(
                onClick = { /* TODO: Toggle Habit */ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (habit.completado) habit.color else Color.Transparent)
                    .border(1.dp, habit.color, CircleShape)
                    .size(28.dp)
            ) {
                if (habit.completado) {
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
        modifier = modifier
            .height(120.dp), // Altura fija para que ambas tarjetas del Grid midan lo mismo
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono con fondo circular suave
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