package com.saico.ada.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.ada.domain.use_case.SuggestionType
import com.saico.ada.ui.theme.*

@Composable
fun AdaSuggestionCard(
    mensaje: String,
    accion: String,
    tipo: SuggestionType = SuggestionType.ACTION
) {
    // 1. Color dinámico animado según la "emoción" o tipo de sugerencia de ADA
    val themeColor by animateColorAsState(
        targetValue = when (tipo) {
            SuggestionType.ACTION -> VerdeSalvia
            SuggestionType.REST -> TerracotaSuave
            SuggestionType.FOCUS -> Color(0xFF945FFB) // Púrpura Foco
            SuggestionType.WELLBEING -> VerdeSalvia
            SuggestionType.CELEBRATION -> AmbarNeutro
        },
        animationSpec = tween(durationMillis = 800),
        label = "themeColorTransition"
    )

    val icon = when (tipo) {
        SuggestionType.ACTION -> Icons.Rounded.Bolt
        SuggestionType.REST -> Icons.Rounded.Bedtime
        SuggestionType.FOCUS -> Icons.Rounded.Psychology
        SuggestionType.WELLBEING -> Icons.Rounded.SelfImprovement
        SuggestionType.CELEBRATION -> Icons.Rounded.AutoAwesome
    }

    // 2. Animación de pulso sutil para el "orbe" del icono
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        border = BorderStroke(1.2.dp, themeColor.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            themeColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // El "Orbe" de ADA: Reacciona visualmente
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp * pulseScale)
                            .clip(CircleShape)
                            .background(themeColor.copy(alpha = 0.15f))
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 18.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = TextoGrisOscuro
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = accion,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 18.sp
                        ),
                        color = TextoGrisOscuro.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
