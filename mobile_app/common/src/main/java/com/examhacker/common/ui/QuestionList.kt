package com.examhacker.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.examhacker.common.data.Question
import com.examhacker.resources.Dimensions

@Composable
fun QuestionList(
    questions: List<Question>,
    onQuestionClick: (Int, Question) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.DefaultListSpacing),
        modifier = modifier
    ) {
        itemsIndexed(questions) { index, question, ->
            QuestionListItem(
                number = index + 1,
                questionDescription = question.description,
                onQuestionClick = { onQuestionClick(index, question) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}