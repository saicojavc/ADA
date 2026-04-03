package com.saico.ada.dashboard.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.dashboard.AgendaViewMode
import com.saico.ada.model.Tarea
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.VerdeSalviaClaro
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

fun parseColorSafe(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color.Gray
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTaskColorsForDate(date: LocalDate, tareas: List<Tarea>): List<Color> =
    tareas
        .filter { it.fechaHoraInicio.toLocalDate() == date }
        .map { parseColorSafe(it.colorHex) }
        .distinct()
        .take(3)

private val DAY_HEADERS = listOf("L", "M", "X", "J", "V", "S", "D")
private val ES = Locale("es")

// ---------------------------------------------------------------------------
// DayCell — celda reutilizable en los 3 modos
// ---------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    taskColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val bgColor = when {
        isSelected -> TerracotaSuave
        isToday -> TerracotaSuave.copy(alpha = 0.15f)
        else -> Color.Transparent
    }
    val textColor = if (isSelected) Color.White else TextoGrisOscuro

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(if (compact) 6.dp else 12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(if (compact) 2.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = if (compact) MaterialTheme.typography.labelSmall
                    else MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        if (taskColors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(if (compact) 4.dp else 6.dp)
            ) {
                taskColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(if (compact) 3.dp else 5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else color)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// ViewModeSelector — chips Semanal / Mensual / Anual
// ---------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewModeSelector(
    currentMode: AgendaViewMode,
    onModeSelected: (AgendaViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                AgendaViewMode.entries.forEach { mode ->
                    val isSelected = mode == currentMode
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = if (isSelected) TerracotaSuave else Color.Transparent,
                        modifier = Modifier.clickable { onModeSelected(mode) }
                    ) {
                        Text(
                            text = when (mode) {
                                AgendaViewMode.SEMANAL -> "Semanal"
                                AgendaViewMode.MENSUAL -> "Mensual"
                                AgendaViewMode.ANUAL -> "Anual"
                            },
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) Color.White else TextoGrisOscuro,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// WeeklyCalendarStrip
// ---------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyCalendarStrip(
    selectedDate: LocalDate,
    tareas: List<Tarea>,
    onDateSelected: (LocalDate) -> Unit
) {
    // Resetea cuando cambia selectedDate desde fuera (al cambiar de modo)
    var displayedWeekStart by remember(selectedDate) {
        mutableStateOf(selectedDate.with(DayOfWeek.MONDAY))
    }
    val days = remember(displayedWeekStart) {
        (0..6).map { displayedWeekStart.plusDays(it.toLong()) }
    }
    val today = LocalDate.now()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VerdeSalviaClaro.copy(alpha = 0.4f),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // Navegación de semana
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    displayedWeekStart = displayedWeekStart.minusWeeks(1)
                }) {
                    Icon(Icons.Rounded.ChevronLeft, contentDescription = "Semana anterior", tint = TextoGrisOscuro)
                }
                Text(
                    text = buildString {
                        append(displayedWeekStart.format(DateTimeFormatter.ofPattern("d MMM", ES)))
                        append(" – ")
                        append(days.last().format(DateTimeFormatter.ofPattern("d MMM yyyy", ES)))
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoGrisOscuro,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = {
                    displayedWeekStart = displayedWeekStart.plusWeeks(1)
                }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = "Semana siguiente", tint = TextoGrisOscuro)
                }
            }

            // Dias de la semana
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.forEach { date ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = date.dayOfWeek
                                .getDisplayName(TextStyle.SHORT, ES)
                                .take(1).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoGrisOscuro.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == today,
                            taskColors = getTaskColorsForDate(date, tareas),
                            onClick = { onDateSelected(date) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// MonthlyCalendarGrid
// ---------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendarGrid(
    selectedDate: LocalDate,
    tareas: List<Tarea>,
    onDateSelected: (LocalDate) -> Unit
) {
    var displayedMonth by remember(selectedDate) {
        mutableStateOf(YearMonth.from(selectedDate))
    }
    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Cabecera mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = null, tint = TextoGrisOscuro)
            }
            Text(
                text = displayedMonth
                    .format(DateTimeFormatter.ofPattern("MMMM yyyy", ES))
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextoGrisOscuro)
            }
        }

        // Cabecera dias de semana
        Row(modifier = Modifier.fillMaxWidth()) {
            DAY_HEADERS.forEach { h ->
                Text(
                    text = h,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoGrisOscuro.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid de dias
        val firstDay = displayedMonth.atDay(1)
        val daysInMonth = displayedMonth.lengthOfMonth()
        val startOffset = firstDay.dayOfWeek.value - 1 // 0 = Lunes
        val rows = (startOffset + daysInMonth + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val dayNumber = row * 7 + col - startOffset + 1
                    if (dayNumber < 1 || dayNumber > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).height(44.dp))
                    } else {
                        val date = displayedMonth.atDay(dayNumber)
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == today,
                            taskColors = getTaskColorsForDate(date, tareas),
                            onClick = { onDateSelected(date) },
                            modifier = Modifier.weight(1f).height(44.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// AnnualCalendarView — 12 mini-meses en grid 2 columnas
// ---------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnnualCalendarView(
    selectedDate: LocalDate,
    tareas: List<Tarea>,
    onDateSelected: (LocalDate) -> Unit,
    onSwitchToMonthly: () -> Unit
) {
    var displayedYear by remember(selectedDate) { mutableStateOf(selectedDate.year) }
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabecera año
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayedYear-- }) {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = null, tint = TextoGrisOscuro)
            }
            Text(
                text = displayedYear.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { displayedYear++ }) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextoGrisOscuro)
            }
        }

        // 6 filas × 2 columnas = 12 meses
        for (rowIdx in 0 until 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (colIdx in 0..1) {
                    val monthNumber = rowIdx * 2 + colIdx + 1
                    MiniMonthGrid(
                        yearMonth = YearMonth.of(displayedYear, monthNumber),
                        selectedDate = selectedDate,
                        tareas = tareas,
                        today = today,
                        onDateSelected = { date ->
                            onDateSelected(date)
                            onSwitchToMonthly()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// MiniMonthGrid — version compacta para la vista anual
// ---------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MiniMonthGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    tareas: List<Tarea>,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value - 1
    val rows = (startOffset + daysInMonth + 6) / 7

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Nombre del mes
            Text(
                text = yearMonth
                    .format(DateTimeFormatter.ofPattern("MMM", ES))
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium,
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Cabeceras dia
            Row(modifier = Modifier.fillMaxWidth()) {
                DAY_HEADERS.forEach { h ->
                    Text(
                        text = h,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 7.sp,
                        color = TextoGrisOscuro.copy(alpha = 0.4f)
                    )
                }
            }

            // Grid de dias compacto
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val dayNumber = row * 7 + col - startOffset + 1
                        if (dayNumber < 1 || dayNumber > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).height(16.dp))
                        } else {
                            val date = yearMonth.atDay(dayNumber)
                            val hasTasks = tareas.any { it.fechaHoraInicio.toLocalDate() == date }
                            val taskColor = tareas
                                .firstOrNull { it.fechaHoraInicio.toLocalDate() == date }
                                ?.colorHex?.let { parseColorSafe(it) }
                            val isSelected = date == selectedDate
                            val isToday = date == today

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> TerracotaSuave
                                            isToday -> TerracotaSuave.copy(alpha = 0.15f)
                                            hasTasks -> (taskColor ?: Color.Gray).copy(alpha = 0.18f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontSize = 7.sp,
                                    color = if (isSelected) Color.White else TextoGrisOscuro,
                                    fontWeight = if (isSelected || isToday || hasTasks) FontWeight.Bold
                                               else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}