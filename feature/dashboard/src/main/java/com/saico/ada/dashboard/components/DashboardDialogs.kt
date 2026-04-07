package com.saico.ada.dashboard.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.saico.ada.model.Tarea
import com.saico.ada.model.TipoRepeticion
import com.saico.ada.ui.R
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

// ─────────────────────────────────────────────────────────────
//  Utilidad de normalización
// ─────────────────────────────────────────────────────────────
private fun String.normalize(): String {
    return this
        .lowercase()
        .replace('á', 'a').replace('é', 'e').replace('í', 'i')
        .replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
        .replace('ñ', 'n').replace('à', 'a').replace('è', 'e')
        .replace('ì', 'i').replace('ò', 'o').replace('ù', 'u')
}

@SuppressLint("LocalContextConfigurationRead")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTareaDialog(
    tarea: Tarea? = null,
    isMother: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Tarea) -> Unit
) {
    val catWork = stringResource(R.string.cat_work)
    val catHome = stringResource(R.string.cat_home)
    val catWellbeing = stringResource(R.string.cat_wellbeing)
    val catMaternity = stringResource(R.string.cat_maternity)
    val catPersonal = stringResource(R.string.cat_personal)

    var titulo by remember(tarea) { mutableStateOf(tarea?.titulo ?: "") }
    var categoriaSelected by remember(tarea) { mutableStateOf(tarea?.categoria ?: catWork) }

    var selectedDate by remember(tarea) {
        mutableStateOf(tarea?.fechaHoraInicio?.toLocalDate() ?: LocalDate.now())
    }
    var selectedStartTime by remember(tarea) {
        mutableStateOf(tarea?.fechaHoraInicio?.toLocalTime() ?: LocalTime.now())
    }
    var selectedEndTime by remember(tarea) {
        mutableStateOf(tarea?.fechaHoraFin?.toLocalTime() ?: LocalTime.now().plusHours(1))
    }

    // Estados para repetición
    var esRepetible by remember(tarea) { mutableStateOf(tarea?.esPlantilla ?: false) }
    var tipoRepeticionSelected by remember(tarea) {
        mutableStateOf(
            if (tarea?.tipoRepeticion == TipoRepeticion.NINGUNA && tarea?.esPlantilla == true) TipoRepeticion.TODOS_LOS_DIAS else tarea?.tipoRepeticion
                ?: TipoRepeticion.TODOS_LOS_DIAS
        )
    }
    var diasSeleccionados by remember(tarea) {
        mutableStateOf(tarea?.diasRepeticion ?: emptyList<DayOfWeek>())
    }
    var fechaFinRepeticion by remember(tarea) { mutableStateOf(tarea?.fechaFinRepeticion) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showEndRepeatDatePicker by remember { mutableStateOf(false) }

    val baseCategorias =
        remember(isMother, catWork, catHome, catWellbeing, catMaternity, catPersonal) {
            val list = mutableListOf(
                CategoryItem(catWork, AmbarNeutro, "#F2CC8F"),
                CategoryItem(catHome, VerdeSalvia, "#81B29A"),
                CategoryItem(catWellbeing, Color(0xFF945FFB), "#945FFB"),
                CategoryItem(catPersonal, Color(0xFF5A9BD5), "#5A9BD5")
            )
            if (isMother) {
                list.add(CategoryItem(catMaternity, TerracotaSuave, "#E07A5F"))
            }
            list
        }

    LaunchedEffect(titulo) {
        val text = titulo.normalize()
        if (text.isBlank()) return@LaunchedEffect

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
            if (isMother) add(CandidatoCategoria(catMaternity, palabrasMaternidad))
            add(CandidatoCategoria(catWork, palabrasTrabajo))
            add(CandidatoCategoria(catHome, palabrasHogar))
            add(CandidatoCategoria(catWellbeing, palabrasBienestar))
            add(CandidatoCategoria(catPersonal, palabrasPersonal))
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
        title = {
            Text(
                if (tarea == null) stringResource(R.string.dialog_new_task) else stringResource(
                    R.string.dialog_edit_task
                ), color = TextoGrisOscuro, fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text(stringResource(R.string.dialog_task_title_label)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    textStyle = TextStyle(color = TextoGrisOscuro, fontSize = 16.sp)
                )

                Column {
                    Text(
                        stringResource(R.string.dialog_suggested_category),
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
                                onClick = { categoriaSelected = cat.name })
                        }
                    }
                }

                val locale = LocalConfiguration.current.locales[0]
                val dateFormatted = remember(selectedDate, locale) {
                    val formatter =
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
                    selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
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
                        Text(text = dateFormatted, color = TextoGrisOscuro)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimeSelectionCard(
                        label = stringResource(R.string.dialog_start_label),
                        time = selectedStartTime,
                        modifier = Modifier.weight(1f),
                        onClick = { showStartTimePicker = true })
                    TimeSelectionCard(
                        label = stringResource(R.string.dialog_end_label),
                        time = selectedEndTime,
                        modifier = Modifier.weight(1f),
                        onClick = { showEndTimePicker = true })
                }

                Column {
                    Text(
                        text = "Repetición",
                        style = MaterialTheme.typography.labelSmall,
                        color = VerdeSalvia,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Repetir esta tarea",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoGrisOscuro
                        )
                        Switch(
                            checked = esRepetible,
                            onCheckedChange = { esRepetible = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VerdeSalvia,
                                checkedTrackColor = VerdeSalvia.copy(alpha = 0.3f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = TextoGrisOscuro.copy(alpha = 0.2f),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = esRepetible,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryChip(
                                    item = CategoryItem("Todos los días", VerdeSalvia, ""),
                                    isSelected = tipoRepeticionSelected == TipoRepeticion.TODOS_LOS_DIAS,
                                    onClick = {
                                        tipoRepeticionSelected = TipoRepeticion.TODOS_LOS_DIAS
                                    })
                                CategoryChip(
                                    item = CategoryItem(
                                        "Días específicos",
                                        VerdeSalvia,
                                        ""
                                    ),
                                    isSelected = tipoRepeticionSelected == TipoRepeticion.DIAS_ESPECIFICOS,
                                    onClick = {
                                        tipoRepeticionSelected = TipoRepeticion.DIAS_ESPECIFICOS
                                    })
                            }
                            if (tipoRepeticionSelected == TipoRepeticion.DIAS_ESPECIFICOS) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val days = listOf(
                                        DayOfWeek.MONDAY,
                                        DayOfWeek.TUESDAY,
                                        DayOfWeek.WEDNESDAY,
                                        DayOfWeek.THURSDAY,
                                        DayOfWeek.FRIDAY,
                                        DayOfWeek.SATURDAY,
                                        DayOfWeek.SUNDAY
                                    )
                                    items(days) { day ->
                                        val isSelected = day in diasSeleccionados
                                        Surface(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clickable {
                                                    diasSeleccionados =
                                                        if (isSelected) diasSeleccionados - day else diasSeleccionados + day
                                                },
                                            shape = CircleShape,
                                            color = if (isSelected) VerdeSalvia else VerdeSalvia.copy(
                                                alpha = 0.1f
                                            )
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = day.getDisplayName(
                                                        JavaTextStyle.NARROW,
                                                        locale
                                                    ),
                                                    color = if (isSelected) Color.White else TextoGrisOscuro,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            val endRepeatDateFormatted = remember(fechaFinRepeticion, locale) {
                                fechaFinRepeticion?.format(
                                    DateTimeFormatter.ofLocalizedDate(
                                        FormatStyle.MEDIUM
                                    ).withLocale(locale)
                                ) ?: "Sin fecha límite"
                            }
                            OutlinedCard(
                                onClick = { showEndRepeatDatePicker = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = Color.White.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.CalendarMonth,
                                        null,
                                        tint = VerdeSalvia,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Repetir hasta",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextoGrisOscuro.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = endRepeatDateFormatted,
                                            color = TextoGrisOscuro,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedCat =
                        baseCategorias.find { it.name == categoriaSelected } ?: CategoryItem(
                            categoriaSelected,
                            VerdeSalvia,
                            "#81B29A"
                        )
                    if (titulo.isNotBlank()) {
                        val duracion =
                            ChronoUnit.MINUTES.between(selectedStartTime, selectedEndTime).toInt()
                        onConfirm(
                            Tarea(
                                id = tarea?.id ?: 0,
                                titulo = titulo,
                                descripcion = tarea?.descripcion ?: "",
                                fechaHoraInicio = LocalDateTime.of(selectedDate, selectedStartTime),
                                fechaHoraFin = if (esRepetible) LocalDateTime.of(
                                    selectedDate,
                                    selectedStartTime
                                ).plusMinutes(duracion.toLong()) else LocalDateTime.of(
                                    selectedDate,
                                    selectedEndTime
                                ),
                                categoria = selectedCat.name,
                                colorHex = selectedCat.hex,
                                estaCompletada = tarea?.estaCompletada ?: false,
                                esPlantilla = esRepetible,
                                tipoRepeticion = if (esRepetible) tipoRepeticionSelected else TipoRepeticion.NINGUNA,
                                diasRepeticion = if (esRepetible && tipoRepeticionSelected == TipoRepeticion.DIAS_ESPECIFICOS) diasSeleccionados else emptyList(),
                                horaInicio = selectedStartTime,
                                duracionMinutos = if (duracion > 0) duracion else 60,
                                fechaInicioRepeticion = selectedDate,
                                fechaFinRepeticion = if (esRepetible) (fechaFinRepeticion
                                    ?: selectedDate.plusYears(1)) else null
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(R.string.action_save),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.action_cancel),
                    color = TerracotaSuave
                )
            }
        }
    )

    if (showDatePicker) {
        AdaDatePickerWheelDialog(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { selectedDate = it; showDatePicker = false })
    }
    if (showStartTimePicker) {
        AdaTimeWheelPickerDialog(
            title = stringResource(R.string.dialog_start_label),
            initialTime = selectedStartTime,
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                selectedStartTime = it; if (selectedEndTime.isBefore(it)) selectedEndTime =
                it.plusHours(1); showStartTimePicker = false
            })
    }
    if (showEndTimePicker) {
        AdaTimeWheelPickerDialog(
            title = stringResource(R.string.dialog_end_label),
            initialTime = selectedEndTime,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { selectedEndTime = it; showEndTimePicker = false })
    }
    if (showEndRepeatDatePicker) {
        AdaDatePickerWheelDialog(
            initialDate = fechaFinRepeticion ?: selectedDate.plusDays(1),
            onDismiss = { showEndRepeatDatePicker = false },
            onConfirm = {
                if (it.isAfter(selectedDate)) {
                    fechaFinRepeticion = it
                }; showEndRepeatDatePicker = false
            })
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
    val months = listOf(
        stringResource(R.string.month_jan),
        stringResource(R.string.month_feb),
        stringResource(R.string.month_mar),
        stringResource(R.string.month_apr),
        stringResource(R.string.month_may),
        stringResource(R.string.month_jun),
        stringResource(R.string.month_jul),
        stringResource(R.string.month_aug),
        stringResource(R.string.month_sep),
        stringResource(R.string.month_oct),
        stringResource(R.string.month_nov),
        stringResource(R.string.month_dec)
    )
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
                    stringResource(R.string.dialog_select_date),
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
                        initialIndex = selectedDay - 1,
                        onValueChange = { selectedDay = it + 1 },
                        modifier = Modifier.weight(1f)
                    )
                    WheelColumn(
                        items = months,
                        initialIndex = selectedMonth - 1,
                        onValueChange = { selectedMonth = it + 1 },
                        modifier = Modifier.weight(1.2f)
                    )
                    WheelColumn(
                        items = years,
                        initialIndex = years.indexOf(selectedYear.toString()),
                        onValueChange = { selectedYear = years[it].toInt() },
                        modifier = Modifier.weight(1.5f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(R.string.action_cancel),
                            color = TerracotaSuave
                        )
                    }
                    TextButton(onClick = {
                        try {
                            val finalDate = LocalDate.of(selectedYear, selectedMonth, 1);
                            val maxDay = finalDate.lengthOfMonth(); onConfirm(
                                LocalDate.of(
                                    selectedYear,
                                    selectedMonth,
                                    selectedDay.coerceIn(1, maxDay)
                                )
                            )
                        } catch (e: Exception) {
                            onConfirm(LocalDate.now())
                        }
                    }) {
                        Text(
                            stringResource(R.string.action_confirm),
                            color = VerdeSalvia,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                        initialIndex = selectedHour,
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
                        initialIndex = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(R.string.action_cancel),
                            color = TerracotaSuave
                        )
                    }
                    TextButton(onClick = {
                        onConfirm(
                            LocalTime.of(
                                selectedHour,
                                selectedMinute
                            )
                        )
                    }) {
                        Text(
                            stringResource(R.string.action_confirm),
                            color = VerdeSalvia,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelColumn(
    items: List<String>,
    initialIndex: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val selectedIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LaunchedEffect(selectedIndex) { onValueChange(selectedIndex) }
    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(VerdeSalvia.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
        )
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 60.dp)
        ) {
            items(items.size) { index ->
                val isSelected = index == selectedIndex
                Text(
                    text = items[index],
                    style = if (isSelected) MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) VerdeSalvia else TextoGrisOscuro.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 8.dp)
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
fun AddBienestarDialog(onDismiss: () -> Unit, onConfirm: (String, LocalTime?) -> Unit) {
    var nombreRitual by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                stringResource(R.string.dialog_new_ritual),
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    stringResource(R.string.dialog_ritual_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoGrisOscuro.copy(alpha = 0.7f)
                ); OutlinedTextField(
                value = nombreRitual,
                onValueChange = { nombreRitual = it },
                label = { Text(stringResource(R.string.dialog_ritual_name_label)) },
                placeholder = { Text("Ej: Yoga matutino") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextoGrisOscuro,
                    unfocusedTextColor = TextoGrisOscuro,
                    focusedLabelColor = VerdeSalvia,
                    cursorColor = VerdeSalvia
                ),
                textStyle = TextStyle(color = TextoGrisOscuro, fontSize = 16.sp)
            ); OutlinedCard(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.AccessTime,
                        null,
                        tint = VerdeSalvia
                    ); Spacer(modifier = Modifier.width(12.dp)); Text(
                    text = if (selectedTime == null) stringResource(
                        R.string.dialog_no_time
                    ) else stringResource(
                        R.string.dialog_scheduled_at,
                        selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                    ), color = TextoGrisOscuro
                )
                }
            }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreRitual.isNotBlank()) onConfirm(
                        nombreRitual,
                        selectedTime
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp),
                enabled = nombreRitual.isNotBlank()
            ) {
                Text(
                    stringResource(R.string.action_create),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.action_cancel),
                    color = TerracotaSuave
                )
            }
        })
    if (showTimePicker) {
        AdaTimeWheelPickerDialog(
            title = stringResource(R.string.dialog_select_hour),
            initialTime = selectedTime ?: LocalTime.of(8, 0),
            onDismiss = { showTimePicker = false },
            onConfirm = { selectedTime = it; showTimePicker = false })
    }
}

@Composable
fun AddNotaDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseCrema,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                stringResource(R.string.dialog_new_note),
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text(stringResource(R.string.dialog_note_title)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextoGrisOscuro,
                        unfocusedTextColor = TextoGrisOscuro,
                        cursorColor = VerdeSalvia
                    ),
                    textStyle = TextStyle(color = TextoGrisOscuro)
                ); OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text(stringResource(R.string.dialog_note_content)) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextoGrisOscuro,
                    unfocusedTextColor = TextoGrisOscuro,
                    cursorColor = VerdeSalvia
                ),
                textStyle = TextStyle(color = TextoGrisOscuro)
            )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) onConfirm(
                        titulo,
                        contenido
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(R.string.action_save),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.action_cancel),
                    color = TerracotaSuave
                )
            }
        })
}
