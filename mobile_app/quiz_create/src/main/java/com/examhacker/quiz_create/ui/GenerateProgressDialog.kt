package com.examhacker.quiz_create.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.examhacker.resources.R
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
internal fun GenerateProgressDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        Card(
            shape = RoundedCornerShape(Dimensions.GenerateDialogRadius),
            colors = CardDefaults.cardColors(
                containerColor = ColorPreset.BackgroundVariant,
                disabledContainerColor = ColorPreset.BackgroundVariant,
                contentColor = ColorPreset.Black,
                disabledContentColor = ColorPreset.Black
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
                modifier = Modifier.padding(
                    vertical = Dimensions.ScreenPadding,
                    horizontal = Dimensions.GenerateDialogHorizontalPadding
                )
            ) {
                Image(
                    painter = painterResource(R.drawable.generation_progress_indicator),
                    contentDescription = "",
                    modifier = Modifier.size(64.dp)
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.generation_progress_dialog_title),
                        fontSize = Dimensions.SubTitleFontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.generation_progress_dialog_subtitle_1),
                        fontSize = Dimensions.ProgressBarFontSize,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenerateProgressDialogPreview() {
    GenerateProgressDialog()
}