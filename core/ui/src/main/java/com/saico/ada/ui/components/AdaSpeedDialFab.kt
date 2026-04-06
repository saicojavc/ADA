package com.saico.ada.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.ada.ui.theme.AmbarNeutro
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia

@Composable
fun AdaSpeedDialFab(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onActionSelected: (FabAction) -> Unit
) {
    val actions = listOf(
        FabAction.Tarea,
        FabAction.Nota
    )

    val transition = updateTransition(targetState = isExpanded, label = "fab_transition")

    val rotation by transition.animateFloat(label = "fab_rotation") { expanded ->
        if (expanded) 45f else 0f
    }

    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(bottom = 8.dp, end = 0.dp) // Ajustado para el slot del Scaffold
        ) {
            actions.forEachIndexed { index, action ->
                val scale by transition.animateFloat(
                    label = "action_scale_${action.name}",
                    transitionSpec = {
                        tween(durationMillis = 150, delayMillis = index * 30, easing = LinearOutSlowInEasing)
                    }
                ) { expanded -> if (expanded) 1f else 0f }

                if (isExpanded) {
                    SpeedDialActionItem(
                        action = action,
                        scale = scale,
                        onActionSelected = {
                            onActionSelected(it)
                            onExpandedChange(false)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            FloatingActionButton(
                onClick = { onExpandedChange(!isExpanded) },
                shape = CircleShape,
                containerColor = TerracotaSuave,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = if (isExpanded) "Cerrar menú" else "Añadir nuevo",
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(rotation)
                )
            }
        }
    }
}

@Composable
fun SpeedDialActionItem(
    action: FabAction,
    scale: Float,
    onActionSelected: (FabAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale, alpha = scale)
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                color = TextoGrisOscuro,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        FloatingActionButton(
            onClick = { onActionSelected(action) },
            shape = CircleShape,
            containerColor = action.color,
            contentColor = if (action.color == AmbarNeutro) TextoGrisOscuro else Color.White,
            modifier = Modifier.size(48.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(imageVector = action.icon, contentDescription = action.label, modifier = Modifier.size(24.dp))
        }
    }
}

sealed class FabAction(val label: String, val icon: ImageVector, val color: Color, val name: String) {
    object Tarea : FabAction("Nueva Tarea", Icons.Rounded.CheckCircle, Color(0xFF945FFB), "task")
    object Nota : FabAction("Nota Rápida", Icons.Rounded.HistoryEdu, AmbarNeutro, "note")
}
