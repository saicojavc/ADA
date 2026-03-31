package com.saico.ada.dashboard.state

import androidx.compose.ui.graphics.vector.ImageVector

data class DashboardEvent(
    val id: Int,
    val hora: String,
    val titulo: String,
    val categoria: String,
    val color: androidx.compose.ui.graphics.Color,
    val icon: ImageVector
)