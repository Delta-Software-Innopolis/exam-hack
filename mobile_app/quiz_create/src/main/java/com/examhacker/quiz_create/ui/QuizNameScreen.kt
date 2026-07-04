package com.examhacker.quiz_create.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.AppNavigationState
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.resources.ColorPreset
import com.examhacker.quiz_create.component.IQuizNameComponent
import com.examhacker.quiz_create.ui.common.CreationStage
import com.examhacker.quiz_create.ui.common.QuizCreationTopBar
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
internal fun QuizNameScreen(
    component: IQuizNameComponent
) {
    val model by component.model.subscribeAsState()

    QuizNameUI(
        model = model,
        onTitleChange = component::onNameChange,
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
        topBar = {
            QuizCreationTopBar(
                creationStage = CreationStage.NAME,
                onBackClick = {},
                onForthClick = {},
                isForthEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        bottomBar = {
            AppNavigationBar(
                selectedState = AppNavigationState.QUIZ_LIST,
                onQuizListClick = {},
                onQuizHubClick = {},
                onProfileClick = {},
                onSettingsClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        containerColor = ColorPreset.Background
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            ScreenTitle(
                text = stringResource(R.string.quiz_name_title),
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = model.name,
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
private fun InputWithSubmitButton(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField(
            value = name,
            onValueChange = onNameChange,
            placeholderText = stringResource(R.string.quiz_name_input_placeholder),
            isSingleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimensions.ScreenPadding))

        InputField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholderText = stringResource(R.string.quiz_description_input_placeholder),
            isSingleLine = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimensions.InputFieldButtonSpacing))


    }
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    isSingleLine: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.InputFieldRadius),
        singleLine = isSingleLine,
        placeholder = {
            Text(
                text = placeholderText,
                style = TextStyle(
                    fontSize = Dimensions.InputLabelFontSize,
                    fontWeight = FontWeight.Normal,
                    color = ColorPreset.Secondary
                )
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ColorPreset.BackgroundDefaultPrimary,
            unfocusedContainerColor = ColorPreset.BackgroundDefaultPrimary,

            focusedBorderColor = ColorPreset.Secondary,
            unfocusedBorderColor = ColorPreset.Secondary,

            focusedTextColor = ColorPreset.Black,
            unfocusedTextColor = ColorPreset.Black
        )
    )
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun QuizNameScreenPreview() {
    QuizNameUI(
        model = IQuizNameComponent.Model(),
        onTitleChange = {},
        onDescriptionChange = {},
        onNextClick = {}
    )
}