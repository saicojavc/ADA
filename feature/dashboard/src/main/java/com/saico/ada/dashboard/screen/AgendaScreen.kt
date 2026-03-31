package com.saico.ada.dashboard.screen

import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.VerdeSalvia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalviaClaro
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 1. Selector de Fecha Horizontal (Weekly Strip)
        WeeklyCalendarStrip(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Lista de Eventos de la Agenda
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(horizontal = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Eventos para hoy",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoGrisOscuro,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Datos estáticos para la Agenda
            items(getMockAgendaItems()) { item ->
                AgendaCard(item)
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyCalendarStrip(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val days = remember { (0..6).map { LocalDate.now().plusDays(it.toLong()) } }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VerdeSalviaClaro.copy(alpha = 0.5f), // Ligeramente translúcido sobre el fondo crema
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { date ->
                val isSelected = date == selectedDate

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) TerracotaSuave else Color.Transparent)
                        .clickable { onDateSelected(date) }
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek.name.take(1), // L, M, M, J...
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else TextoGrisOscuro.copy(alpha = 0.5f)
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextoGrisOscuro
                    )
                }
            }
        }
    }
}
@Composable
fun AgendaCard(item: AgendaItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Barra lateral de color según categoría
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .clip(CircleShape)
                    .background(item.color)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextoGrisOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange, //Schedule
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextoGrisOscuro.copy(alpha = 0.5f)
                    )
                    Text(
                        text = " ${item.horaInicio} - ${item.horaFin}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoGrisOscuro.copy(alpha = 0.6f)
                    )
                }
            }

            // Etiqueta de Categoría (Pill style)
            Surface(
                color = item.color.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Text(
                    text = item.categoria,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = item.color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
data class AgendaItem(
    val titulo: String,
    val horaInicio: String,
    val horaFin: String,
    val categoria: String,
    val color: androidx.compose.ui.graphics.Color
)

fun getMockAgendaItems() = listOf(
    AgendaItem("Reunión de Diseño UI", "09:00", "10:30", "Trabajo", AmbarNeutro),
    AgendaItem("Yoga Express", "11:00", "11:30", "Bienestar", VerdeSalvia),
    AgendaItem("Almuerzo con Mamá", "13:00", "14:30", "Hogar", TerracotaSuave),
    AgendaItem("Revisión de ADA Project", "16:00", "17:00", "Trabajo", AmbarNeutro)
)