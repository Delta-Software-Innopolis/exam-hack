package com.examhacker.common.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.examhacker.resources.R
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun TopDialogBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = Dimensions.DialogTitleFontSize,
            fontWeight = FontWeight.Bold,
            color = ColorPreset.Black
        )

        IconButton(
            onClick = onCloseClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "",
                tint = ColorPreset.Secondary
            )
        }
    }
}