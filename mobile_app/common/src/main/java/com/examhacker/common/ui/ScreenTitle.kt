package com.examhacker.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start
) {
    val titleStyle = TextStyle(
        color = ColorPreset.Black,
        fontSize = Dimensions.ScreenTitleFontSize,
        fontWeight = FontWeight.Bold
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Text(
            text = text,
            style = titleStyle
        )
    }
}