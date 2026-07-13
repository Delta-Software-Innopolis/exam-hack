package com.examhacker.domain.model

data class QuizStatistics(
    val quizId: Int,
    val progress: Float,
    val attemptsNumber: Int,
    val rightWrongRatio: Float
)