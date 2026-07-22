package com.examhacker.quiz_create.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.NavigationTab
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
        onNameChange = component::onNameChange,
        onDescriptionChange = component::onDescriptionChange,
        onNextClick = component::onNextClick,
        onQuizHubClick = component::goToQuizHub,
        onProfileClick = component::goToProfile,
        onSettingsClick = component::goToSettings,
        onBackClick = component::back
    )
}

@Composable
private fun QuizNameUI(
    model: IQuizNameComponent.Model,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onQuizHubClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            QuizCreationTopBar(
                creationStage = CreationStage.NAME,
                onBackClick = onBackClick,
                onForthClick = onNextClick,
                isForthEnabled = model.forthEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.QUIZ_LIST,
                onQuizListClick = {},
                onQuizHubClick = onQuizHubClick,
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        containerColor = ColorPreset.BackgroundVariant
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
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

            InputWithSubmitButton(
                name = model.name,
                description = model.description,
                onNameChange = onNameChange,
                onDescriptionChange = onDescriptionChange,
                onNextClick = onNextClick,
                isNextEnabled = model.nextEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    BackHandler { onBackClick() }
}

@Composable
private fun InputWithSubmitButton(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean,
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
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimensions.DescriptionFieldMinHeight)
        )
        Spacer(Modifier.height(Dimensions.InputButtonSpacing))

        SubmitButton(
            onNextClick = onNextClick,
            isNextEnabled = isNextEnabled,
            modifier = Modifier.fillMaxWidth()
        )
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

@Composable
private fun SubmitButton(
    onNextClick: () -> Unit,
    isNextEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onNextClick,
        enabled = isNextEnabled,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        modifier = modifier,
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.PositivePrimary,
            disabledContainerColor = ColorPreset.PositivePrimary,

            contentColor = ColorPreset.BackgroundDefaultPrimary,
            disabledContentColor = ColorPreset.BackgroundDefaultPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.NavigationButtonPadding)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = "",
                modifier = Modifier.size(Dimensions.NextButtonIconSize)
            )

            Text(
                text = stringResource(R.string.next_button_label),
                style = TextStyle(
                    fontSize = Dimensions.ButtonLabelFontSize,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun QuizNameScreenPreview() {
    QuizNameUI(
        model = IQuizNameComponent.Model(),
        onNameChange = {},
        onDescriptionChange = {},
        onNextClick = {},
        onQuizHubClick = {},
        onProfileClick = {},
        onSettingsClick = {},
        onBackClick = {}
    )
}