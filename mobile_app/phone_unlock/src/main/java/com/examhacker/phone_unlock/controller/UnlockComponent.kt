package com.examhacker.phone_unlock.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.utility.ISettingStorage
import com.examhacker.common.utility.ai_chat.AIChatComponent
import com.examhacker.common.utility.ai_chat.IAIChatComponent
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface IUnlockComponent {

    val slot: Value<ChildSlot<*, Child>>
    val model: Value<Model>

    data class Model(
        val question: Question? = null,
        val finalAnswer: AnswerVariant? = null,
        val solvedQuestions: Int = 0,
        val totalQuestions: Int = 0
    )

    sealed class Child {
        data class AIChat(val component: IAIChatComponent) : Child()
    }

    fun onAnswerClick(index: Int)
    fun onHintClick()
    fun onProceedClick()
}

class UnlockComponent(
    componentContext: ComponentContext,
    private val settingStorage: ISettingStorage,
    private val onCloseQuestion: () -> Unit
) : IUnlockComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IUnlockComponent.Model())
    override val model = _model

    private val navigation = SlotNavigation<Config>()
    override val slot = childSlot(
        source = navigation,
        serializer = Config.serializer(),
        handleBackButton = false,
        childFactory = ::createChild
    )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): IUnlockComponent.Child =
        when(config) {
            is Config.AIChat -> IUnlockComponent.Child.AIChat(
                AIChatComponent(
                    componentContext = componentContext,
                    goBack = ::dismissChildSlot
                )
            )
        }

    init {
        initializeQuestionData()
    }

    override fun onAnswerClick(index: Int) {
        model.value.question?.let { question ->
            _model.update {
                it.copy(
                    finalAnswer = question.variants[index],
                    solvedQuestions = it.solvedQuestions + 1
                )
            }
        }
    }

    override fun onHintClick() {
        navigation.activate(Config.AIChat)
    }

    override fun onProceedClick() {
        CoroutineScope(Dispatchers.Main).launch {
            model.value.question?.let {
                settingStorage.updateQuestionAnsweredStatus(it.id)
            }

            onCloseQuestion()
        }
    }

    private fun dismissChildSlot() {
        navigation.dismiss()
    }

    private fun initializeQuestionData() {
        val quizWithAnswers = settingStorage.getUnlockFeatureQuiz()

        quizWithAnswers?.let { (quiz, answered) ->

            val question = quiz.questions
                .filterIndexed { index, _ -> !answered[index] }
                .firstOrNull()

            _model.update { state ->
                state.copy(
                    question = question,
                    totalQuestions = quiz.questions.size,
                    solvedQuestions = answered.count { it }
                )
            }
        }
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object AIChat : Config()
    }
}