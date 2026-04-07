package com.saico.ada.dashboard.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.model.Bienestar
import com.saico.ada.ui.theme.*
import com.saico.ada.ui.R
import java.time.LocalTime
import kotlin.math.sin

enum class TimeOfDay { DAY, SUNSET, NIGHT }

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WellnessScreen(
    uiState: DashboardState,
    viewModel: DashboardViewModel
) {
    val successState = uiState as? DashboardState.Success
    val registros = successState?.registrosBienestar ?: emptyList()
    val context = LocalContext.current

    // Determinar el momento del día para la estética circadiana
    val currentTime = LocalTime.now()
    val timeOfDay = when {
        currentTime.isAfter(LocalTime.of(7, 0)) && currentTime.isBefore(LocalTime.of(18, 0)) -> TimeOfDay.DAY
        currentTime.isAfter(LocalTime.of(18, 0)) && currentTime.isBefore(LocalTime.of(20, 30)) -> TimeOfDay.SUNSET
        else -> TimeOfDay.NIGHT
    }

    val permissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        }
    )

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            val intent = Intent(context, Class.forName("com.saico.ada.service.StepCounterService"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    val balanceScore = if (successState != null) {
        val tareas = successState.tareasHoy
        val countCarga = tareas.count { it.categoria in listOf("Trabajo", "Hogar", "Maternidad") }
        val countBienestar = tareas.count { it.categoria == "Bienestar" }
        val totalTareas = (countCarga + countBienestar).coerceAtLeast(1)
        val ratioBienestar = countBienestar.toFloat() / totalTareas.toFloat()
        val score = (100 - (kotlin.math.abs(0.5f - ratioBienestar) * 200)).toInt()
        score.coerceIn(0, 100)
    } else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item { WellnessHeaderOrganico(balanceScore) }
        item { SleepWaveSection(registros, timeOfDay) }
        item {
            StepsSection(
                registros = registros,
                hasPermission = permissionState.status.isGranted,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
fun WellnessHeaderOrganico(score: Int) {
    val isUnbalanced = score < 50
    val statusColor by animateColorAsState(
        targetValue = if (isUnbalanced) TerracotaSuave else VerdeSalvia,
        animationSpec = tween(1000)
    )

    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp), contentAlignment = Alignment.Center) {
        val infiniteTransition = rememberInfiniteTransition()
        val pulse by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.03f,
            animationSpec = infiniteRepeatable(tween(3000, easing = LinearOutSlowInEasing), RepeatMode.Reverse)
        )

        Surface(
            modifier = Modifier.size(210.dp * pulse).shadow(12.dp, CircleShape, spotColor = statusColor),
            shape = CircleShape, color = BlancoPuro, tonalElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = 1f, modifier = Modifier.fillMaxSize().padding(12.dp),
                    color = statusColor.copy(alpha = if (isUnbalanced) 0.1f else 0.05f),
                    strokeWidth = 12.dp, strokeCap = StrokeCap.Round
                )
                val gradient = if (isUnbalanced) Brush.sweepGradient(listOf(TerracotaSuave, AmbarNeutro, TerracotaSuave))
                               else Brush.sweepGradient(listOf(VerdeSalvia, AmbarNeutro, VerdeSalvia))

                Canvas(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                    drawArc(brush = gradient, startAngle = -90f, sweepAngle = (score / 100f) * 360f, useCenter = false, style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$score%", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = if (isUnbalanced) TerracotaSuave else TextoGrisOscuro)
                    Text(
                        if (isUnbalanced) stringResource(R.string.dialog_unbalanced) else stringResource(R.string.wellness_balance), 
                        style = MaterialTheme.typography.labelLarge, 
                        letterSpacing = if (isUnbalanced) 1.sp else 3.sp, 
                        color = (if (isUnbalanced) TerracotaSuave else TextoGrisOscuro).copy(alpha = 0.7f), 
                        fontWeight = FontWeight.Bold
                    )
                    if (isUnbalanced) Icon(imageVector = Icons.Rounded.WarningAmber, contentDescription = null, tint = TerracotaSuave.copy(alpha = 0.5f), modifier = Modifier.size(20.dp).padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun SleepWaveSection(registros: List<Bienestar>, timeOfDay: TimeOfDay) {
    val sueno = registros.find { it.tipo == "Sueño" }?.valorActual ?: 0f
    
    val config = when(timeOfDay) {
        TimeOfDay.DAY -> Triple(AmbarNeutro, Icons.Rounded.LightMode, stringResource(R.string.wellness_day))
        TimeOfDay.SUNSET -> Triple(TerracotaSuave, Icons.Rounded.WbTwilight, stringResource(R.string.wellness_sunset))
        TimeOfDay.NIGHT -> Triple(Color(0xFF2D3142), Icons.Rounded.NightsStay, stringResource(R.string.wellness_night))
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = config.first)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(config.second, null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("${stringResource(R.string.wellness_rest)} (${config.third})", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${sueno.toInt()}h", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Box(modifier = Modifier.fillMaxWidth().height(80.dp).padding(top = 16.dp)) {
                SleepWaveCanvas(timeOfDay == TimeOfDay.NIGHT)
            }
            
            if (timeOfDay == TimeOfDay.NIGHT) {
                Text(
                    stringResource(R.string.wellness_sleep_analysis),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SleepWaveCanvas(isNight: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(if(isNight) 6000 else 3000, easing = LinearEasing))
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val midY = size.height / 2
        val amplitude = if(isNight) 10.dp.toPx() else 20.dp.toPx()
        
        path.moveTo(0f, midY)
        for (x in 0..size.width.toInt() step 5) {
            val y = midY + amplitude * sin(x * (if(isNight) 0.01f else 0.02f) + phase)
            path.lineTo(x.toFloat(), y)
        }
        
        drawPath(path = path, color = Color.White.copy(alpha = 0.4f), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun StepsSection(registros: List<Bienestar>, hasPermission: Boolean, onRequestPermission: () -> Unit) {
    val pasos = registros.find { it.tipo == "Pasos" }
    val progress = (pasos?.valorActual ?: 0f) / (pasos?.metaObjetivo ?: 10000f)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeSalviaClaro),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(65.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = 1f, color = VerdeSalvia.copy(alpha = 0.1f), strokeWidth = 7.dp)
                CircularProgressIndicator(progress = progress.coerceIn(0f, 1f), color = VerdeSalvia, strokeWidth = 7.dp)
                Icon(Icons.Rounded.DirectionsRun, null, tint = VerdeSalvia)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.wellness_movement), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextoGrisOscuro)
                if (hasPermission) {
                    Text(stringResource(R.string.wellness_steps_today, pasos?.valorActual?.toInt() ?: 0), style = MaterialTheme.typography.bodyMedium, color = TextoGrisOscuro.copy(alpha = 0.7f))
                } else {
                    Text(stringResource(R.string.wellness_activate_counter), style = MaterialTheme.typography.bodySmall, color = TerracotaSuave, modifier = Modifier.clickable { onRequestPermission() })
                }
            }
        }
    }
}
