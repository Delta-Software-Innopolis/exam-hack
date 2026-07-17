package com.examhacker.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun AnswerVariantCard(
    description: String,
    status: AnswerVariantStatus,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(Dimensions.DefaultCardRadius),
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused && enabled }
            .clickable(onClick = onClick, enabled = enabled),
        border =
            if (isFocused)
                BorderStroke(
                    width = Dimensions.ThickBorderWidth,
                    color = ColorPreset.SecondaryDimm
                )
            else
                null,
        colors = CardDefaults.cardColors(
            contentColor = ColorPreset.Black,
            containerColor =
                when(status) {
                    AnswerVariantStatus.DEFAULT          -> ColorPreset.BackgroundDefaultPrimary
                    AnswerVariantStatus.ANSWERED_CORRECT -> ColorPreset.BackgroundPositiveSecondary
                    AnswerVariantStatus.ANSWERED_WRONG   -> ColorPreset.BackgroundDangerSecondary
                }
        )
    ) {
        Text(
            text = description,
            style = TextStyle(
                fontSize = Dimensions.AnswerVariantFontSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            ),
            modifier = modifier
        )
    }
}

enum class AnswerVariantStatus {
    DEFAULT,
    ANSWERED_CORRECT,
    ANSWERED_WRONG
}