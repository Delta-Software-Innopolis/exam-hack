package com.examhacker.quiz_edit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.examhacker.quiz_edit.component.IQuizEditComponent

@Composable
fun QuizEditScreen(component: IQuizEditComponent) {
    QuizEditUI()
}

@Composable
private fun QuizEditUI() {
    // Временные данные для примера
    val questions = listOf(
        "Why do dogs think their tails are...",
        "Why something is this thing? ...",
        "Why do dogs think their tails are..."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "Review generated questions",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Список вопросов
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            items(questions) { question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = question,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка Add question
        Button(
            onClick = { /* TODO: add question */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add question")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка Save Quiz
        Button(
            onClick = { /* TODO: save quiz */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Quiz")
        }
    }
}

@Preview(device = Devices.PIXEL, showBackground = true)
@Composable
private fun PreviewQuizEditUI() {
    MaterialTheme {
        QuizEditUI()
    }
}