package com.saico.ada.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.BaseCrema
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.VerdeSalvia
import kotlin.random.Random
import androidx.compose.foundation.Canvas
// Este es el más importante para drawCircle:

@Composable
fun AdaGravityBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    var tiltX by remember { mutableStateOf(0f) }
    var tiltY by remember { mutableStateOf(0f) }

    // Colores cálidos de ADA con alta transparencia (0.1f - 0.2f)
    val particleColors = listOf(
        TerracotaSuave.copy(alpha = 0.15f),
        AmbarNeutro.copy(alpha = 0.2f),
        VerdeSalvia.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.3f)
    )

    val particles = remember {
        List(40) { // Menos partículas para un look más limpio
            ParticleState(
                pos = Offset(Random.nextFloat(), Random.nextFloat()),
                velocity = Offset(
                    (Random.nextFloat() - 0.5f) * 0.0001f,
                    (Random.nextFloat() - 0.5f) * 0.0001f
                ),
                size = Random.nextFloat() * 8f + 4f, // Partículas un poco más grandes y suaves
                color = particleColors.random()
            )
        }
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    // Impulso extremadamente sutil
                    tiltX = -it.values[0] * 0.000005f
                    tiltY = it.values[1] * 0.000005f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ada_particles")
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(20, easing = LinearEasing)),
        label = "frame"
    )

    // Gradiente de fondo acogedor (Crema a Blanco Hueso)
    val warmGradient = remember {
        Brush.verticalGradient(
            colors = listOf(BaseCrema, Color(0xFFFFFBF0), Color(0xFFFDF5E6))
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(warmGradient)
    ) {
        val f = frame // Trigger recomposición

        particles.forEach { p ->
            // FRICCIÓN MUY ALTA para movimiento "en miel" o aire denso
            val friction = 0.92f

            // VELOCIDAD TERMINAL BAJÍSIMA (Movimiento zen)
            val newVelX = (p.velocity.x * friction + tiltX).coerceIn(-0.0002f, 0.0002f)
            val newVelY = (p.velocity.y * friction + tiltY).coerceIn(-0.0002f, 0.0002f)

            p.velocity = Offset(newVelX, newVelY)

            // Actualizar posición con rebote suave (Wrap around)
            p.pos = Offset(
                x = (p.pos.x + p.velocity.x + 1f) % 1f,
                y = (p.pos.y + p.velocity.y + 1f) % 1f
            )

            // Dibujar con un ligero desenfoque visual (radio mayor)
            drawCircle(
                color = p.color,
                radius = p.size.dp.toPx(),
                center = Offset(p.pos.x * size.width, p.pos.y * size.height)
            )
        }
    }
}

// Clase necesaria para guardar el estado de cada mota de luz
class ParticleState(
    var pos: Offset,
    var velocity: Offset,
    val size: Float,
    val color: Color
)