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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import java.util.Locale

// ─────────────────────────────────────────────────────────────
//  Utilidad de normalización
//  Convierte "Réunión" → "reunion", "niño" → "nino", etc.
// ─────────────────────────────────────────────────────────────
private fun String.normalize(): String {
    return this
        .lowercase()
        .replace('á', 'a').replace('é', 'e').replace('í', 'i')
        .replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
        .replace('ñ', 'n').replace('à', 'a').replace('è', 'e')
        .replace('ì', 'i').replace('ò', 'o').replace('ù', 'u')
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTareaDialog(
    tarea: Tarea? = null, // Agregado para soportar edición
    isMother: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Tarea) -> Unit
) {
    // Inicialización de estados vinculada a la tarea recibida
    var titulo by remember(tarea) { mutableStateOf(tarea?.titulo ?: "") }
    var categoriaSelected by remember(tarea) { mutableStateOf(tarea?.categoria ?: "Trabajo") }

    var selectedDate by remember(tarea) { mutableStateOf(tarea?.fechaHoraInicio?.toLocalDate() ?: LocalDate.now()) }
    var selectedStartTime by remember(tarea) { mutableStateOf(tarea?.fechaHoraInicio?.toLocalTime() ?: LocalTime.now()) }
    var selectedEndTime by remember(tarea) { mutableStateOf(tarea?.fechaHoraFin?.toLocalTime() ?: LocalTime.now().plusHours(1)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val baseCategorias = mutableListOf(
        CategoryItem("Trabajo", AmbarNeutro, "#F2CC8F"),
        CategoryItem("Hogar", VerdeSalvia, "#81B29A"),
        CategoryItem("Bienestar", Color(0xFF945FFB), "#945FFB")
    )
    if (isMother) {
        baseCategorias.add(CategoryItem("Maternidad", TerracotaSuave, "#E07A5F"))
    }

    // --- INTELIGENCIA ADA: Categorización Predictiva (Preservada intacta) ---
    LaunchedEffect(titulo) {
        // Solo activamos la predicción si el usuario está escribiendo un nuevo título o modificando uno
        if (tarea == null || titulo != tarea.titulo) {
            val text = titulo.normalize()

            // ── helpers locales ──────────────────────────────────────────
            fun List<String>.matchesAny() = any { text.contains(it.normalize()) }

            // ── Diccionarios por categoría ───────────────────────────────

            val palabrasMaternidad = listOf(
                "bebe", "bebe", "nino", "nina", "hijo", "hija",
                "hijos", "hijas", "recien nacido", "lactancia", "leche materna",
                "biberon", "chupete", "cuna", "carrito", "cochecito",
                "pañal", "panal", "pediatra", "guarderia", "jardin infantil",
                "colegio", "escuela", "tarea escolar", "mochila", "uniforme",
                "cumpleanos nino", "cumpleanos nina", "fiesta infantil",
                "embarazo", "embarazada", "ecografia", "ultrasonido",
                "obstetra", "ginecologo", "parto", "preparto", "posparto",
                "cesarea", "semana de gestacion", "primer trimestre",
                "nana", "bua", "berrinche", "rabieta", "siesta bebe",
                "introduccion alimentaria", "papilla", "puré bebe",
                "abuela cuida", "abuela bebe", "guarderia"
            )

            val palabrasTrabajo = listOf(
                "reunion", "junta", "scrum", "daily", "standup", "sprint",
                "retrospectiva", "planning", "one on one", "1 on 1",
                "llamada de trabajo", "videollamada trabajo", "zoom work",
                "meet work", "teams", "slack",
                "cliente", "proyecto", "entregable", "deadline", "plazo",
                "informe", "reporte", "presentacion trabajo", "propuesta",
                "cotizacion", "factura", "cobro", "pago proveedor",
                "contrato", "firma contrato", "revision codigo", "deploy",
                "lanzamiento", "release", "soporte", "ticket", "incidencia",
                "trabajo", "oficina", "office", "home office", "remoto",
                "jefe", "jefa", "gerente", "director", "ceo", "rrhh",
                "recursos humanos", "entrevista trabajo", "evaluacion",
                "capacitacion", "formacion laboral", "curso trabajo",
                "networking", "conferencia", "congreso", "taller profesional",
                "workshop", "webinar", "seminario"
            )

            val palabrasHogar = listOf(
                "limpiar", "limpeza", "barrer", "fregar", "trapear", "aspirar",
                "ordenar", "organizar casa", "desinfectar", "lavar ropa",
                "tender ropa", "planchar", "doblar ropa", "guardar ropa",
                "cocinar", "receta", "preparar comida", "hornear", "amasar",
                "descongelar", "marinar", "meal prep",
                "compra", "compras", "supermercado", "mercado", "tienda",
                "lista compras", "mandado", "verduleria", "carniceria",
                "ferreteria", "farmacia hogar",
                "reparar", "arreglar", "plomero", "electricista", "pintar casa",
                "pared", "goteras", "fuga", "instalar", "montar mueble",
                "ikea", "perforacion", "taladro", "tornillo",
                "casa", "hogar", "habitacion", "cuarto", "bano", "cocina",
                "jardin", "patio", "balcon", "terraza", "garage",
                "perro", "gato", "mascota", "veterinario", "pasear perro",
                "comida perro", "comida gato", "vacuna mascota"
            )

            val palabrasBienestar = listOf(
                "yoga", "gym", "gimnasio", "ejercicio", "entrenamiento",
                "crossfit", "pilates", "spinning", "nadar", "natacion",
                "correr", "running", "caminar", "senderismo", "ciclismo",
                "bicicleta", "pesas", "cardio", "estiramientos", "flexiones",
                "abdominales", "zumba", "baile", "kickboxing", "boxeo",
                "medico", "doctor", "cita medica", "consulta", "chequeo",
                "analisis", "sangre", "presion arterial", "vacuna", "vacunacion",
                "farmacia", "medicamento", "pastilla", "tratamiento", "terapia fisica",
                "fisioterapia", "quiropraxia", "masaje", "acupuntura",
                "dentista", "odontologo", "oculista", "optomentista",
                "meditar", "meditacion", "mindfulness", "respiracion",
                "psicologo", "psiquiatra", "terapia", "sesion terapia",
                "diario emocional", "journaling", "gratitud",
                "descanso", "dormir", "siesta", "nap", "descansar",
                "relajar", "relajacion", "spa", "bano relajante",
                "dieta", "nutricion", "nutricionista", "ayuno", "comer sano",
                "ensalada", "proteina", "suplemento", "vitamina", "hidratacion",
                "agua", "infusion", "batido saludable",
                "piel", "skincare", "crema", "serum", "rutina facial",
                "mascarilla", "protector solar", "hidratante", "peluqueria",
                "corte pelo", "manicura", "pedicura",
                "bienestar", "autocuidado", "autoestima", "habito saludable",
                "reto saludable", "paso diario", "pasos"
            )

            val palabrasPersonal = listOf(
                "banco", "transferencia", "pago", "factura personal",
                "impuesto", "declaracion", "seguro", "poliza",
                "inversion", "ahorro", "presupuesto",
                "tramite", "cita gobierno", "cita banco", "renovar",
                "pasaporte", "dni", "cedula", "licencia conducir",
                "registro", "notaria", "abogado",
                "amigo", "amiga", "cena", "comida con", "cafe con",
                "cumpleanos", "aniversario", "boda", "evento",
                "pelicula", "concierto", "teatro", "exposicion",
                "viaje", "vuelo", "hotel", "reserva",
                "leer", "libro", "curso", "aprender", "idioma",
                "ingles", "podcast", "video tutorial"
            )

            data class CandidatoCategoria(val nombre: String, val palabras: List<String>)

            val candidatos = buildList {
                if (isMother) add(CandidatoCategoria("Maternidad", palabrasMaternidad))
                add(CandidatoCategoria("Trabajo",   palabrasTrabajo))
                add(CandidatoCategoria("Hogar",     palabrasHogar))
                add(CandidatoCategoria("Bienestar", palabrasBienestar))
                add(CandidatoCategoria("Personal",  palabrasPersonal))
            }

            val ganador = candidatos
                .map { cat ->
                    val hits = cat.palabras.count { kw -> text.contains(kw.normalize()) }
                    cat to hits
                }
                .filter { (_, hits) -> hits > 0 }
                .maxByOrNull { (_, hits) -> hits }

            if (ganador != null) {
                categoriaSelected = ganador.first.nombre
            }
        }
    }

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
        title = { Text(if (tarea == null) "Nueva Tarea" else "Editar Tarea", color = TextoGrisOscuro, fontWeight = FontWeight.Bold) },
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
                    Text(
                        "Categoría sugerida",
                        style = MaterialTheme.typography.labelSmall,
                        color = VerdeSalvia,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(baseCategorias) { cat ->
                            CategoryChip(
                                item = cat,
                                isSelected = categoriaSelected == cat.name,
                                onClick = { categoriaSelected = cat.name }
                            )
                        }
                    }
                }

                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.CalendarMonth, null, tint = VerdeSalvia)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = selectedDate.format(
                                DateTimeFormatter.ofPattern(
                                    "EEEE, d 'de' MMMM",
                                    Locale("es", "ES")
                                )
                            ),
                            color = TextoGrisOscuro
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimeSelectionCard(
                        label = "Inicio",
                        time = selectedStartTime,
                        modifier = Modifier.weight(1f),
                        onClick = { showStartTimePicker = true }
                    )
                    TimeSelectionCard(
                        label = "Fin",
                        time = selectedEndTime,
                        modifier = Modifier.weight(1f),
                        onClick = { showEndTimePicker = true }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedCat = baseCategorias.find { it.name == categoriaSelected }!!
                    if (titulo.isNotBlank()) {
                        onConfirm(
                            Tarea(
                                id = tarea?.id ?: 0,
                                titulo = titulo,
                                descripcion = tarea?.descripcion ?: "",
                                fechaHoraInicio = LocalDateTime.of(selectedDate, selectedStartTime),
                                fechaHoraFin = LocalDateTime.of(selectedDate, selectedEndTime),
                                categoria = selectedCat.name,
                                colorHex = selectedCat.hex,
                                estaCompletada = tarea?.estaCompletada ?: false
                            )
                        )
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

    if (showDatePicker) {
        AdaDatePickerWheelDialog(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = {
                selectedDate = it
                showDatePicker = false
            }
        )
    }

    if (showStartTimePicker) {
        AdaTimeWheelPickerDialog(
            title = "Hora de Inicio",
            initialTime = selectedStartTime,
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                selectedStartTime = it
                if (selectedEndTime.isBefore(it)) selectedEndTime = it.plusHours(1)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        AdaTimeWheelPickerDialog(
            title = "Hora de Fin",
            initialTime = selectedEndTime,
            onDismiss = { showEndTimePicker = false },
            onConfirm = {
                selectedEndTime = it
                showEndTimePicker = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdaDatePickerWheelDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(initialDate.dayOfMonth) }
    var selectedMonth by remember { mutableIntStateOf(initialDate.monthValue) }
    var selectedYear by remember { mutableIntStateOf(initialDate.year) }

    val months =
        listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    val years = (2024..2030).map { it.toString() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = BaseCrema,
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Selecciona la fecha",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoGrisOscuro,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelColumn(
                        items = (1..31).map { it.toString().padStart(2, '0') },
                        selectedIndex = selectedDay - 1,
                        onValueChange = { selectedDay = it + 1 },
                        modifier = Modifier.weight(1f)
                    )
                    WheelColumn(
                        items = months,
                        selectedIndex = selectedMonth - 1,
                        onValueChange = { selectedMonth = it + 1 },
                        modifier = Modifier.weight(1.2f)
                    )
                    WheelColumn(
                        items = years,
                        selectedIndex = years.indexOf(selectedYear.toString()),
                        onValueChange = { selectedYear = years[it].toInt() },
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = TerracotaSuave) }
                    TextButton(onClick = {
                        try {
                            val finalDate = LocalDate.of(selectedYear, selectedMonth, 1)
                            val maxDay = finalDate.lengthOfMonth()
                            onConfirm(
                                LocalDate.of(
                                    selectedYear,
                                    selectedMonth,
                                    selectedDay.coerceIn(1, maxDay)
                                )
                            )
                        } catch (e: Exception) {
                            onConfirm(LocalDate.now())
                        }
                    }) { Text("Confirmar", color = VerdeSalvia, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdaTimeWheelPickerDialog(
    title: String,
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableIntStateOf(initialTime.minute) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = BaseCrema,
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoGrisOscuro,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelColumn(
                        items = (0..23).map { it.toString().padStart(2, '0') },
                        selectedIndex = selectedHour,
                        onValueChange = { selectedHour = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        ":",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextoGrisOscuro
                    )
                    WheelColumn(
                        items = (0..59).map { it.toString().padStart(2, '0') },
                        selectedIndex = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = TerracotaSuave) }
                    TextButton(onClick = {
                        onConfirm(LocalTime.of(selectedHour, selectedMinute))
                    }) { Text("Confirmar", color = VerdeSalvia, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun WheelColumn(
    items: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(VerdeSalvia.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 60.dp)
        ) {
            items(items.size) { index ->
                val isSelected = index == selectedIndex
                Text(
                    text = items[index],
                    style = if (isSelected) MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    else MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) VerdeSalvia else TextoGrisOscuro.copy(alpha = 0.3f),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onValueChange(index) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSelectionCard(label: String, time: LocalTime, modifier: Modifier, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextoGrisOscuro.copy(alpha = 0.6f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.AccessTime,
                    null,
                    tint = VerdeSalvia,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = TextoGrisOscuro,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryChip(item: CategoryItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .height(40.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) item.color else item.color.copy(alpha = 0.15f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            item.color.copy(alpha = 0.3f)
        )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddBienestarDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, LocalTime?) -> Unit
) {
    var nombreRitual by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoGrisOscuro,
        unfocusedTextColor = TextoGrisOscuro,
        focusedLabelColor = VerdeSalvia,
        cursorColor = VerdeSalvia
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                "Nuevo Ritual Personalizado",
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Dale un nombre a tu nuevo ritual y asígnale una hora para verlo en tu día.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoGrisOscuro.copy(alpha = 0.7f)
                )

                OutlinedTextField(
                    value = nombreRitual,
                    onValueChange = { nombreRitual = it },
                    label = { Text("¿Cómo se llama el ritual?") },
                    placeholder = { Text("Ej: Yoga matutino") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro, fontSize = 16.sp)
                )

                OutlinedCard(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AccessTime, null, tint = VerdeSalvia)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (selectedTime == null) "Sin hora establecida" else "Programado: ${
                                selectedTime?.format(
                                    DateTimeFormatter.ofPattern("HH:mm")
                                )
                            }",
                            color = TextoGrisOscuro
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreRitual.isNotBlank()) onConfirm(nombreRitual, selectedTime)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp),
                enabled = nombreRitual.isNotBlank()
            ) {
                Text("Crear", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TerracotaSuave) }
        }
    )

    if (showTimePicker) {
        AdaTimeWheelPickerDialog(
            title = "Hora del Ritual",
            initialTime = selectedTime ?: LocalTime.of(8, 0),
            onDismiss = { showTimePicker = false },
            onConfirm = {
                selectedTime = it
                showTimePicker = false
            }
        )
    }
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
