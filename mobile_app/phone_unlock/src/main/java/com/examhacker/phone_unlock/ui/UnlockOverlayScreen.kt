package com.examhacker.phone_unlock.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.common.ui.AnswerVariantCard
import com.examhacker.common.ui.AnswerVariantStatus
import com.examhacker.common.ui.QuizProgressBar
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.common.ui.QuizSolveBottomBar
import com.examhacker.phone_unlock.controller.UnlockOverlayController
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Deprecated("Migrated to View")
@Composable
fun UnlockOverLayScreen() {
    UnlockOverlayUI(
        model = UnlockOverlayController.State(),
        submitAnswer = {},
        takeHint = {},
        back = {}
    )
}

@Composable
private fun UnlockOverlayUI(
    model: UnlockOverlayController.State,
    submitAnswer: (AnswerVariant) -> Unit,
    takeHint: () -> Unit,
    back: () -> Unit
) {
    Scaffold(
        bottomBar = {
            QuizSolveBottomBar(
                onAiClick = takeHint,
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
            QuizProgressBar(
                solvedQuestions = 1,
                totalQuestions = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.ProgressBarHeight)
            )

            model.question?.let {
                QuestionSection(
                    question = model.question,
                    finalAnswer = model.finalAnswer,
                    submitAnswer = submitAnswer
                )
            }
        }
    }

    BackHandler { back() }
}

@Composable
fun QuestionSection(
    question: Question,
    finalAnswer: AnswerVariant?,
    submitAnswer: (AnswerVariant) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = question.description,
            style = TextStyle(
                fontSize = Dimensions.QuestionDescriptionFontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.padding(Dimensions.ScreenPadding)
        )
        Spacer(Modifier.height(Dimensions.DescriptionVariantsSpacing))

        question.variants.forEach {
            AnswerVariantCard(
                description = it.description,
                onClick = { submitAnswer(it) },
                enabled = finalAnswer == null,
                status =
                    if (finalAnswer == null || (finalAnswer != it && !it.isCorrect))
                        AnswerVariantStatus.DEFAULT
                    else if (it.isCorrect)
                        AnswerVariantStatus.ANSWERED_CORRECT
                    else
                        AnswerVariantStatus.ANSWERED_WRONG,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.VariantsSpacing)
            )
            Spacer(Modifier.height(Dimensions.VariantsSpacing))
        }
    }
}

@Preview(device = Devices.PIXEL, showBackground = true, showSystemUi = true)
@Composable
fun UnlockOverlayPreview() {
    UnlockOverlayUI(
        model = UnlockOverlayController.State().copy(
            question = Question(
                id = 1,
                description = "Question description, may span several lines, we’ll discuss the font size and boldness later",
                variants = listOf(
                    AnswerVariant(
                        description = "Option 1, option description",
                        isCorrect = false
                    ),
                    AnswerVariant(
                        description = "Option 2, description, maybe a correct answer",
                        isCorrect = false
                    ),
                    AnswerVariant(
                        description = "Option 3, description, choose wisely",
                        isCorrect = false
                    ),
                    AnswerVariant(
                        description = "Option 4, idk which is correct, really",
                        isCorrect = true
                    )
                )
            )
        ),
        submitAnswer = {},
        takeHint = {},
        back = {}
    )
}

