package com.examhacker.quiz_solve.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.examhacker.quiz_solve.component.IQuizResultComponent
import com.examhacker.resources.Dimensions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.ui.QuizProgressBar
import com.examhacker.common.ui.SingleBackButtonTopBar
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.R

@Composable
fun QuizResultScreen(component: IQuizResultComponent) {
    val model by component.model.subscribeAsState()

    QuizResultUI(
        model = model,
        onQuitSolving = component::quitSolving,
        back = component::back
    )
}

@Composable
private fun QuizResultUI(
    model: IQuizResultComponent.Model,
    onQuitSolving: () -> Unit,
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
                solvedQuestions = model.totalQuestions,
                totalQuestions = model.totalQuestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.QuizProgressBarPadding)
            )

            ResultSection(
                correctAnswers = model.correctAnswers,
                totalQuestions = model.totalQuestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            QuitSolvingButton(
                onQuitSolving = onQuitSolving,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    BackHandler { back() }
}

@Composable
private fun ResultSection(
    correctAnswers: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.QuizResultSectionSpacing)
        ) {
            Text(
                text = stringResource(R.string.quiz_result_screen_title),
                color = ColorPreset.Black,
                fontSize = Dimensions.QuizResultFontSize,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "${stringResource(R.string.quiz_result_correct_answers)} $correctAnswers/$totalQuestions",
                color = ColorPreset.Black,
                fontSize = Dimensions.QuizResultFontSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun QuitSolvingButton(
    onQuitSolving: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onQuitSolving,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        border = BorderStroke(
            width = Dimensions.DefaultBorderWidth,
            color = ColorPreset.SecondaryDimm
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ColorPreset.Transparent,
            contentColor = ColorPreset.SecondaryDimm
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_house),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.quit_solving_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun QuizResultScreenPreview() {
    QuizResultUI(
        model = createMockData(),
        onQuitSolving = {},
        back = {}
    )
}

private fun createMockData(): IQuizResultComponent.Model =
    IQuizResultComponent.Model(
        correctAnswers = 4,
        totalQuestions = 5
    )