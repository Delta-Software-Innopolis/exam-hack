package com.examhacker.quiz_create.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.examhacker.resources.ColorPreset

@Deprecated("Not used in new design")
@Composable
fun QuizCreationProgressBar(currentStep: Int) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {

        Surface(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterStart),
            shape = RoundedCornerShape(16.dp),
            color = ColorPreset.BackgroundDefaultPrimary,
            shadowElevation = 4.dp
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = ColorPreset.LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .width(208.dp)
                .height(48.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),
            color = ColorPreset.BackgroundDefaultPrimary,
            shadowElevation = 4.dp
        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ProgressDot(active = currentStep >= 1)
                Spacer(modifier = Modifier.width(8.dp))

                ProgressLine(active = currentStep >= 2)
                Spacer(modifier = Modifier.width(8.dp))

                ProgressDot(active = currentStep >= 2)
                Spacer(modifier = Modifier.width(8.dp))

                ProgressLine(active = currentStep >= 3)
                Spacer(modifier = Modifier.width(8.dp))

                ProgressDot(active = currentStep >= 3)
            }
        }
    }
}

@Composable
private fun ProgressDot(
    active: Boolean
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(
                if (active)
                    ColorPreset.Turquoise
                else
                    ColorPreset.LightGray
            )
    )
}

@Composable
private fun ProgressLine(
    active: Boolean
) {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (active)
                    ColorPreset.Turquoise
                else
                    ColorPreset.LightGray
            )
    )
}