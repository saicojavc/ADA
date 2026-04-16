package com.saico.ada.widget

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.saico.ada.MainActivity
import com.saico.ada.ui.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object AdaTheme {
    @SuppressLint("RestrictedApi")
    val Fondo = ColorProvider(Color(0xFFFAF8F5))

    @SuppressLint("RestrictedApi")
    val TextoPrincipal = ColorProvider(Color(0xFF3D405B))

    @SuppressLint("RestrictedApi")
    val TextoSecundario = ColorProvider(Color(0x993D405B))

    @SuppressLint("RestrictedApi")
    val Verde = ColorProvider(Color(0xFF81B29A))

    @SuppressLint("RestrictedApi")
    val Terracota = ColorProvider(Color(0xFFE07A5F))

    @SuppressLint("RestrictedApi")
    val Blanco = ColorProvider(Color(0xFFFFFFFF))

    @SuppressLint("RestrictedApi")
    val GrisSuave = ColorProvider(Color(0xFFF0EFEA))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdaTasksWidgetContent(tareas: List<WidgetTarea>) {
    val today = LocalDate.now()
    val locale = Locale("es")
    val dayName = today.format(DateTimeFormatter.ofPattern("EEEE", locale))
        .replaceFirstChar { it.uppercase() }
    val dateLabel = today.format(DateTimeFormatter.ofPattern("d 'de' MMMM", locale))

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(AdaTheme.Fondo)
            .padding(16.dp)
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionStartActivity<MainActivity>())
        ) {
            // --- HEADER ---
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = dayName,
                        style = TextStyle(
                            color = AdaTheme.TextoPrincipal,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = dateLabel.uppercase(),
                        style = TextStyle(
                            color = AdaTheme.TextoSecundario,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Indicador de cantidad de tareas pendientes
                val pendientes = tareas.count { !it.estaCompletada }
                if (pendientes > 0) {
                    Text(
                        text = stringResource(R.string.widget_pending_tasks, pendientes),
                        style = TextStyle(
                            color = AdaTheme.Terracota,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // --- CONTENIDO ---
            if (tareas.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✦",
                            style = TextStyle(color = AdaTheme.Verde, fontSize = 24.sp)
                        )
                        Text(
                            text = stringResource(R.string.widget_all_clear),
                            style = TextStyle(color = AdaTheme.TextoSecundario, fontSize = 14.sp)
                        )
                    }
                }
            } else {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    items(tareas) { tarea ->
                        WidgetTareaCard(tarea = tarea)
                    }
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun WidgetTareaCard(tarea: WidgetTarea) {
    val completada = tarea.estaCompletada

    val taskColor = try {
        ColorProvider(Color(android.graphics.Color.parseColor(tarea.colorHex)))
    } catch (e: Exception) {
        AdaTheme.Verde
    }

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(AdaTheme.Blanco)
            .cornerRadius(12.dp)
    ) {
        Row(
            modifier = GlanceModifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo de estado en lugar de barra lateral (más orgánico)
            Box(
                modifier = GlanceModifier
                    .size(12.dp)
                    .background(if (completada) AdaTheme.GrisSuave else taskColor)
                    .cornerRadius(6.dp)
            ) {}

            Spacer(modifier = GlanceModifier.width(12.dp))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = tarea.titulo,
                    style = TextStyle(
                        color = if (completada) AdaTheme.TextoSecundario else AdaTheme.TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = if (completada) FontWeight.Normal else FontWeight.Medium,
                        textDecoration = if (completada) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tarea.hora,
                        style = TextStyle(
                            color = AdaTheme.TextoSecundario,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Check icon sutil
            if (completada) {
                Text(
                    text = "✓",
                    style = TextStyle(
                        color = AdaTheme.Verde,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
