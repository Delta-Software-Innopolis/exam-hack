package com.examhacker.phone_unlock.ui

import androidx.activity.compose.BackHandler
import com.examhacker.resources.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.examhacker.common.ui.AnswerVariantCard
import com.examhacker.common.ui.AnswerVariantStatus
import com.examhacker.common.ui.QuizProgressBar
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.common.ui.QuizSolveBottomBar
import com.examhacker.common.ui.ai_chat.AIChatBottomSheet
import com.examhacker.phone_unlock.controller.IUnlockComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun UnlockScreen(component: IUnlockComponent) {
    val model by component.model.subscribeAsState()
    val slot by component.slot.subscribeAsState()

    UnlockUI(
        model = model,
        slot = slot,
        submitAnswer = component::onAnswerClick,
        takeHint = component::onHintClick,
        finishSolving = component::onProceedClick
    )
}

@Composable
private fun UnlockUI(
    model: IUnlockComponent.Model,
    slot: ChildSlot<*, IUnlockComponent.Child>?,
    submitAnswer: (Int) -> Unit,
    takeHint: () -> Unit,
    finishSolving: () -> Unit
) {
    Scaffold(
        bottomBar = {
            QuizSolveBottomBar(
                onAiClick = takeHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .safeContentPadding()
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
                solvedQuestions = model.solvedQuestions,
                totalQuestions = model.totalQuestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.ProgressBarHeight)
            )

            model.question?.let {
                QuestionSection(
                    question = model.question,
                    finalAnswer = model.finalAnswer,
                    submitAnswer = submitAnswer,
                    finishSolving = finishSolving
                )
            }
        }
    }

    slot?.child?.instance?.let {
        when(it) {
            is IUnlockComponent.Child.AIChat -> AIChatBottomSheet(it.component)
        }
    }

    BackHandler { }
}

@Composable
fun QuestionSection(
    question: Question,
    finalAnswer: AnswerVariant?,
    submitAnswer: (Int) -> Unit,
    finishSolving: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.DescriptionVariantsSpacing),
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

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.DefaultListSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            itemsIndexed(question.variants) { index, variant ->

                AnswerVariantCard(
                    description = variant.description,
                    onClick = { submitAnswer(index) },
                    enabled = finalAnswer == null,
                    status =
                        if (finalAnswer == null || (finalAnswer != variant && !variant.isCorrect))
                            AnswerVariantStatus.DEFAULT
                        else if (variant.isCorrect)
                            AnswerVariantStatus.ANSWERED_CORRECT
                        else
                            AnswerVariantStatus.ANSWERED_WRONG,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.VariantsSpacing)
                )
            }
        }

        finalAnswer?.let {
            Button(
                onClick = finishSolving,
                shape = RoundedCornerShape(Dimensions.ButtonRadius),
                contentPadding = PaddingValues(Dimensions.ScreenPadding),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPreset.BackgroundDefaultPrimary,
                    contentColor = ColorPreset.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.finish_solving_button_label),
                    fontSize = Dimensions.ButtonLabelFontSize,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnlockPreview() {
    UnlockUI(
        model = IUnlockComponent.Model().copy(
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
        slot = null,
        submitAnswer = {},
        takeHint = {},
        finishSolving = {},
    )
}

