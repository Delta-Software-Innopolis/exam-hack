package com.examhacker.quiz_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import androidx.compose.ui.res.painterResource
import com.examhacker.resources.R
import androidx.compose.foundation.layout.height

@Composable
fun QuizCard(
    quizName: String,
    author: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ColorPreset.BackgroundDefaultSecondary)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = quizName,
                fontSize = Dimensions.ScreenTitleFontSize,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = author,
                color = ColorPreset.TextDefaultSecondary,
                fontSize = Dimensions.AuthLabelFontSize
            )
        }

        Icon(
            painter = painterResource(R.drawable.icon_button),
            contentDescription = null,
            tint = ColorPreset.TextDefaultSecondary,
            modifier = Modifier.size(34.dp)
        )
    }
}