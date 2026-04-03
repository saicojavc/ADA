package com.saico.ada.dashboard.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.dashboard.DashboardViewModel
import com.saico.ada.dashboard.state.DashboardState
import com.saico.ada.model.Bienestar
import com.saico.ada.ui.theme.*
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WellnessScreen(
    uiState: DashboardState,
    viewModel: DashboardViewModel
) {
    val successState = uiState as? DashboardState.Success
    val registros = successState?.registrosBienestar ?: emptyList()
    val ritualesHoy = successState?.ritualesHoy ?: emptyList()

    // Cálculo de equilibrio dinámico basado en datos reales (Carga vs Recuperación)
    val balanceScore = if (successState != null) {
        val completedTasks = successState.tareasHoy.count { it.estaCompletada }.toFloat()
        val totalTasks = successState.tareasHoy.size.coerceAtLeast(1).toFloat()
        
        // Rituales completados hoy
        val completedRituals = ritualesHoy.count { it.valorActual >= it.metaObjetivo }.toFloat()
        val totalRituals = ritualesHoy.size.coerceAtLeast(1).toFloat()
        
        val loadScore = completedTasks / totalTasks
        val recoveryScore = (completedRituals / totalRituals).coerceIn(0f, 1f)
        
        ((loadScore + recoveryScore) / 2f * 100).toInt().coerceIn(0, 100)
    } else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            WellnessHeaderOrganico(balanceScore)
        }

        item {
            RitualsSection(ritualesHoy, viewModel)
        }

        item {
            SleepWaveSection(registros)
        }

        item {
            StepsSection(registros)
        }
    }
}

@Composable
fun WellnessHeaderOrganico(score: Int) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val pulse by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.03f,
            animationSpec = infiniteRepeatable(tween(3000, easing = LinearOutSlowInEasing), RepeatMode.Reverse)
        )

        // Fondo sólido para legibilidad perfecta sobre partículas
        Surface(
            modifier = Modifier
                .size(210.dp * pulse)
                .shadow(12.dp, CircleShape, spotColor = VerdeSalvia),
            shape = CircleShape,
            color = BlancoPuro,
            tonalElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    color = VerdeSalvia.copy(alpha = 0.05f),
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                
                val gradient = Brush.sweepGradient(listOf(VerdeSalvia, AmbarNeutro, TerracotaSuave, VerdeSalvia))
                Canvas(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                    drawArc(
                        brush = gradient,
                        startAngle = -90f,
                        sweepAngle = (score / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$score%", 
                        style = MaterialTheme.typography.displayMedium, 
                        fontWeight = FontWeight.Black, 
                        color = TextoGrisOscuro
                    )
                    Text(
                        "EQUILIBRIO", 
                        style = MaterialTheme.typography.labelLarge, 
                        letterSpacing = 3.sp, 
                        color = TextoGrisOscuro.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RitualsSection(ritualesHoy: List<Bienestar>, viewModel: DashboardViewModel) {
    // Definición de iconos y colores sugeridos para el mapeo visual
    val infoBase = mapOf(
        "Baño de Luz" to RitualInfo(Icons.Rounded.LightMode, VerdeSalvia),
        "Brain Dump" to RitualInfo(Icons.Rounded.Psychology, AmbarNeutro),
        "Estiramiento" to RitualInfo(Icons.Rounded.SelfImprovement, TerracotaSuave),
        "Lectura Cuentos" to RitualInfo(Icons.Rounded.ChildCare, Color(0xFFB39DDB))
    )

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Text(
            "Tus Rituales", 
            style = MaterialTheme.typography.titleLarge, 
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp), 
            fontFamily = FontFamily.Serif, 
            color = TextoGrisOscuro,
            fontWeight = FontWeight.Bold
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(ritualesHoy) { ritual ->
                val info = infoBase[ritual.tipo] ?: RitualInfo(Icons.Rounded.Star, VerdeSalvia)
                RitualCircle(ritual, info) {
                    viewModel.toggleRitual(ritual)
                }
            }
        }
    }
}

data class RitualInfo(val icon: ImageVector, val color: Color)

@Composable
fun RitualCircle(ritual: Bienestar, info: RitualInfo, onToggle: () -> Unit) {
    val isCompleted = ritual.valorActual >= ritual.metaObjetivo
    var pressProgress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(if (isCompleted) 1f else pressProgress)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .background(if (isCompleted) info.color else BlancoPuro.copy(alpha = 0.8f))
                .pointerInput(isCompleted) {
                    detectTapGestures(
                        onLongPress = { onToggle() },
                        onPress = {
                            if (!isCompleted) {
                                pressProgress = 1f
                                tryAwaitRelease()
                                pressProgress = 0f
                            }
                        },
                        onTap = {
                            // Opción de marcar/desmarcar con toque simple también para comodidad
                            onToggle()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (!isCompleted && pressProgress > 0f) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    color = info.color,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 4.dp
                )
            }
            Icon(
                info.icon, 
                null, 
                tint = if (isCompleted) Color.White else info.color.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            ritual.tipo, 
            style = MaterialTheme.typography.labelSmall, 
            modifier = Modifier.padding(top = 8.dp), 
            color = TextoGrisOscuro,
            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun SleepWaveSection(registros: List<Bienestar>) {
    val sueno = registros.find { it.tipo == "Sueño" }?.valorActual ?: 0f
    val isGoodSleep = sueno >= 7f

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isGoodSleep) Color(0xFF2D3142) else TerracotaSuave
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.NightsStay, null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Descanso", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${sueno.toInt()}h", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Box(modifier = Modifier.fillMaxWidth().height(80.dp).padding(top = 16.dp)) {
                SleepWaveCanvas(isGoodSleep)
            }
        }
    }
}

@Composable
fun SleepWaveCanvas(isGood: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing))
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val midY = size.height / 2
        val amplitude = 15.dp.toPx()
        
        path.moveTo(0f, midY)
        for (x in 0..size.width.toInt() step 5) {
            val y = midY + amplitude * sin(x * 0.015f + phase)
            path.lineTo(x.toFloat(), y)
        }
        
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.4f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun StepsSection(registros: List<Bienestar>) {
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
            Column {
                Text("Movimiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextoGrisOscuro)
                Text("${pasos?.valorActual?.toInt() ?: 0} pasos hoy", style = MaterialTheme.typography.bodyMedium, color = TextoGrisOscuro.copy(alpha = 0.7f))
            }
        }
    }
}
