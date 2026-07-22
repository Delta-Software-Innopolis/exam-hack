package com.examhacker.quiz_solve.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.examhacker.common.ui.QuizProgressBar
import com.examhacker.common.ui.QuizSolveBottomBar
import com.examhacker.common.ui.QuizSolveQuestionSection
import com.examhacker.common.ui.SingleBackButtonTopBar
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import com.examhacker.common.ui.ai_chat.AIChatBottomSheet
import com.examhacker.quiz_solve.component.IQuizQuestionComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun QuizQuestionScreen(component: IQuizQuestionComponent) {
    val model by component.model.subscribeAsState()
    val slot by component.slot.subscribeAsState()

    QuizQuestionUI(
        model = model,
        slot = slot,
        onAnswerCurrentQuestion = component::answerCurrentQuestion,
        onPreviousQuestion = component::goToPreviousQuestion,
        onNextQuestion = component::goToNextQuestion,
        onOpenAiChat = component::openAiChat,
        back = component::back
    )
}

@Composable
private fun QuizQuestionUI(
    model: IQuizQuestionComponent.Model,
    slot: ChildSlot<*, IQuizQuestionComponent.Child>?,
    onAnswerCurrentQuestion: (AnswerVariant) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onOpenAiChat: () -> Unit,
    back: () -> Unit
) {
    Scaffold(
        topBar = {
            SingleBackButtonTopBar(
                onBackClick = back,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        bottomBar = {
            QuizSolveBottomBar(
                onAiClick = onOpenAiChat,
                onPreviousClick = onPreviousQuestion,
                onNextClick = onNextQuestion,
                previousEnabled = model.previousEnabled,
                nextEnabled = model.nextEnabled,
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
            verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding)
        ) {
            QuizProgressBar(
                solvedQuestions = model.answers.count { it != null },
                totalQuestions = model.questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.QuizProgressBarPadding)
            )

            QuizSolveQuestionSection(
                question = model.questions[model.currentQuestionIndex],
                finalAnswer = model.answers[model.currentQuestionIndex],
                onAnswerClick = onAnswerCurrentQuestion,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    slot?.child?.instance?.let {
        when(val child = it) {
            is IQuizQuestionComponent.Child.AIChat ->
                AIChatBottomSheet(it.component)
        }
    }

    BackHandler { back() }
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun QuizQuestionScreen() {
    QuizQuestionUI(
        model = createMockData(),
        slot = null,
        onAnswerCurrentQuestion = {},
        onPreviousQuestion = {},
        onNextQuestion = {},
        onOpenAiChat = {},
        back = {}
    )
}

private fun createMockData(): IQuizQuestionComponent.Model {
    val question1 = Question(
        id = 1,
        description = "Question description, may span several lines, we’ll discuss the font size and boldness later",
        hint = "Hint text",
        variants = listOf(
            AnswerVariant("Option 1, option description", false),
            AnswerVariant("Option 2, description, maybe a correct answer", false),
            AnswerVariant("Option 3, description, choose wisely", false),
            AnswerVariant("Option 4, idk which is correct, really", true)
        )
    )

    val question2 = Question(
        id = 2,
        description = "Why do dogs think their tails are so clingy, they always want to grab it?",
        hint = "Hint text",
        variants = listOf(
            AnswerVariant("Option 1", false),
            AnswerVariant("Option 2", false),
            AnswerVariant("Option 3", true),
            AnswerVariant("Option 4", false)
        )
    )

    return IQuizQuestionComponent.Model(
        questions = listOf(question1, question2),
        currentQuestionIndex = 0,
        answers = List(2) { null },
        nextEnabled = true,
        previousEnabled = false
    )
}