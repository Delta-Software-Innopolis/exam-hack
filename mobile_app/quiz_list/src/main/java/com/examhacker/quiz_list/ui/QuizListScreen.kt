package com.examhacker.quiz_list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.FloatingAddButton
import com.examhacker.common.ui.NavigationTab

@Composable
fun QuizListScreen(
    component: IQuizListComponent
) {

    val quizzes = List(8) { "Quiz Name" }

    Scaffold(

        floatingActionButton = {
            FloatingAddButton(component::goToQuizCreation)
        },

        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.QUIZ_LIST,
                onQuizListClick = {},
                onQuizHubClick = component::goToQuizHub,
                onProfileClick = component::goToProfile,
                onSettingsClick = component::goToSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
            )
        }

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = Dimensions.ScreenPadding,
                end = Dimensions.ScreenPadding,
                top = Dimensions.ScreenPadding,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            item {
                Text(
                    text = "Your Quizzes",
                    fontSize = Dimensions.ScreenTitleFontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

            }

            items(quizzes) {
                QuizCard(
                    quizName = it,
                    author = "by User",
                    onClick = component::onQuizClick
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun QuizListScreenPreview() {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.padding(bottom = 8.dp),
                shape = CircleShape,
                containerColor = ColorPreset.PositivePrimary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_plus),
                    contentDescription = null,
                    tint = ColorPreset.BackgroundDefaultPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        },

        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.QUIZ_LIST,
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
            )

        }

    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(Dimensions.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Text(
                    text = "Your Quizzes",
                    fontSize = Dimensions.ScreenTitleFontSize,
                    fontWeight = FontWeight.Bold
                )
            }

            items(List(8) { "Quiz Name" }) {
                QuizCard(
                    quizName = it,
                    author = "by User"
                )
            }

        }

    }

}