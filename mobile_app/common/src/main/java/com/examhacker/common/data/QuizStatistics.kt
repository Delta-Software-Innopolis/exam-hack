package com.examhacker.common.data

data class QuizStatistics(
    val quizId: Int,
    val progress: Float,
    val attemptsNumber: Int,
    val rightWrongRatio: Float
)