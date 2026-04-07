package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.AgendaViewMode
import com.saico.ada.dashboard.components.AnnualCalendarView
import com.saico.ada.dashboard.components.MonthlyCalendarGrid
import com.saico.ada.dashboard.components.ViewModeSelector
import com.saico.ada.dashboard.components.WeeklyCalendarStrip
import com.saico.ada.model.Tarea
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.util.toComposeColor
import com.saico.ada.ui.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(
    todasLasTareas: List<Tarea>,
    selectedDate: LocalDate,
    agendaViewMode: AgendaViewMode,
    onDateSelected: (LocalDate) -> Unit,
    onViewModeChanged: (AgendaViewMode) -> Unit
) {
    val itemsAgenda = todasLasTareas.filter { it.fechaHoraInicio.toLocalDate() == selectedDate }
        .sortedBy { it.fechaHoraInicio.toLocalTime() }
    
    val now = LocalDateTime.now()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item { ViewModeSelector(currentMode = agendaViewMode, onModeSelected = onViewModeChanged) }

        item {
            when (agendaViewMode) {
                AgendaViewMode.SEMANAL -> WeeklyCalendarStrip(selectedDate = selectedDate, tareas = todasLasTareas, onDateSelected = onDateSelected)
                AgendaViewMode.MENSUAL -> MonthlyCalendarGrid(selectedDate = selectedDate, tareas = todasLasTareas, onDateSelected = onDateSelected)
                AgendaViewMode.ANUAL -> AnnualCalendarView(selectedDate = selectedDate, tareas = todasLasTareas, onDateSelected = onDateSelected, onSwitchToMonthly = { onViewModeChanged(AgendaViewMode.MENSUAL) })
            }
        }

        item {
            AgendaDayHeader(date = selectedDate, taskCount = itemsAgenda.size, modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
        }

        if (itemsAgenda.isEmpty()) {
            item { EmptyDayState(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) }
        } else {
            items(itemsAgenda) { tarea ->
                TareaAgendaCard(tarea = tarea, now = now, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaDayHeader(date: LocalDate, taskCount: Int, modifier: Modifier = Modifier) {
    val es = Locale("es")
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, es).replaceFirstChar { it.uppercase() }
    val dateFormatted = date.format(DateTimeFormatter.ofPattern("d 'de' MMMM", es))
    val isToday = date == LocalDate.now()

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(
                text = if (isToday) stringResource(R.string.agenda_today_prefix, dayName) else dayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(text = dateFormatted.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = TextoGrisOscuro.copy(alpha = 0.5f))
        }
        if (taskCount > 0) {
            Surface(shape = CircleShape, color = TerracotaSuave.copy(alpha = 0.12f)) {
                Text(text = stringResource(R.string.agenda_items_count, taskCount), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = TerracotaSuave, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TareaAgendaCard(tarea: Tarea, now: LocalDateTime, modifier: Modifier = Modifier) {
    val color = tarea.colorHex.toComposeColor()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val isPast = tarea.fechaHoraInicio.isBefore(now)

    Card(modifier = modifier.fillMaxWidth().alpha(if (isPast) 0.6f else 1f), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = if (isPast) 0.dp else 1.dp)) {
        Row(modifier = Modifier.padding(16.dp).height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(4.dp).clip(CircleShape).background(if (isPast) color.copy(alpha = 0.4f) else color))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tarea.titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextoGrisOscuro, textDecoration = if (isPast) TextDecoration.LineThrough else TextDecoration.None)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextoGrisOscuro.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${tarea.fechaHoraInicio.format(timeFormatter)} – ${tarea.fechaHoraFin.format(timeFormatter)}", style = MaterialTheme.typography.labelMedium, color = TextoGrisOscuro.copy(alpha = 0.6f), textDecoration = if (isPast) TextDecoration.LineThrough else TextDecoration.None)
                }
            }
            Surface(color = color.copy(alpha = 0.1f), shape = CircleShape) {
                Text(text = tarea.categoria, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (isPast) color.copy(alpha = 0.6f) else color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmptyDayState(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = CircleShape, color = VerdeSalvia.copy(alpha = 0.1f), modifier = Modifier.size(72.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Rounded.CalendarToday, contentDescription = null, tint = VerdeSalvia, modifier = Modifier.size(36.dp)) }
        }
        Text(text = stringResource(R.string.agenda_free_day), style = MaterialTheme.typography.titleMedium, color = TextoGrisOscuro, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        Text(text = stringResource(R.string.agenda_empty_message), style = MaterialTheme.typography.bodySmall, color = TextoGrisOscuro.copy(alpha = 0.5f), textAlign = TextAlign.Center)
    }
}
