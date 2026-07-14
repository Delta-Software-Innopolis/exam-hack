package com.examhacker.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.resources.R
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun NotImplementedSnackBarUI(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding, Alignment.CenterHorizontally),
        modifier = modifier
            .background(
                shape = RoundedCornerShape(Dimensions.DefaultCardRadius),
                color = ColorPreset.BackgroundWarningSecondary
            )
            .padding(Dimensions.ScreenPadding)
    ) {
        Icon(
           painter = painterResource(R.drawable.ic_info),
           contentDescription = "",
           tint = ColorPreset.BackgroundVariant,
           modifier = Modifier.size(Dimensions.MediumIconSize)
        )

        Text(
            text = stringResource(R.string.not_implemented_text),
            fontSize = Dimensions.SnackBarFontSize,
            fontWeight = FontWeight.Bold,
            color = ColorPreset.BackgroundVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotImplementedSnackBarPreview() {
    NotImplementedSnackBarUI(Modifier.fillMaxWidth())
}