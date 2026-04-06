package com.saico.ada.ui.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AdaIcons {
    val FitnessCenter: ImageVector = Icons.Default.FitnessCenter
    val UserProfile: ImageVector = Icons.Default.Person
    val Settings: ImageVector = Icons.Default.Settings
    val Add: ImageVector = Icons.Default.Add
    val Error: ImageVector = Icons.Default.Error
    val Visibility: ImageVector = Icons.Default.Visibility
    val VisibilityOff: ImageVector = Icons.Default.VisibilityOff
    val ArrowForward: ImageVector = Icons.Default.ArrowForward
    val Walk: ImageVector = Icons.Outlined.DirectionsRun


    val Fire: ImageVector = Icons.Default.Whatshot
    val Check: ImageVector = Icons.Outlined.CheckCircle
    val Cake: ImageVector = Icons.Default.Cake
    val Male: ImageVector = Icons.Default.Male
    val Female: ImageVector = Icons.Default.Female
    val Person: ImageVector = Icons.Default.Person
    val Weight: ImageVector = Icons.Default.FitnessCenter
    val Height: ImageVector = Icons.Default.Height
    val Home: ImageVector = Icons.Default.Home
    val History: ImageVector = Icons.Default.History
    val Clock: ImageVector = Icons.Default.Timer
    val Map: ImageVector = Icons.Default.Map
    val Speed: ImageVector = Icons.Default.Speed
    val Pause: ImageVector = Icons.Default.Pause
    val Stop: ImageVector = Icons.Default.Stop
    val Play: ImageVector = Icons.Default.PlayArrow
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val Save: ImageVector = Icons.Default.Save
    val ArrowUp: ImageVector = Icons.Default.ArrowDropUp
    val ArrowDown: ImageVector = Icons.Default.ArrowDropDown
    val Delete: ImageVector = Icons.Default.Delete
    val Edit: ImageVector = Icons.Default.Edit
    val KeyboardArrowUp: ImageVector = Icons.Default.KeyboardArrowUp
    val KeyboardArrowDown: ImageVector = Icons.Default.KeyboardArrowDown
    val Moon: ImageVector = Icons.Default.Nightlight
    val Sun: ImageVector = Icons.Default.WbSunny
    val Location : ImageVector = Icons.Default.LocationOn

    val Download : ImageVector = Icons.Default.Download

    val Link : ImageVector = Icons.Default.Link

    val Scale : ImageVector = Icons.Default.Scale

    val Star : ImageVector = Icons.Default.Star

    val Notifications : ImageVector = Icons.Default.Notifications

    val Language : ImageVector = Icons.Default.Language

    val Straighten : ImageVector = Icons.Default.Straighten


    val CalendarToday : ImageVector = Icons.Default.CalendarToday

    val ArrowDropDown : ImageVector = Icons.Default.ArrowDropDown

    val ChevronRight : ImageVector = Icons.Default.ArrowForward

    val SystemUpdate : ImageVector = Icons.Default.Update

    val DirectionsRun : ImageVector = Icons.AutoMirrored.Outlined.DirectionsRun

    val DirectionsBike : ImageVector = Icons.AutoMirrored.Filled.DirectionsBike

    val Pool : ImageVector = Icons.Default.Pool

    val Treadmill: ImageVector
        get() = ImageVector.Builder(
            name = "Treadmill",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {

            // ══════════════════════════════════════════
            //  PLATAFORMA / CINTA
            //  Rectángulo con esquinas redondeadas
            //  fill gris oscuro + stroke gris claro
            // ══════════════════════════════════════════
            path(
                fill = SolidColor(Color(0xFF3A3A3A)),
                stroke = SolidColor(Color(0xFF888888)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4f, 17f)
                lineTo(20f, 17f)
                curveTo(21.1f, 17f, 22f, 17.9f, 22f, 19f)
                curveTo(22f, 20.1f, 21.1f, 21f, 20f, 21f)
                lineTo(4f, 21f)
                curveTo(2.9f, 21f, 2f, 20.1f, 2f, 19f)
                curveTo(2f, 17.9f, 2.9f, 17f, 4f, 17f)
                close()
            }

            // Línea interior de la banda (cinta corriente)
            path(
                fill = null,
                stroke = SolidColor(Color(0xFF888888)),
                strokeLineWidth = 0.8f,
                strokeLineCap = StrokeCap.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(5f, 19f)
                lineTo(19f, 19f)
            }

            // ══════════════════════════════════════════
            //  POSTE VERTICAL — gris, lado derecho
            // ══════════════════════════════════════════
            path(
                fill = null,
                stroke = SolidColor(Color(0xFF888888)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(17f, 17f)
                lineTo(17f, 7f)
            }

            // ══════════════════════════════════════════
            //  BRAZO DIAGONAL — rojo
            //  Desde la parte alta del poste hacia izq-arriba
            // ══════════════════════════════════════════
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFE53935)),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15f, 9f)
                lineTo(19f, 5f)
            }

            // ══════════════════════════════════════════
            //  MANUBRIO HORIZONTAL — rojo
            //  Barra horizontal a media altura
            // ══════════════════════════════════════════
            path(
                fill = null,
                stroke = SolidColor(Color(0xFFE53935)),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 13f)
                lineTo(17f, 13f)
            }

        }.build()

    // Icono de Google personalizado con nombres de funciones corregidos
    val Google: ImageVector
        get() = ImageVector.Builder(
            name = "Google",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF4285F4)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(23.49f, 12.275f)
                curveTo(23.49f, 11.49f, 23.415f, 10.73f, 23.3f, 10f)
                horizontalLineTo(12f)
                verticalLineTo(14.252f)
                horizontalLineTo(18.438f)
                curveTo(18.157f, 15.713f, 17.282f, 16.955f, 16.045f, 17.781f)
                verticalLineTo(20.513f)
                horizontalLineTo(19.933f)
                curveTo(22.215f, 18.412f, 23.49f, 15.305f, 23.49f, 12.275f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF34A853)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(12f, 24f)
                curveTo(15.24f, 24f, 17.955f, 22.922f, 19.933f, 21.086f)
                lineTo(16.045f, 18.354f)
                curveTo(14.955f, 19.088f, 13.582f, 19.515f, 12f, 19.515f)
                curveTo(8.872f, 19.515f, 6.225f, 17.412f, 5.28f, 14.582f)
                horizontalLineTo(1.275f)
                verticalLineTo(17.682f)
                curveTo(3.255f, 21.645f, 7.305f, 24f, 12f, 24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFBBC05)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(5.28f, 14.582f)
                curveTo(5.032f, 13.855f, 4.89f, 13.077f, 4.89f, 12.273f)
                curveTo(4.89f, 11.468f, 5.032f, 10.691f, 5.28f, 9.964f)
                verticalLineTo(6.864f)
                horizontalLineTo(1.275f)
                curveTo(0.465f, 8.495f, 0f, 10.332f, 0f, 12.273f)
                curveTo(0f, 14.214f, 0.465f, 16.05f, 1.275f, 17.682f)
                lineTo(5.28f, 14.582f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEA4335)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(12f, 5.045f)
                curveTo(13.763f, 5.045f, 15.345f, 5.645f, 16.586f, 6.827f)
                lineTo(19.727f, 3.686f)
                curveTo(17.814f, 1.9f, 15.24f, 0.818f, 12f, 0.818f)
                curveTo(7.305f, 0.818f, 3.255f, 3.173f, 1.275f, 7.136f)
                lineTo(5.28f, 10.236f)
                curveTo(6.225f, 7.405f, 8.872f, 5.305f, 12f, 5.305f)
                close()
            }
        }.build()
}