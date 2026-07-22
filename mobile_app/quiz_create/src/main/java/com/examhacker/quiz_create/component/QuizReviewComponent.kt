package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.utility.dialogs.AddQuestionDialogComponent
import com.examhacker.common.utility.dialogs.EditQuestionDialogComponent
import com.examhacker.common.utility.dialogs.IAddQuestionDialogComponent
import com.examhacker.common.utility.dialogs.IEditQuestionDialogComponent
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.QuestionCreate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

internal interface IQuizReviewComponent {
    val slot: Value<ChildSlot<*, Child>>
    val model: Value<Model>

    data class Model(
        val questions: List<Question> = emptyList()
    )

    sealed class Child {
        data class EditQuestionDialog(val component: IEditQuestionDialogComponent) : Child()
        data class AddQuestionDialog(val component: IAddQuestionDialogComponent) : Child()
    }

    fun onEditQuestionClick(index: Int, question: Question)
    fun onAddQuestionClick()
    fun onSaveQuizClick()
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun back()
}

internal class QuizReviewComponent(
    componentContext: ComponentContext,
    private val questions: List<Question>,
    private val saveQuiz: (List<Question>) -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val goBack: () -> Unit,
) : IQuizReviewComponent, ComponentContext by componentContext {
    
    private val _model = MutableValue(IQuizReviewComponent.Model())
    override val model = _model
    
    private val navigation = SlotNavigation<Config>()
    override val slot = childSlot(
        source = navigation,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): IQuizReviewComponent.Child =
        when(config) {
            is Config.EditQuestionDialog ->
                IQuizReviewComponent.Child.EditQuestionDialog(
                    EditQuestionDialogComponent(
                        componentContext = componentContext,
                        index = config.index,
                        question = config.question,
                        saveChanges = ::saveChangedQuestion,
                        deleteQuestion = { deleteQuestion(config.index) },
                        back = { navigation.dismiss() },
                    )
                )

            is Config.AddQuestionDialog  ->
                IQuizReviewComponent.Child.AddQuestionDialog(
                    AddQuestionDialogComponent(
                        componentContext = componentContext,
                        index = config.index,
                        addQuestion = ::addQuestion,
                        back = { navigation.dismiss() }
                    )
                )
        }

    init {
        _model.update {
            it.copy(questions = questions)
        }
    }

    override fun onEditQuestionClick(index: Int, question: Question) {
        navigation.activate(
            Config.EditQuestionDialog(index, question)
        )
    }

    override fun onAddQuestionClick() {
        navigation.activate(
            Config.AddQuestionDialog(model.value.questions.size)
        )
    }

    override fun onSaveQuizClick() {
        saveQuiz(model.value.questions)
    }

    override fun goToQuizHub() {
        toQuizHub()
    }

    override fun goToProfile() {
        toProfile()
    }

    override fun goToSettings() {
        toSettings()
    }

    override fun back() {
        goBack()
    }

    private fun saveChangedQuestion(index: Int, question: Question) {
        val newQuestions = model.value.questions.toMutableList()
        newQuestions.removeAt(index)
        newQuestions.add(index, question)

        _model.update {
            it.copy(questions = newQuestions)
        }
    }

    private fun deleteQuestion(index: Int) {
        val newQuestions = model.value.questions.toMutableList()
        newQuestions.removeAt(index)

        _model.update {
            it.copy(questions = newQuestions)
        }
    }

    private fun addQuestion(question: QuestionCreate) {}

    @Serializable
    sealed class Config {
        @Serializable
        data class EditQuestionDialog(
            val index: Int,
            @Contextual val question: Question
        ) : Config()

        @Serializable
        data class AddQuestionDialog(val index: Int) : Config()
    }
}