package com.saico.ada.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.ada.dashboard.model.BottomAppBarItems
import com.saico.ada.ui.R
import com.saico.ada.ui.theme.TerracotaSuave
import com.saico.ada.ui.theme.TextoGrisOscuro

@Composable
fun NavigationBar(
    selectedBottomAppBarItem: BottomAppBarItems,
    onItemSelected: (BottomAppBarItems) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(32.dp)),
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AdaNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.HOME,
                    onClick = { onItemSelected(BottomAppBarItems.HOME) },
                    icon = Icons.Rounded.Home,
                    label = stringResource(R.string.nav_home)
                )
                AdaNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.AGENDA,
                    onClick = { onItemSelected(BottomAppBarItems.AGENDA) },
                    icon = Icons.Rounded.DateRange,
                    label = stringResource(R.string.nav_agenda)
                )
                AdaNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.WELLNES,
                    onClick = { onItemSelected(BottomAppBarItems.WELLNES) },
                    icon = Icons.Rounded.Favorite,
                    label = stringResource(R.string.nav_wellness)
                )
                AdaNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.NOTES,
                    onClick = { onItemSelected(BottomAppBarItems.NOTES) },
                    icon = Icons.Rounded.Description,
                    label = stringResource(R.string.nav_notes)
                )
            }
        }
    }
}

@Composable
fun RowScope.AdaNavItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val activeColor = TerracotaSuave
    val inactiveColor = TextoGrisOscuro.copy(alpha = 0.4f)
    val color = if (isSelected) activeColor else inactiveColor

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(activeColor)
            )
        }
    }
}
