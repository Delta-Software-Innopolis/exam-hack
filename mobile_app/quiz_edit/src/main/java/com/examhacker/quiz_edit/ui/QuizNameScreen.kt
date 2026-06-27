package com.examhacker.quiz_edit.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.quiz_edit.component.IQuizNameComponent

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.clip

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.examhacker.resources.ColorPreset

import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon

@Composable
fun QuizNameScreen(
    component: IQuizNameComponent
) {
    val model by component.model.subscribeAsState()

    QuizNameUI(
        model = model,
        onTitleChange = component::onTitleChange,
        onDescriptionChange = component::onDescriptionChange,
        onNextClick = component::onNextClick
    )
}

@Composable
private fun QuizNameUI(
    model: IQuizNameComponent.Model,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    Scaffold(
        containerColor = ColorPreset.Background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .statusBarsPadding()
        ) {

            QuizProgressHeader(1)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Name your new quiz",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Everything starts with a name, right?",
                color = ColorPreset.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = model.title,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(107.dp),
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        text = "Quiz title",
                        color = ColorPreset.Gray
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorPreset.Background,
                    unfocusedContainerColor = ColorPreset.Background,
                    focusedBorderColor = ColorPreset.LightGray,
                    unfocusedBorderColor = ColorPreset.LightGray,
                    focusedTextColor = ColorPreset.Black,
                    unfocusedTextColor = ColorPreset.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = model.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(107.dp),
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        text = "Quiz description",
                        color = ColorPreset.Gray
                    )
                },
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorPreset.Background,
                    unfocusedContainerColor = ColorPreset.Background,
                    focusedBorderColor = ColorPreset.LightGray,
                    unfocusedBorderColor = ColorPreset.LightGray,
                    focusedTextColor = ColorPreset.Black,
                    unfocusedTextColor = ColorPreset.Black
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = ColorPreset.Green
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ColorPreset.LightGreen,
                    contentColor = ColorPreset.Black
                ),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "Next",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
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
@Composable
fun QuizProgressHeader(
    currentStep: Int
) {

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

                ProgressDot(
                    active = currentStep >= 1
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                ProgressLine(
                    active = currentStep >= 2
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                ProgressDot(
                    active = currentStep >= 2
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                ProgressLine(
                    active = currentStep >= 3
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                ProgressDot(
                    active = currentStep >= 3
                )
            }
        }
    }
}

@Preview(
    widthDp = 393,
    heightDp = 836,
    showBackground = true
)
@Composable
private fun QuizNameScreenPreview() {

    QuizNameScreen(
        component = object : IQuizNameComponent {

            override val model: Value<IQuizNameComponent.Model> =
                MutableValue(
                    IQuizNameComponent.Model(
                        title = "",
                        description = ""
                    )
                )

            override fun onTitleChange(title: String) {}

            override fun onDescriptionChange(description: String) {}

            override fun onNextClick() {}
        }
    )
}