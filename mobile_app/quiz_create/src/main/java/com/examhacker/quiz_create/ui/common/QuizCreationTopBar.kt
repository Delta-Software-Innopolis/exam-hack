package com.examhacker.quiz_create.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun QuizCreationTopBar(
    creationStage: CreationStage,
    onBackClick: () -> Unit,
    onForthClick: () -> Unit,
    isForthEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NavigationButton(
            onClick = onBackClick,
            isEnabled = true,
            painter = painterResource(R.drawable.ic_arrow_left),
            modifier = Modifier.padding(Dimensions.NavigationButtonPadding)
        )

        CreationProgressIndicator(creationStage)

        if (creationStage != CreationStage.REVIEW) {
            NavigationButton(
                onClick = onForthClick,
                isEnabled = isForthEnabled,
                painter = painterResource(R.drawable.ic_arrow_right),
                modifier = Modifier.padding(Dimensions.NavigationButtonPadding)
            )
        } else {
            Spacer(Modifier.width(Dimensions.QuizCreateNavigationButtonSize))
        }
    }
}

@Composable
private fun CreationProgressIndicator(stage: CreationStage) {
    val progressText = remember {
        when(stage) {
            CreationStage.NAME -> "1/3"
            CreationStage.AI_GENERATION -> "2/3"
            CreationStage.REVIEW -> "3/3"
        }
    }

    Text(
        text = "${stringResource(R.string.step)} $progressText",
        style = TextStyle(
            fontSize = Dimensions.ScreenTitleFontSize,
            fontWeight = FontWeight.Bold,
            color = ColorPreset.Secondary
        )
    )
}

@Composable
private fun NavigationButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = ColorPreset.SecondaryDimm,
            disabledContentColor = ColorPreset.BackgroundLightGrey,

            containerColor = ColorPreset.Transparent,
            disabledContainerColor = ColorPreset.Transparent
        ),
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = ""
        )
    }
}

enum class CreationStage {
    NAME,
    AI_GENERATION,
    REVIEW
}