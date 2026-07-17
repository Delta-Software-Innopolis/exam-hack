package com.examhacker.quiz_list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import com.examhacker.resources.R
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examhacker.quiz_list.component.IQuizListComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.FloatingAddButton
import com.examhacker.common.ui.NavigationTab
import com.examhacker.common.ui.ScreenTitle

@Composable
fun QuizListScreen(component: IQuizListComponent) {
    val model by component.model.subscribeAsState()

    QuizListUI(
        model = model,
        onAddQuizClick = component::onAddQuiz,
        onQuizClick = component::onQuizClick,
        onProfileClick = component::goToProfile,
        onQuizHubClick = component::goToQuizHub,
        onSettingsClick = component::goToSettings
    )
}

@Composable
fun QuizListUI(
    model: IQuizListComponent.Model,
    onAddQuizClick: () -> Unit,
    onQuizClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQuizHubClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingAddButton(onAddQuizClick)
        },
        topBar = {
            ScreenTitle(
                text = stringResource(R.string.quiz_list_screen_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(Dimensions.ScreenPadding),
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
                    .systemBarsPadding()
            )
        },
        containerColor = ColorPreset.BackgroundVariant,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(
                start = Dimensions.ScreenPadding,
                end = Dimensions.ScreenPadding,
                top = Dimensions.ScreenPadding
            ),
            verticalArrangement = Arrangement.spacedBy(Dimensions.DefaultListSpacing)
        ) {
            model.quizzes?.let { quizzes ->
                items(quizzes) {
                    QuizCard(
                        quizName = it.name,
                        author = "${stringResource(R.string.by)} ${it.authorName}",
                        onClick = onQuizClick
                    )
                }
            }
        }

    }
}

@Composable
fun QuizCard(
    quizName: String,
    author: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.DefaultCardRadius))
            .background(ColorPreset.BackgroundDefaultPrimary)
            .clickable(onClick = onClick)
            .padding(Dimensions.ScreenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = quizName,
                fontSize = Dimensions.QuizItemTitleFontSize,
                fontWeight = FontWeight.Bold,
                color = ColorPreset.Black
            )

            Text(
                text = author,
                fontSize = Dimensions.QuizItemAuthorFontSize,
                fontWeight = FontWeight.Normal,
                color = ColorPreset.SecondaryDimm
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = "",
            tint = ColorPreset.SecondaryDimm,
            modifier = Modifier
                .padding(Dimensions.NavigationButtonPadding)
                .size(Dimensions.NavigationIconSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizListScreenPreview() {
    QuizListUI(
        model = IQuizListComponent.Model(),
        onAddQuizClick = {},
        onQuizClick = {},
        onProfileClick = {},
        onQuizHubClick = {},
        onSettingsClick = {}
    )
}