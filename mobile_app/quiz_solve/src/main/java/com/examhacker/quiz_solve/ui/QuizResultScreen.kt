package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.examhacker.quiz_solve.component.IQuizResultComponent
import com.examhacker.resources.Dimensions

import androidx.compose.ui.res.stringResource
import com.examhacker.resources.R
@Composable
fun QuizResultScreen(
    component: IQuizResultComponent
) {

    val uiState = component.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(
            Dimensions.DescriptionVariantsSpacing
        )
    ) {

        Text(
            text = stringResource(R.string.quiz_result_placeholder)
        )

        // TODO Result UI

    }
}