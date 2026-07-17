package com.examhacker.common.utility

import android.content.Context
import com.examhacker.common.data.Quiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.let

interface ISettingStorage {

    suspend fun saveUnlockFeatureMode(isOn: Boolean)
    fun getUnlockFeatureMode(): Boolean
    suspend fun saveUnlockFeatureQuiz(quiz: Quiz)
    suspend fun updateQuestionAnsweredStatus(id: Int)
    fun getUnlockFeatureQuiz(): Pair<Quiz, List<Boolean>>?
    suspend fun clearUnlockFeatureQuiz()
}

class SettingStorage(context: Context) : ISettingStorage {

    private val prefs by lazy {
        context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }

    override suspend fun saveUnlockFeatureMode(isOn: Boolean) {
        withContext(Dispatchers.IO) {
            prefs.edit {
                putBoolean(IS_UNLOCK_FEATURE_ON, isOn)
            }
        }
    }

    override fun getUnlockFeatureMode(): Boolean {
        return prefs.getBoolean(IS_UNLOCK_FEATURE_ON, false)
    }

    override suspend fun saveUnlockFeatureQuiz(quiz: Quiz) {
        withContext(Dispatchers.IO) {
            val quizJson = Json.encodeToJsonElement(quiz to List(quiz.questions.size) { false })
            prefs.edit {
                putString("unlockFeatureQuiz", quizJson.toString())
            }
        }
    }

    override suspend fun updateQuestionAnsweredStatus(id: Int) {
        withContext(Dispatchers.IO) {
            val quizWithAnswers =
                prefs.getString(UNLOCK_FEATURE_QUIZ, null)?.let<String, Pair<Quiz, List<Boolean>>> {
                    val quizJson = Json.parseToJsonElement(it)
                    Json.decodeFromJsonElement(quizJson)
                }

            quizWithAnswers?.let {
                val questions = it.first.questions
                val answered = it.second
                var updatedAnswered = answered.mapIndexed { index, isAnswered ->
                    if (questions[index].id == id)
                        !isAnswered
                    else
                        isAnswered
                }
                if (updatedAnswered.count { isAnswered -> !isAnswered } == 0) {
                    updatedAnswered = List(questions.size) { false }
                }

                val quizJson = Json.encodeToJsonElement(it.first to updatedAnswered)
                prefs.edit {
                    putString(UNLOCK_FEATURE_QUIZ, quizJson.toString())
                }
            }
        }
    }

    override fun getUnlockFeatureQuiz(): Pair<Quiz, List<Boolean>>? {
        return prefs.getString(UNLOCK_FEATURE_QUIZ, null)?.let {
            val quizJson = Json.parseToJsonElement(it)
            Json.decodeFromJsonElement(quizJson)
        }
    }

    override suspend fun clearUnlockFeatureQuiz() {
        withContext(Dispatchers.IO) {
            prefs.edit {
                remove(UNLOCK_FEATURE_QUIZ)
            }
        }
    }

    companion object {
        private const val SETTINGS_PREFS = "settings"
        private const val IS_UNLOCK_FEATURE_ON = "isUnlockFeatureOn"
        private const val UNLOCK_FEATURE_QUIZ = "unlockFeatureQuiz"
    }
}