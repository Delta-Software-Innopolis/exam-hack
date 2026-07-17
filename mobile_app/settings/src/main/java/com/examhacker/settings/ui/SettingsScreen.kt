package com.examhacker.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.common.data.Quiz
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.ContentPlaceholder
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.NotImplementedSnackBarUI
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R
import com.examhacker.settings.component.ISettingsComponent
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(component: ISettingsComponent) {
    val model by component.model.subscribeAsState()

    SettingsUI(
        model = model,
        onPhoneUnlockFeatureToggle = component::onPhoneUnlockFeatureToggle,
        onLanguageToggle = component::onLanguageToggle,
        onThemeToggle = component::onThemeToggle,
        onQuizSelect = component::onQuizSelect,
        onProfileClick = component::toProfile,
        onQuizListClick = component::toQuizList,
        onQuizHubClick = component::toQuizHub
    )
}

@Composable
private fun SettingsUI(
    model: ISettingsComponent.Model,
    onPhoneUnlockFeatureToggle: () -> Unit,
    onLanguageToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    onQuizSelect: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onQuizListClick: () -> Unit,
    onQuizHubClick: () -> Unit
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(Dimensions.ScreenPadding),
                snackbar = {
                    NotImplementedSnackBarUI(Modifier.fillMaxWidth())
                }
            )
        },
        topBar = {
            ScreenTitle(
                text = stringResource(R.string.settings_screen_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(Dimensions.ScreenPadding)
            )
        },
        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.SETTINGS,
                onQuizListClick = onQuizListClick,
                onQuizHubClick = onQuizHubClick,
                onProfileClick = onProfileClick,
                onSettingsClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        containerColor = ColorPreset.BackgroundVariant,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding)
        ) {
            SettingItemCard(Modifier.fillMaxWidth()) {
                ThemeLanguageSettings(
                    isEnglishLanguage = model.isEnglishLanguage,
                    isLightTheme = model.isLightTheme,
                    onLanguageToggle = {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    onThemeToggle = {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingItemCard(Modifier.fillMaxWidth()) {
                PhoneUnlockFeatureSetting(
                    isPhoneUnlockFeatureOn = model.isPhoneUnlockFeatureOn,
                    onPhoneUnlockFeatureToggle = onPhoneUnlockFeatureToggle,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (model.isPhoneUnlockFeatureOn) {
                SettingItemCard(Modifier.fillMaxWidth()) {
                    model.quizzes?.let {
                        QuizSelectList(
                            quizzes = it,
                            selectedQuiz = model.selectedQuiz,
                            onQuizSelect = onQuizSelect,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                        ?: ContentPlaceholder(
                            isLoading = model.isQuizzesLoading,
                            error = model.quizLoadingError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.3f)
                        )
                }
            }
        }
    }
}

@Composable
private fun SettingItemCard(
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.DefaultCardRadius),
        colors = CardDefaults.cardColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(Dimensions.ScreenPadding)
        ) {
            content()
        }
    }
}

@Composable
private fun ThemeLanguageSettings(
    isEnglishLanguage: Boolean,
    isLightTheme: Boolean,
    onLanguageToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.SettingCardSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingRow(
            settingLabel = stringResource(R.string.theme_setting_label),
            switchLabelLeft = stringResource(R.string.theme_light_label),
            switchLabelRight = stringResource(R.string.theme_dark_label),
            isSwitchLeft = isLightTheme,
            onSwitchToggle = onThemeToggle,
            modifier = Modifier.fillMaxWidth()
        )

        SettingRow(
            settingLabel = stringResource(R.string.language_setting_label),
            switchLabelLeft = stringResource(R.string.language_english_label),
            switchLabelRight = stringResource(R.string.language_russian_label),
            isSwitchLeft = isEnglishLanguage,
            onSwitchToggle = onLanguageToggle,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PhoneUnlockFeatureSetting(
    isPhoneUnlockFeatureOn: Boolean,
    onPhoneUnlockFeatureToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
       verticalAlignment = Alignment.CenterVertically,
       horizontalArrangement = Arrangement.SpaceBetween,
       modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.unlock_feature_setting_label),
            color = ColorPreset.Black,
            fontSize = Dimensions.SettingLabelFontSize,
            fontWeight = FontWeight.Normal
        )

        Switch(
            checked = isPhoneUnlockFeatureOn,
            onCheckedChange = { onPhoneUnlockFeatureToggle() },
            colors = SwitchDefaults.colors(
                checkedTrackColor = ColorPreset.BackgroundVariant,
                uncheckedTrackColor = ColorPreset.BackgroundVariant,

                checkedThumbColor = ColorPreset.PositivePrimary,
                uncheckedThumbColor = ColorPreset.SecondaryDimm,

                checkedBorderColor = ColorPreset.BorderPositiveTertiary,
                uncheckedBorderColor = ColorPreset.Secondary
            )
        )
    }
}

@Composable
private fun QuizSelectList(
    quizzes: List<Quiz>,
    selectedQuiz: Quiz?,
    onQuizSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.DefaultListSpacing),
        modifier = modifier.selectableGroup()   
    ) {
        items(quizzes) { quiz ->

            QuizSelectable(
                quiz = quiz,
                isSelected = quiz == selectedQuiz,
                onSelect = onQuizSelect,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuizSelectable(
    quiz: Quiz,
    isSelected: Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .selectable(
                selected = isSelected,
                onClick = { onSelect(quiz.id) },
                role = Role.RadioButton
            )
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = quiz.name,
                color = ColorPreset.Black,
                fontSize = Dimensions.QuizSelectableFontSize,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                text = "${quiz.questions.size} question${if (quiz.questions.size > 1) "s" else ""}",
                color = ColorPreset.Secondary,
                fontSize = Dimensions.QuizSelectableFontSize,
                fontWeight = FontWeight.Normal
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = ColorPreset.PositivePrimary,
                unselectedColor = ColorPreset.SecondaryDimm
            )
        )
    }
}

@Composable
private fun SettingRow(
    settingLabel: String,
    switchLabelLeft: String,
    switchLabelRight: String,
    isSwitchLeft: Boolean,
    onSwitchToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = settingLabel,
            fontSize = Dimensions.SettingLabelFontSize,
            fontWeight = FontWeight.Normal,
            color = ColorPreset.Black
        )

        SettingSwitch(
            switchLabelLeft = switchLabelLeft,
            switchLabelRight = switchLabelRight,
            isSwitchLeft = isSwitchLeft,
            onSwitchToggle = onSwitchToggle
        )
    }
}

@Composable
private fun SettingSwitch(
    switchLabelLeft: String,
    switchLabelRight: String,
    isSwitchLeft: Boolean,
    onSwitchToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(Dimensions.SettingSwitchWidth)
            .border(
                shape = RoundedCornerShape(Dimensions.SettingSwitchRadius),
                width = Dimensions.DefaultBorderWidth,
                color = ColorPreset.Secondary
            )
            .background(
                shape = RoundedCornerShape(Dimensions.SettingSwitchRadius),
                color = ColorPreset.BackgroundVariant
            )
            .padding(Dimensions.SettingSwitchPadding)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = Dimensions.SettingSwitchThumbWidth,
                    height = Dimensions.SettingSwitchThumbHeight
                )
                .background(
                    shape = RoundedCornerShape(Dimensions.SettingSwitchThumbRadius),
                    color = ColorPreset.PositivePrimary
                )
                .then(
                    if (isSwitchLeft)
                        Modifier.align(Alignment.CenterStart)
                    else
                        Modifier.align(Alignment.CenterEnd)
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingSwitchOptionBox(
                label = switchLabelLeft,
                isActive = isSwitchLeft,
                onClick =
                    if (!isSwitchLeft)
                        onSwitchToggle
                    else
                        null
            )

            SettingSwitchOptionBox(
                label = switchLabelRight,
                isActive = !isSwitchLeft,
                onClick =
                    if (isSwitchLeft)
                        onSwitchToggle
                    else
                        null
            )
        }
    }
}

@Composable
private fun SettingSwitchOptionBox(
    label: String,
    isActive: Boolean,
    onClick: (() -> Unit)?
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(Dimensions.SettingSwitchThumbWidth)
            .then(
                onClick?.let {
                    Modifier.clickable { it.invoke() }
                } ?: Modifier
            )
            .padding(Dimensions.SettingSwitchThumbVerticalPadding)
    ) {
        Text(
            text = label,
            fontSize = Dimensions.SettingOptionFontSize,
            fontWeight = FontWeight.Normal,
            color =
                if (isActive)
                    ColorPreset.BackgroundVariant
                else
                    ColorPreset.SecondaryDimm
        )
    }
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun SettingsScreenPreview() {
    SettingsUI(
        model = ISettingsComponent.Model()
            .copy(isPhoneUnlockFeatureOn = true, quizzes = createMockQuizzes()),
        onPhoneUnlockFeatureToggle = {},
        onLanguageToggle = {},
        onThemeToggle = {},
        onQuizSelect = {},
        onProfileClick = {},
        onQuizListClick = {},
        onQuizHubClick = {}
    )
}

private fun createMockQuizzes(): List<Quiz> =
    listOf(
        Quiz(
            id = 1,
            authorName = "pavmash",
            name = "best quiz",
            description = "",
            questions = listOf(
                Question(
                    description = "How much?",
                    variants = listOf(
                        AnswerVariant("One", false),
                        AnswerVariant("Two", true)
                    )
                ),
                Question(
                    description = "How are you",
                    variants = listOf(AnswerVariant("good", true))
                )
            )
        ),
        Quiz(
            id = 2,
            authorName = "upconett",
            name = "Nice quiz",
            description = "",
            questions = listOf(
                Question(
                    description = "Hate me?",
                    variants = listOf(AnswerVariant("No", true))
                )
            )
        )
    )