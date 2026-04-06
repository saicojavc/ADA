package com.saico.ada.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.saico.ada.ui.theme.Typography
import java.time.format.TextStyle

// Colores de ADA
val BaseCrema = Color(0xFFFAF8F5)
val TextoGrisOscuro = Color(0xFF3D405B)
val TerracotaSuave = Color(0xFFE07A5F)
val AmbarNeutro = Color(0xFFF2CC8F)
val VerdeSalvia = Color(0xFF81B29A)
val VerdeSalviaClaro = Color(0xFFF0F4F2) // Para fondos de tarjetas de sugerencia
val BlancoPuro = Color(0xFFFFFFFF)

// Tipografía (Asegúrate de tener fuentes Serif en tu proyecto)
val AdaTypography = Typography(
    displayMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Serif, // O una fuente específica como Playfair Display
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = TextoGrisOscuro
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        color = TextoGrisOscuro
    )
)
// ADA Warm Palette
val Cream = Color(0xFFFAF8F5)
val Terracotta = Color(0xFFE07A5F)
val SageGreen = Color(0xFF81B29A)
val DeepCharcoal = Color(0xFF2D3142)
val MellowYellow = Color(0xFFF2CC8F)

// Light Theme
val PrimaryLight = Terracotta
val OnPrimaryLight = Color.White
val PrimaryContainerLight = Color(0xFFFBE9E7)
val OnPrimaryContainerLight = Color(0xFF3E0E00)

val SecondaryLight = SageGreen
val OnSecondaryLight = Color.White
val SecondaryContainerLight = Color(0xFFE8F5E9)
val OnSecondaryContainerLight = Color(0xFF00210B)

val BackgroundLight = Cream
val OnBackgroundLight = DeepCharcoal
val SurfaceLight = Cream
val OnSurfaceLight = DeepCharcoal

// Dark Theme (Adjusted for visibility while keeping the vibe)
val PrimaryDark = Color(0xFFFFB4A2)
val OnPrimaryDark = Color(0xFF561E0F)
val SecondaryDark = Color(0xFFA5D6A7)
val OnSecondaryDark = Color(0xFF003914)

val BackgroundDark = Color(0xFF2B2D42)
val OnBackgroundDark = Color(0xFFEDF2F4)
val SurfaceDark = Color(0xFF2B2D42)
val OnSurfaceDark = Color(0xFFEDF2F4)
