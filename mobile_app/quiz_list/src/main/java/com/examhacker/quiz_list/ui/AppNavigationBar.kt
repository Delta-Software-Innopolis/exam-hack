package com.examhacker.quiz_list.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.examhacker.resources.ColorPreset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppNavigationBar(
    selectedIndex: Int,
    onQuizClick: () -> Unit = {},
    onQuizHubClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorPreset.BackgroundDefaultPrimary)
            .padding(vertical = 12.dp)
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavigationItem(
            text = "Quizzes",
            icon = Icons.Outlined.List,
            selected = selectedIndex == 0,
            onClick = onQuizClick
        )

        NavigationItem(
            text = "QuizHub",
            icon = Icons.Outlined.Home,
            selected = selectedIndex == 1,
            onClick = onQuizHubClick
        )

        NavigationItem(
            text = "Profile",
            icon = Icons.Outlined.Person,
            selected = selectedIndex == 2,
            onClick = onProfileClick
        )

        NavigationItem(
            text = "Settings",
            icon = Icons.Outlined.Settings,
            selected = selectedIndex == 3,
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun NavigationItem(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint =
                if (selected)
                    ColorPreset.IconPositiveTertiary
                else
                    ColorPreset.TextDefaultSecondary
        )

        Text(
            text = text,
            color =
                if (selected)
                    ColorPreset.IconPositiveTertiary
                else
                    ColorPreset.TextDefaultSecondary,
            fontWeight =
                if (selected)
                    FontWeight.Bold
                else
                    FontWeight.Normal
        )
    }
}