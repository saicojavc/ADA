package com.saico.ada.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.ada.ui.theme.TextoGrisOscuro
import com.saico.ada.ui.theme.VerdeSalvia
import com.saico.ada.ui.theme.VerdeSalviaClaro

@Composable
fun AdaSuggestionCard(mensaje: String, accion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        // Subimos la opacidad para que la tarjeta tenga "cuerpo"
        colors = CardDefaults.cardColors(
            containerColor = VerdeSalviaClaro.copy(alpha = 0.1f)
        ),
        // Un borde muy fino del mismo color pero más oscuro ayuda a definir la forma
        border = BorderStroke(1.dp, VerdeSalvia.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = VerdeSalvia, // El verde oscuro del icono ayuda a guiar la vista
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = mensaje,
                    // Forzamos el color gris oscuro para legibilidad máxima
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextoGrisOscuro
                )
                Text(
                    text = accion,
                    style = MaterialTheme.typography.bodyMedium,
                    // Un gris un poco más suave para la acción, pero aún legible
                    color = TextoGrisOscuro.copy(alpha = 0.8f)
                )
            }
        }
    }
}