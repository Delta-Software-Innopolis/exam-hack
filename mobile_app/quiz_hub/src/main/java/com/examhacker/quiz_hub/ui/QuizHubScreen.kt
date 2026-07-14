package com.examhacker.quiz_hub.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.NotImplementedSnackBarUI
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.quiz_hub.component.IQuizHubComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R
import kotlinx.coroutines.launch

@Composable
fun QuizHubScreen(component: IQuizHubComponent) {
    val model by component.model.subscribeAsState()

    QuizHubUI(
        model = model,
        onInputChange = component::onSearchInputChange,
        onSearchClick = component::onSearchClick,
        onQuizClick = component::goToQuizList,
        onProfileClick = component::goToProfile,
        onSettingsClick = component::goToSettings
    )
}

@Composable
private fun QuizHubUI(
    model: IQuizHubComponent.Model,
    onInputChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onQuizClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ScreenTitle(
                text = stringResource(R.string.quiz_hub_screen_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(Dimensions.ScreenPadding)
            )
        },
        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.QUIZ_HUB,
                onQuizListClick = onQuizClick,
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .safeContentPadding()
                    .padding(horizontal = Dimensions.ScreenPadding),
                snackbar = {
                    NotImplementedSnackBarUI(Modifier.fillMaxWidth())
                }
            )
        },
        containerColor = ColorPreset.BackgroundVariant,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.QuizHubPlaceholderTextSpacing),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(
                    start = Dimensions.ScreenPadding,
                    end = Dimensions.ScreenPadding,
                    bottom = Dimensions.ScreenPadding
                )
        ) {
            SearchBar(
                input = model.searchInput,
                onInputChange = onInputChange,
                onSearchCLick = {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.not_used_in_mvp),
                fontSize = Dimensions.SubtitleFontSize,
                fontWeight = FontWeight.Bold,
                color = ColorPreset.NotImplementedSecondary
            )
        }
    }
}

@Composable
private fun SearchBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSearchCLick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        modifier = modifier,
        textStyle = TextStyle(
            fontSize = Dimensions.InputLabelFontSize,
            fontWeight = FontWeight.Normal
        ),
        label = {
            Text(
                text = stringResource(R.string.search_bar_label),
                fontSize = Dimensions.InputLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search_magnifying_glass),
                contentDescription = "",
                modifier = Modifier
                    .size(Dimensions.MediumIconSize)
                    .clickable { onSearchCLick() }
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchCLick() }),
        singleLine = true,
        shape = RoundedCornerShape(Dimensions.SearchBarRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ColorPreset.BackgroundVariant,
            unfocusedContainerColor = ColorPreset.BackgroundVariant,

            focusedTextColor = ColorPreset.Black,
            unfocusedTextColor = ColorPreset.Black,

            focusedBorderColor = ColorPreset.BorderDefault,
            unfocusedBorderColor = ColorPreset.BorderDefault,

            focusedLabelColor = ColorPreset.TextDefaultSecondary,
            unfocusedLabelColor = ColorPreset.TextDefaultSecondary,

            focusedTrailingIconColor = ColorPreset.BorderDefault,
            unfocusedTrailingIconColor = ColorPreset.BorderDefault,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun QuizHubContentPreview() {
    QuizHubUI(
        model = IQuizHubComponent.Model(),
        onInputChange = {},
        onSearchClick = {},
        onQuizClick = {},
        onProfileClick = {},
        onSettingsClick = {}
    )
}