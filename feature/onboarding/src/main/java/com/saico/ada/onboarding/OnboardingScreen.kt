package com.saico.ada.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.ada.ui.components.AdaGravityBackground
import com.saico.ada.ui.theme.*

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isMother by viewModel.isMother.collectAsStateWithLifecycle()
    val occupation by viewModel.occupation.collectAsStateWithLifecycle()
    val completed by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    LaunchedEffect(completed) {
        if (completed) {
            onOnboardingComplete()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo de partículas integrado
        AdaGravityBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenida a ADA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = TextoGrisOscuro
            )
            Text(
                text = "Tu LifeOS personal",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoGrisOscuro.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Superficie translúcida para los campos para mejorar legibilidad sobre partículas
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("¿Cómo te llamas?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeSalvia,
                            unfocusedBorderColor = TextoGrisOscuro.copy(alpha = 0.3f),
                            focusedLabelColor = VerdeSalvia,
                            focusedTextColor = TextoGrisOscuro,
                            unfocusedTextColor = TextoGrisOscuro
                        )
                    )

                    OutlinedTextField(
                        value = occupation,
                        onValueChange = viewModel::onOccupationChange,
                        label = { Text("¿A qué te dedicas?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VerdeSalvia,
                            unfocusedBorderColor = TextoGrisOscuro.copy(alpha = 0.3f),
                            focusedLabelColor = VerdeSalvia,
                            focusedTextColor = TextoGrisOscuro,
                            unfocusedTextColor = TextoGrisOscuro
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isMother,
                            onCheckedChange = viewModel::onMotherChange,
                            colors = CheckboxDefaults.colors(checkedColor = VerdeSalvia)
                        )
                        Text(
                            text = "Soy madre",
                            color = TextoGrisOscuro,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = viewModel::completeOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeSalvia),
                enabled = name.isNotBlank()
            ) {
                Text(
                    text = "Comenzar mi viaje",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
