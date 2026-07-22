package com.examhacker.quiz_create.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.FloatingAddButton
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.QuestionList
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.common.ui.dialogs.AddQuestionDialog
import com.examhacker.common.ui.dialogs.EditQuestionDialog
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import com.examhacker.resources.R
import com.examhacker.quiz_create.component.IQuizReviewComponent
import com.examhacker.quiz_create.ui.common.CreationStage
import com.examhacker.quiz_create.ui.common.QuizCreationTopBar
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
internal fun QuizReviewScreen(component: IQuizReviewComponent) {
    val model by component.model.subscribeAsState()
    val slot by component.slot.subscribeAsState()

    QuizReviewUI(
        model = model,
        slot = slot,
        onEditQuestionClick = component::onEditQuestionClick,
        onAddQuestion = component::onAddQuestionClick,
        onSaveQuizClick = component::onSaveQuizClick,
        onQuizHubClick = component::goToQuizHub,
        onProfileClick = component::goToProfile,
        onSettingsClick = component::goToSettings,
        goBack = component::back
    )
}

@Composable
private fun QuizReviewUI(
    model: IQuizReviewComponent.Model,
    slot: ChildSlot<*, IQuizReviewComponent.Child>?,
    onEditQuestionClick: (Int, Question) -> Unit,
    onAddQuestion: () -> Unit,
    onSaveQuizClick: () -> Unit,
    onQuizHubClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    goBack: () -> Unit
) {
    Scaffold(
        topBar = {
            QuizCreationTopBar(
                creationStage = CreationStage.REVIEW,
                onBackClick = goBack,
                onForthClick = {},
                isForthEnabled = false,
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
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorPreset.BackgroundVariant
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding)
        ) {
            QuestionListWithTitle(
                questions = model.questions,
                onEditQuestionClick = onEditQuestionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            )

            SaveQuizAddQuestionButtons(
                onAddQuestion = onAddQuestion,
                onSaveQuizClick = onSaveQuizClick,
                saveEnabled = model.questions.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    slot?.child?.instance?.let { child ->
        when(child) {
            is IQuizReviewComponent.Child.EditQuestionDialog ->
                EditQuestionDialog(child.component)

            is IQuizReviewComponent.Child.AddQuestionDialog  ->
                AddQuestionDialog(child.component)
        }
    }

    BackHandler { goBack() }
}

@Composable
private fun QuestionListWithTitle(
    questions: List<Question>,
    onEditQuestionClick: (Int, Question) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.QuizReviewTitleSpacing),
        modifier = modifier
    ) {
        ScreenTitle(
            text = stringResource(R.string.quiz_review_title),
            modifier = Modifier.fillMaxWidth(),
            fontSize = Dimensions.SubtitleFontSize,
            horizontalArrangement = Arrangement.Start
        )

        QuestionList(
            questions = questions,
            onQuestionClick = onEditQuestionClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SaveQuizAddQuestionButtons(
    onAddQuestion: () -> Unit,
    onSaveQuizClick: () -> Unit,
    saveEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        FloatingAddButton(onAddQuestion)

        QuizSaveButton(
            onClick = onSaveQuizClick,
            enabled = saveEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuizSaveButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.PositivePrimary,
            disabledContainerColor = ColorPreset.PositivePrimary,

            contentColor = ColorPreset.BackgroundVariant,
            disabledContentColor = ColorPreset.TextDefaultSecondary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.quiz_save_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(device = Devices.PIXEL, showBackground = true)
@Composable
private fun PreviewQuizReviewScreen() {
    MaterialTheme {
        QuizReviewUI(
            model = IQuizReviewComponent.Model().copy(questions = createMockQuestions()),
            slot = null,
            onEditQuestionClick = {_, _ ->},
            onAddQuestion = {},
            onSaveQuizClick = {},
            onQuizHubClick = {},
            onProfileClick = {},
            onSettingsClick = {},
            goBack = {}
        )
    }
}

private fun createMockQuestions(): List<Question> =
    listOf(
        Question(
            id = 1,
            description = "Why do dogs think their tails are so clingy, they always want to grab it?",
            hint = "Hint text",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            )
        ),
        Question(
            id = 2,
            description = "Why something is this thing?",
            hint = "Hint text",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            )
        ),
        Question(
            id = 3,
            description = "Why do dogs think their tails are so clingy, they always want to grab it?",
            hint = "Hint text",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            )
        )
    )