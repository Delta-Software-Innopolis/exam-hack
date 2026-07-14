package com.examhacker.quiz_edit.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.FloatingAddButton
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.QuestionList
import com.examhacker.common.ui.ScreenTitle
import com.examhacker.common.ui.SingleBackButtonTopBar
import com.examhacker.common.ui.dialogs.AddQuestionDialog
import com.examhacker.common.ui.dialogs.EditQuestionDialog
import com.examhacker.quiz_edit.component.IQuizEditComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun QuizEditScreen(component: IQuizEditComponent) {
    val model by component.model.subscribeAsState()
    val slot by component.slot.subscribeAsState()

    QuizEditUI(
        model = model,
        slot = slot,
        onEditQuestionClick = component::onEditQuestionClick,
        onAddQuestion = component::onAddQuestionClick,
        onQuizHubClick = component::goToQuizHub,
        onProfileClick = component::goToProfile,
        onSettingsClick = component::goToSettings,
        onCloseClick = component::onCloseClick
    )
}

@Composable
private fun QuizEditUI(
    model: IQuizEditComponent.Model,
    slot: ChildSlot<*, IQuizEditComponent.Child>?,
    onEditQuestionClick: (Int, Question) -> Unit,
    onAddQuestion: () -> Unit,
    onQuizHubClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SingleBackButtonTopBar(
                onBackClick = onCloseClick,
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
        floatingActionButton = {
            FloatingAddButton(onAddQuestion)
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    slot?.child?.instance?.let { child ->
        when(child) {
            is IQuizEditComponent.Child.EditQuestionDialog ->
                EditQuestionDialog(child.component)

            is IQuizEditComponent.Child.AddQuestionDialog  ->
                AddQuestionDialog(child.component)
        }
    }

    BackHandler { onCloseClick() }
}

@Composable
private fun QuestionListWithTitle(
    questions: List<Question>,
    onEditQuestionClick: (Int, Question) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
        modifier = modifier
    ) {
        ScreenTitle(
            text = stringResource(R.string.quiz_edit_screen_title),
            modifier = Modifier.fillMaxWidth(),
            fontSize = Dimensions.ScreenTitleFontSize,
            horizontalArrangement = Arrangement.Start
        )

        QuestionList(
            questions = questions,
            onQuestionClick = onEditQuestionClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(device = Devices.DEFAULT, showBackground = true)
@Composable
private fun PreviewQuizEditScreen() {
    MaterialTheme {
        QuizEditUI(
            model = IQuizEditComponent.Model().copy(questions = createMockQuestions()),
            slot = null,
            onEditQuestionClick = {_, _ ->},
            onAddQuestion = {},
            onQuizHubClick = {},
            onProfileClick = {},
            onSettingsClick = {},
            onCloseClick = {}
        )
    }
}

private fun createMockQuestions(): List<Question> =
    listOf(
        Question(
            description = "Why do dogs think their tails are so clingy, they always want to grab it?",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            )
        ),
        Question(
            description = "Why something is this thing?",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            )
        ),
        Question(
            description = "Why do dogs think their tails are so clingy, they always want to grab it?",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            )
        )
    )