package com.saico.ada.dashboard.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.model.Tarea
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTareaDialog(
    initialTarea: Tarea? = null,
    onDismiss: () -> Unit,
    onConfirm: (Tarea) -> Unit
) {
    // --- ESTADOS DE DATOS (Objetos reales) ---
    var titulo by remember { mutableStateOf(initialTarea?.titulo ?: "") }
    var categoriaSelected by remember { mutableStateOf(initialTarea?.categoria ?: "Trabajo") }

    // Inicializamos con los valores de la tarea a editar o con la fecha/hora actual
    var selectedDate by remember {
        mutableStateOf(
            initialTarea?.fechaHoraInicio?.toLocalDate() ?: LocalDate.now()
        )
    }
    var startTime by remember {
        mutableStateOf(
            initialTarea?.fechaHoraInicio?.toLocalTime() ?: LocalTime.now().withMinute(0)
        )
    }
    var endTime by remember {
        mutableStateOf(
            initialTarea?.fechaHoraFin?.toLocalTime() ?: LocalTime.now().plusHours(1).withMinute(0)
        )
    }

    val categorias = listOf(
        CategoryItem("Trabajo", AmbarNeutro, "#F2CC8F"),
        CategoryItem("Hogar", VerdeSalvia, "#81B29A"),
        CategoryItem("Maternidad", TerracotaSuave, "#E07A5F")
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoGrisOscuro,
        unfocusedTextColor = TextoGrisOscuro,
        cursorColor = VerdeSalvia
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                if (initialTarea == null) "Nueva Tarea" else "Editar Tarea",
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
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
                    Text(
                        "Categoría",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoGrisOscuro.copy(alpha = 0.6f)
                    )
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
                    Text(
                        "Fecha",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoGrisOscuro.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Generamos los próximos 60 días
                    val days = remember { (0..59).map { LocalDate.now().plusDays(it.toLong()) } }

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
                    Text(
                        "Horario",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoGrisOscuro.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mostramos el rango visualmente
                    Text(
                        text = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                            endTime.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                        }",
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
                                id = initialTarea?.id ?: 0,
                                titulo = titulo,
                                descripcion = initialTarea?.descripcion ?: "",
                                fechaHoraInicio = LocalDateTime.of(selectedDate, startTime),
                                fechaHoraFin = LocalDateTime.of(selectedDate, endTime),
                                categoria = categoriaSelected,
                                colorHex = categorias.find { it.name == categoriaSelected }?.hex
                                    ?: "#81B29A"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    if (initialTarea == null) "Guardar Tarea" else "Actualizar Tarea",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (initialTarea == null) "Quizás luego" else "Cancelar",
                    color = TerracotaSuave.copy(alpha = 0.7f)
                )
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
        border = BorderStroke(
            1.dp,
            if (isSelected) Color.Transparent else TextoGrisOscuro.copy(alpha = 0.1f)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextoGrisOscuro.copy(
                    alpha = 0.5f
                ),
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
    // Definimos los tipos con su configuración visual
    val opcionesBienestar = remember {
        listOf(
            BienestarOption(
                "Hidratación",
                Icons.Rounded.WaterDrop,
                VerdeSalvia,
                "ml",
                100f,
                2000f,
                100f
            ),
            BienestarOption(
                "Pasos",
                Icons.Rounded.DirectionsWalk,
                AmbarNeutro,
                "pasos",
                500f,
                15000f,
                500f
            ),
            BienestarOption(
                "Sueño",
                Icons.Rounded.NightsStay,
                Color(0xFF945FFB),
                "hrs",
                1f,
                12f,
                0.5f
            ),
            BienestarOption("Lectura", Icons.Rounded.MenuBook, TerracotaSuave, "min", 5f, 120f, 5f)
        )
    }

    var seleccionado by remember { mutableStateOf(opcionesBienestar[0]) }
    var valorActual by remember { mutableStateOf(seleccionado.paso) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                "Registro de Bienestar",
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. SELECTOR DE TIPO (Iconos en Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    opcionesBienestar.forEach { opcion ->
                        val isSelected = seleccionado.nombre == opcion.nombre
                        WellnessIconChip(
                            option = opcion,
                            isSelected = isSelected,
                            onClick = {
                                seleccionado = opcion
                                valorActual = opcion.paso // Reiniciamos al paso base al cambiar
                            }
                        )
                    }
                }

                // 2. VISUALIZADOR DE VALOR GRANDE
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (seleccionado.nombre == "Sueño") "%.1f".format(valorActual) else valorActual.toInt()
                            .toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = seleccionado.color
                    )
                    Text(
                        text = seleccionado.unidad,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoGrisOscuro.copy(alpha = 0.5f)
                    )
                }

                // 3. STEPPER / CONTROL TÁCTIL (Sustituye al TextField feo)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Botón Menos
                    CircularActionButton(Icons.Rounded.Remove, seleccionado.color) {
                        if (valorActual > seleccionado.min) valorActual -= seleccionado.paso
                    }

                    // Slider de apoyo para ajustes rápidos
                    Slider(
                        value = valorActual,
                        onValueChange = { valorActual = it },
                        valueRange = seleccionado.min..seleccionado.max,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = seleccionado.color,
                            activeTrackColor = seleccionado.color,
                            inactiveTrackColor = seleccionado.color.copy(alpha = 0.1f)
                        )
                    )

                    // Botón Más
                    CircularActionButton(Icons.Rounded.Add, seleccionado.color) {
                        if (valorActual < seleccionado.max) valorActual += seleccionado.paso
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(seleccionado.nombre, valorActual) },
                colors = ButtonDefaults.buttonColors(containerColor = seleccionado.color),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Confirmar Registro", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar", color = TextoGrisOscuro.copy(alpha = 0.4f))
            }
        }
    )
}

@Composable
fun WellnessIconChip(
    option: BienestarOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = if (isSelected) option.color else Color.White,
            border = BorderStroke(
                1.dp,
                if (isSelected) Color.Transparent else option.color.copy(alpha = 0.2f)
            ),
            shadowElevation = if (isSelected) 4.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.nombre,
                    tint = if (isSelected) Color.White else option.color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = option.nombre,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TextoGrisOscuro else TextoGrisOscuro.copy(alpha = 0.5f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CircularActionButton(icon: ImageVector, color: Color, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(color.copy(alpha = 0.1f), CircleShape)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
    }
}

// Modelo de datos para la configuración
data class BienestarOption(
    val nombre: String,
    val icon: ImageVector,
    val color: Color,
    val unidad: String,
    val min: Float,
    val max: Float,
    val paso: Float
)

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
