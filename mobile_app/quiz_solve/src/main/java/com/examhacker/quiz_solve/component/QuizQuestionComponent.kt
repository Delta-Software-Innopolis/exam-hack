package com.examhacker.quiz_solve.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface IQuizQuestionComponent {

    val slot: Value<ChildSlot<*, Child>>
    val model: Value<Model>

    data class Model(
        val questions: List<Question> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val answers: List<AnswerVariant?> = emptyList(),
        val nextEnabled: Boolean = false,
        val previousEnabled: Boolean = false
    )

    sealed class Child {
        data class AIChat(val component: IAIChatComponent) : Child()
    }

    fun back()
    fun goToPreviousQuestion()
    fun goToNextQuestion()
    fun answerCurrentQuestion(answer: AnswerVariant)
    fun openAiChat()
}

class QuizQuestionComponent(
    componentContext: ComponentContext,
    questions: List<Question>,
    private val goToResults: () -> Unit,
    private val updateResults: (Int) -> Unit,
    private val goBack: () -> Unit
) : IQuizQuestionComponent, ComponentContext by componentContext {

    private val navigation = SlotNavigation<Config>()
    override val slot: Value<ChildSlot<*, IQuizQuestionComponent.Child>> =
        childSlot(
            source = navigation,
            serializer = Config.serializer(),
            handleBackButton = false,
            childFactory = ::createChild
        )

    private val _model = MutableValue(createMockData())//MutableValue(IQuizQuestionComponent.Model())
    override val model = _model

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): IQuizQuestionComponent.Child.AIChat =
        when(config) {
            is Config.AIChat ->
                IQuizQuestionComponent.Child.AIChat(
                    AIChatComponent(
                        componentContext = componentContext,
                        goBack = ::dismissAIChat
                    )
                )
        }

    init {
        _model.update {
            it.copy(
                questions = questions,
                currentQuestionIndex = 0,
                answers = List(questions.size) { null },
                nextEnabled = isNextEnabled(0),
                previousEnabled = isPreviousEnabled(0)
            )
        }
    }

    override fun goToPreviousQuestion() {
        if (model.value.previousEnabled) {
            _model.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex - 1,
                    nextEnabled = isNextEnabled(it.currentQuestionIndex - 1),
                    previousEnabled = isPreviousEnabled(it.currentQuestionIndex - 1)
                )
            }
        }
    }

    override fun goToNextQuestion() {
        if (model.value.nextEnabled) {
            _model.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    nextEnabled = isNextEnabled(it.currentQuestionIndex + 1),
                    previousEnabled = isPreviousEnabled(it.currentQuestionIndex + 1)
                )
            }
        }
    }

    override fun answerCurrentQuestion(answer: AnswerVariant) {
        val updatedAnswers = model.value.answers.toMutableList()
        updatedAnswers.add(model.value.currentQuestionIndex, answer)

        _model.update {
            it.copy(answers = updatedAnswers)
        }

        if (isAllQuestionsAnswered()) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(1_000L)

                updateResults(
                    model.value.answers.count { it?.isCorrect == true }
                )
                goToResults()
            }
        }
    }

    override fun openAiChat() {
        navigation.activate(Config.AIChat)
    }

    override fun back() {
        goBack()
    }

    private fun isNextEnabled(index: Int): Boolean {
        return index < model.value.questions.lastIndex
            && index != -1
    }

    private fun isPreviousEnabled(index: Int): Boolean {
        return index > 0
    }

    private fun isAllQuestionsAnswered(): Boolean {
        return model.value.answers.count { it != null } ==
            model.value.questions.size
    }

    private fun dismissAIChat() {
        navigation.dismiss()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object AIChat : Config()
    }
    
    private fun createMockData(): IQuizQuestionComponent.Model {
        val question1 = Question(
            id = 1,
            description = "Question description, may span several lines, we’ll discuss the font size and boldness later",
            variants = listOf(
                AnswerVariant("Option 1, option description", false),
                AnswerVariant("Option 2, description, maybe a correct answer", false),
                AnswerVariant("Option 3, description, choose wisely", false),
                AnswerVariant("Option 4, idk which is correct, really", true)
            )
        )
        
        val question2 = Question(
            id = 2,
            description = "Why do dogs think their tails are so clingy, they always want to grab it?",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", false),
                AnswerVariant("Option 3", true),
                AnswerVariant("Option 4", false)
            )
        )

        return IQuizQuestionComponent.Model(
            questions = listOf(question1, question2),
            currentQuestionIndex = 0,
            answers = emptyList(),
            nextEnabled = true,
            previousEnabled = false
        )
    }
}