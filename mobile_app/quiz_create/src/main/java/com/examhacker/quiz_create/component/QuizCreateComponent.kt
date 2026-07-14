package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popToFirst
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question
import com.examhacker.common.data.Quiz
import com.examhacker.common.utility.FilePicker
import kotlinx.serialization.Serializable

interface IQuizCreateComponent {
    val stack: Value<ChildStack<*, Child>>
    val model: Value<Model>

    data class Model(
        val quiz: Quiz = Quiz(0, "", "", "", emptyList())
    )

    sealed class Child {
        internal data class Name(val component: IQuizNameComponent) : Child()
        internal data class Generate(val component: IQuizGenerateComponent) : Child()
        internal data class Edit(val component: IQuizReviewComponent) : Child()
    }
}

class QuizCreateComponent(
    componentContext: ComponentContext,
    private val filePicker: FilePicker,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val back: () -> Unit
) : IQuizCreateComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizCreateComponent.Model())
    override val model = _model

    private val navigation = StackNavigation<Config>()
    override val stack: Value<ChildStack<*, IQuizCreateComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Name,
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): IQuizCreateComponent.Child =
        when(config) {
            is Config.Name ->
                IQuizCreateComponent.Child.Name(
                    QuizNameComponent(
                        componentContext = componentContext,
                        updateName = ::updateQuizName,
                        updateDescription = ::updateQuizDescription,
                        goToGenerate = ::navigateToGenerate,
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings
                    )
                )

            is Config.Generate ->
                IQuizCreateComponent.Child.Generate(
                    QuizGenerateComponent(
                        componentContext = componentContext,
                        filePicker = filePicker,
                        saveQuestions = ::updateQuestions,
                        toReview = ::navigateToReview,
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings,
                        back = ::goBack
                    )
                )

            is Config.Review ->
                IQuizCreateComponent.Child.Edit(
                    QuizReviewComponent(
                        componentContext = componentContext,
                        questions = createMockQuestions(),
                        saveQuiz = ::saveQuiz,
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings,
                        back = ::goBack
                    )
                )
        }

    private fun updateQuizName(name: String) {
        _model.update {
            it.copy(
                quiz = it.quiz.copy(name = name)
            )
        }
    }

    private fun updateQuizDescription(description: String) {
        _model.update {
            it.copy(
                quiz = it.quiz.copy(description = description)
            )
        }
    }

    private fun navigateToGenerate() {
        navigation.pushNew(Config.Generate)
    }

    private fun navigateToReview() {
        navigation.pushNew(Config.Review)
    }

    private fun navigateToQuizHub() {
        navigation.popToFirst()
        toQuizHub()
    }

    private fun navigateToProfile() {
        navigation.popToFirst()
        toProfile()
    }

    private fun navigateToSettings() {
        navigation.popToFirst()
        toSettings()
    }

    private fun goBack() {
        if (stack.items.size > 1) {
            navigation.pop()
        } else {
            back()
        }
    }

    private fun updateQuestions(questions: List<Question>) {
        _model.update {
            it.copy(
                quiz = it.quiz.copy(questions = questions)
            )
        }
    }

    private fun saveQuiz(questions: List<Question>) {
        updateQuestions(questions)
        back()
    }

    private fun createMockQuestions(): List<Question> =
        listOf(
            Question(
                description = "Why do dogs think their tails are so clingy, they always want to grab it?",
                variants = listOf(
                    AnswerVariant("Option 1", false),
                    AnswerVariant("Option 2", true),
                    AnswerVariant("Option 3", false),
                    AnswerVariant("Option 4", false)
                )
            ),
            Question(
                description = "Why something is this thing?",
                variants = listOf(
                    AnswerVariant("Option 1", false),
                    AnswerVariant("Option 2", true),
                    AnswerVariant("Option 3", false),
                    AnswerVariant("Option 4", false)
                )
            ),
            Question(
                description = "Why do dogs think their tails are so clingy, they always want to grab it?",
                variants = listOf(
                    AnswerVariant("Option 1", false),
                    AnswerVariant("Option 2", true),
                    AnswerVariant("Option 3", false),
                    AnswerVariant("Option 4", false)
                )
            )
        )

    @Serializable
    sealed class Config {
        @Serializable
        data object Name: Config()
        @Serializable
        data object Generate: Config()
        @Serializable
        data object Review: Config()
    }
}