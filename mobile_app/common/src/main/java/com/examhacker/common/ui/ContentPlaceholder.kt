package com.examhacker.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun ContentPlaceholder(
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (isLoading) {
            CustomCircularProgressIndicator()
        } else {
            error?.let {
                Text(
                    text = it,
                    fontSize = Dimensions.ConnectionErrorFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorPreset.BackgroundVariant,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .background(
                            color = ColorPreset.ErrorPrimary,
                            shape = RoundedCornerShape(Dimensions.DefaultCardRadius)
                        )
                        .padding(Dimensions.ScreenPadding)
                )
            }
        }
    }
}