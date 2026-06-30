package com.examhacker.quiz_list.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.examhacker.resources.ColorPreset
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.examhacker.resources.R

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
            .border(
                width = 1.dp,
                color = ColorPreset.BorderDefault
            )
            .padding(vertical = 8.dp)
            .height(76.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavigationItem(
            text = "Quizzes",
            icon = R.drawable.book_open,
            selected = selectedIndex == 0,
            onClick = onQuizClick
        )

        NavigationItem(
            text = "QuizHub",
            icon = R.drawable.search_magnifying_glass,
            selected = selectedIndex == 1,
            onClick = onQuizHubClick
        )

        NavigationItem(
            text = "Profile",
            icon = R.drawable.user_02,
            selected = selectedIndex == 2,
            onClick = onProfileClick
        )

        NavigationItem(
            text = "Settings",
            icon = R.drawable.settings_future,
            selected = selectedIndex == 3,
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun NavigationItem(
    text: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint =
                if (selected)
                    ColorPreset.IconPositiveTertiary
                else
                    ColorPreset.TextDefaultSecondary
        )

        Text(
            text = text,
            fontSize = 12.sp,
            color =
                if (selected)
                    ColorPreset.IconPositiveTertiary
                else
                    ColorPreset.TextDefaultSecondary,
            fontWeight =
                if (selected) FontWeight.Medium
                else FontWeight.Normal
        )
    }
}