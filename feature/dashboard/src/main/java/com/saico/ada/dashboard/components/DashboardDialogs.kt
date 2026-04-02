package com.saico.ada.dashboard.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.model.Tarea
import com.saico.ada.ui.theme.*
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTareaDialog(
    onDismiss: () -> Unit,
    onConfirm: (Tarea) -> Unit
) {
    // --- ESTADOS DE DATOS (Objetos reales) ---
    var titulo by remember { mutableStateOf("") }
    var categoriaSelected by remember { mutableStateOf("Trabajo") }

    // Inicializamos con la fecha/hora actual
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.now().withMinute(0)) } // Redondeamos a la hora
    var endTime by remember { mutableStateOf(LocalTime.now().plusHours(1).withMinute(0)) }

    val categorias = listOf(
        CategoryItem("Trabajo", AmbarNeutro, "#F2CC8F"),
        CategoryItem("Hogar", VerdeSalvia, "#81B29A"),
        CategoryItem("Maternidad", TerracotaSuave, "#E07A5F")
    )

    // Estilos de texto reutilizables (Tu estética)
    val textFieldColors = textFieldColors()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Nueva Tarea", color = TextoGrisOscuro, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp), // Más espacio para respirar
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 1. TÍTULO (Tu diseño original)
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("¿Qué tienes pendiente?") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro, fontSize = 16.sp)
                )

                // 2. CATEGORÍAS (Tu diseño original)
                Column {
                    Text("Categoría", style = MaterialTheme.typography.labelMedium, color = TextoGrisOscuro.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categorias.forEach { cat ->
                            CategoryChip(
                                item = cat,
                                isSelected = categoriaSelected == cat.name,
                                onClick = { categoriaSelected = cat.name }
                            )
                        }
                    }
                }

                Divider(color = TextoGrisOscuro.copy(alpha = 0.05f))

                // 3. SELECTOR DE FECHA ORGÁNICO (Horizontal Scroll)
                Column {
                    Text("Fecha", style = MaterialTheme.typography.labelMedium, color = TextoGrisOscuro.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Generamos los próximos 14 días
                    val days = remember { (0..13).map { LocalDate.now().plusDays(it.toLong()) } }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(days) { date ->
                            val isSelected = date == selectedDate
                            DatePillItem(
                                date = date,
                                isSelected = isSelected,
                                onClick = { selectedDate = date }
                            )
                        }
                    }
                }

                Divider(color = TextoGrisOscuro.copy(alpha = 0.05f))

                // 4. SELECTOR DE HORA CUIDADO (Deslizadores Suaves)
                Column {
                    Text("Horario", style = MaterialTheme.typography.labelMedium, color = TextoGrisOscuro.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mostramos el rango visualmente
                    Text(
                        text = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoGrisOscuro,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Deslizador personalizado para Hora de Inicio
                    AdaTimeSlider(
                        value = startTime.hour.toFloat(),
                        onValueChange = { startTime = LocalTime.of(it.toInt(), 0) },
                        label = "Inicio",
                        color = VerdeSalvia
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Deslizador personalizado para Hora de Fin
                    AdaTimeSlider(
                        value = endTime.hour.toFloat(),
                        onValueChange = { endTime = LocalTime.of(it.toInt(), 0) },
                        label = "Fin",
                        color = TerracotaSuave
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && endTime.isAfter(startTime)) {
                        onConfirm(
                            Tarea(
                                titulo = titulo,
                                descripcion = "",
                                fechaHoraInicio = LocalDateTime.of(selectedDate, startTime),
                                fechaHoraFin = LocalDateTime.of(selectedDate, endTime),
                                categoria = categoriaSelected,
                                colorHex = categorias.find { it.name == categoriaSelected }?.hex ?: "#81B29A"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text("Guardar Tarea", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Quizás luego", color = TerracotaSuave.copy(alpha = 0.7f))
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePillItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayName = date.format(DateTimeFormatter.ofPattern("EEE")).uppercase()
    val dayNumber = date.dayOfMonth.toString()

    Surface(
        modifier = Modifier
            .width(60.dp)
            .height(74.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) VerdeSalvia else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else TextoGrisOscuro.copy(alpha = 0.1f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextoGrisOscuro.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.titleLarge,
                color = if (isSelected) Color.White else TextoGrisOscuro,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun AdaTimeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextoGrisOscuro.copy(alpha = 0.6f),
            modifier = Modifier.width(50.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..23f, // 24 horas
            steps = 22, // Pasos de 1 hora
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.1f)
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryChip(item: CategoryItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) item.color else item.color.copy(alpha = 0.15f),
        border = if (isSelected) null else BorderStroke(1.dp, item.color.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else TextoGrisOscuro,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

data class CategoryItem(val name: String, val color: Color, val hex: String)

@Composable
fun AddBienestarDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float) -> Unit
) {
    var tipo by remember { mutableStateOf("Hidratación") }
    val tipos = listOf("Hidratación", "Pasos", "Sueño", "Skincare", "Lectura")
    var valor by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoGrisOscuro,
        unfocusedTextColor = TextoGrisOscuro,
        cursorColor = VerdeSalvia
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Registro de Bienestar", color = TextoGrisOscuro, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoGrisOscuro)
                    ) {
                        Text("Tipo: $tipo")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        tipos.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    tipo = t
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = valor,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) valor = it },
                    label = { Text("Valor") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = valor.toFloatOrNull()
                    if (v != null) {
                        onConfirm(tipo, v)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TerracotaSuave)
            }
        }
    )
}

@Composable
fun AddNotaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoGrisOscuro,
        unfocusedTextColor = TextoGrisOscuro,
        cursorColor = VerdeSalvia
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Nueva Nota", color = TextoGrisOscuro, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro)
                )
                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        onConfirm(titulo, contenido)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TerracotaSuave)
            }
        }
    )
}
