package com.saico.ada.dashboard.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.model.Tarea
import com.saico.ada.ui.theme.*
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTareaDialog(
    onDismiss: () -> Unit,
    onConfirm: (Tarea) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var categoriaSelected by remember { mutableStateOf("Trabajo") }
    var fechaStr by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var horaInicioStr by remember { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var horaFinStr by remember { mutableStateOf(LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))) }
    
    val categorias = listOf(
        CategoryItem("Trabajo", AmbarNeutro, "#F2CC8F"),
        CategoryItem("Hogar", VerdeSalvia, "#81B29A"),
        CategoryItem("Maternidad", TerracotaSuave, "#E07A5F")
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoGrisOscuro,
        unfocusedTextColor = TextoGrisOscuro,
        focusedLabelColor = VerdeSalvia,
        unfocusedLabelColor = TextoGrisOscuro.copy(alpha = 0.6f),
        cursorColor = VerdeSalvia
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Nueva Tarea", color = TextoGrisOscuro, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("¿Qué tienes pendiente?") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro, fontSize = 16.sp)
                )

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fechaStr,
                        onValueChange = { fechaStr = it },
                        label = { Text("Fecha") },
                        placeholder = { Text("AAAA-MM-DD") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        textStyle = TextStyle(color = TextoGrisOscuro)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = horaInicioStr,
                        onValueChange = { horaInicioStr = it },
                        label = { Text("Inicio") },
                        placeholder = { Text("HH:mm") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        textStyle = TextStyle(color = TextoGrisOscuro)
                    )
                    OutlinedTextField(
                        value = horaFinStr,
                        onValueChange = { horaFinStr = it },
                        label = { Text("Fin") },
                        placeholder = { Text("HH:mm") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        textStyle = TextStyle(color = TextoGrisOscuro)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val date = LocalDate.parse(fechaStr)
                        val startT = LocalTime.parse(horaInicioStr)
                        val endT = LocalTime.parse(horaFinStr)
                        val selectedCat = categorias.find { it.name == categoriaSelected }!!
                        
                        if (titulo.isNotBlank()) {
                            onConfirm(
                                Tarea(
                                    titulo = titulo,
                                    descripcion = "",
                                    fechaHoraInicio = LocalDateTime.of(date, startT),
                                    fechaHoraFin = LocalDateTime.of(date, endT),
                                    categoria = selectedCat.name,
                                    colorHex = selectedCat.hex
                                )
                            )
                        }
                    } catch (e: Exception) { }
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
