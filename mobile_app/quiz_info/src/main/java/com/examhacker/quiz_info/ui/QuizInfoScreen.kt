package com.examhacker.quiz_info.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.domain.model.QuizStatistics
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.CustomProgressIndicator
import com.examhacker.common.ui.DeleteButton
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.SingleBackButtonTopBar
import com.examhacker.domain.model.Author
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.model.QuizInfo
import com.examhacker.resources.R
import com.examhacker.quiz_info.component.IQuizInfoComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

@Composable
fun QuizInfoScreen(component: IQuizInfoComponent) {
    val model by component.model.subscribeAsState()

    QuizInfoUI(
        model = model,
        onAttemptQuizClick = component::attemptQuiz,
        onViewQuestionsClick = component::viewQuestions,
        onDeleteQuizClick = component::onDeleteQuiz,
        onQuizHubClick = component::goToQuizHub,
        onProfileClick = component::goToProfile,
        onSettingsClick = component::goToSettings,
        onBackClick = component::goBack
    )
}

@Composable
private fun QuizInfoUI(
    model: IQuizInfoComponent.Model,
    onAttemptQuizClick: () -> Unit,
    onViewQuestionsClick: () -> Unit,
    onDeleteQuizClick: () -> Unit,
    onQuizHubClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SingleBackButtonTopBar(
                onBackClick = onBackClick,
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
        containerColor = ColorPreset.BackgroundVariant,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(
                    start = Dimensions.ScreenPadding,
                    end = Dimensions.ScreenPadding,
                    bottom = Dimensions.ScreenPadding
                )
        ) {
            QuizNameWithAuthor(
                quizName = model.quiz?.info?.name ?: "Quiz name",
                authorName = model.quiz?.info?.author?.name ?: "Author name",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.ScreenPadding)
            )

            model.quiz?.description?. let {
                if (it.isNotEmpty() && it.isNotBlank()) {
                    QuizDescriptionCard(
                        description = it,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            AttemptQuizButton(
                onAttemptQuiz = onAttemptQuizClick,
                modifier = Modifier.fillMaxWidth()
            )

            ViewQuestionsButton(
                onViewQuestionsClick = onViewQuestionsClick,
                modifier = Modifier.fillMaxWidth()
            )

            QuizStatisticsCard(
                progress = model.statistics.progress,
                attempts = model.statistics.attemptsNumber,
                rightWrongRatio = model.statistics.rightWrongRatio,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            DeleteButton(
                onDeleteClick = onDeleteQuizClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    BackHandler { onBackClick() }
}

@Composable
private fun QuizNameWithAuthor(
    quizName: String,
    authorName: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimensions.QuizNameSpacing),
        modifier = modifier
    ) {
        Text(
            text = quizName,
            fontSize = Dimensions.TitleSmallFontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = ColorPreset.SecondaryDimm)) {
                    append(stringResource(R.string.by))
                }
                append(" ")
                withStyle(SpanStyle(
                    color = ColorPreset.PositivePrimary,
                    textDecoration = TextDecoration.Underline
                )) {
                    append(authorName)
                }
            },
            fontSize = Dimensions.AuthorNameFontSize,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun QuizDescriptionCard(
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.DefaultCardRadius),
        colors = CardDefaults.cardColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary,
            contentColor = ColorPreset.SecondaryDimm
        )
    ) {
        Text(
            text = description,
            fontSize = Dimensions.DescriptionInputFontSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(Dimensions.ScreenPadding)
        )
    }
}

@Composable
private fun AttemptQuizButton(
    onAttemptQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onAttemptQuiz,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.PositivePrimary,
            contentColor = ColorPreset.BackgroundVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.solve_quiz_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ViewQuestionsButton(
    onViewQuestionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onViewQuestionsClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        border = BorderStroke(
            width = Dimensions.DefaultBorderWidth,
            color = ColorPreset.SecondaryDimm
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.BackgroundVariant,
            contentColor = ColorPreset.SecondaryDimm
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_list),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.view_questions_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun QuizStatisticsCard(
    progress: Float,
    attempts: Int,
    rightWrongRatio: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.DefaultCardRadius),
        colors = CardDefaults.cardColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding)
        ) {
            QuizProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )

            StatisticsList(
                attempts = attempts,
                rightWrongRatio = rightWrongRatio,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuizProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.quiz_progress_title),
                fontSize = Dimensions.ProgressBarFontSize,
                fontWeight = FontWeight.Normal,
                color = ColorPreset.Black
            )

            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "",
                tint = ColorPreset.TextDefaultSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${(progress * 100).toInt()} %",
                fontSize = Dimensions.ProgressBarFontSize,
                fontWeight = FontWeight.Normal,
                color = ColorPreset.Black
            )

            CustomProgressIndicator(
                progress = progress,
                trackColor = ColorPreset.BackgroundVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.ProgressBarHeight)
            )
        }
    }
}

@Composable
private fun StatisticsList(
    attempts: Int,
    rightWrongRatio: Float,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.quiz_statistics_title),
                fontSize = Dimensions.ProgressBarFontSize,
                fontWeight = FontWeight.Normal,
                color = ColorPreset.Black
            )

            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "",
                tint = ColorPreset.TextDefaultSecondary
            )
        }

        Column(horizontalAlignment = Alignment.Start) {
            StatisticsListItem(
                text = "${stringResource(R.string.quiz_statistics_attempts_number)} $attempts",
            )

            StatisticsListItem(
                text = "${stringResource(R.string.quiz_statistics_rw_ratio)} $rightWrongRatio"
            )
        }
    }
}

@Composable
private fun StatisticsListItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box( modifier = Modifier
            .size(5.dp)
            .background(
                color = ColorPreset.Black,
                shape = CircleShape
            )
        )

        Text(
            text = text,
            fontSize = Dimensions.ProgressBarFontSize,
            fontWeight = FontWeight.Normal,
            color = ColorPreset.Black
        )
    }
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun QuizInfoScreenPreview() {
    QuizInfoUI(
        model = IQuizInfoComponent.Model().copy(
            quiz = createMockQuiz(),
            statistics = createMockStatistics()
        ),
        onAttemptQuizClick = {},
        onViewQuestionsClick = {},
        onDeleteQuizClick = {},
        onQuizHubClick = {},
        onProfileClick = {},
        onSettingsClick = {},
        onBackClick = {}
    )
}

@OptIn(ExperimentalTime::class)
private fun createMockQuiz() =
    Quiz(
        info = QuizInfo(
            id = 1,
            name = "Full Quiz Title, maybe a long one",
            creationDate = now().toString(),
            updatingDate = null,
            author = Author(1, "User")
        ),
        description = "Full quiz description, may take several lines, like a lot of text\n" + "For real",
        questions = emptyList()
    )

private fun createMockStatistics() =
    QuizStatistics(
        quizId = 1,
        progress = 0.32f,
        attemptsNumber = 67,
        rightWrongRatio = 0.76f
    )