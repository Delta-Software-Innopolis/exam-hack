package com.examhacker.quiz_edit.component

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
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

interface IQuizEditComponent {
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
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun onCloseClick()
}

class QuizEditComponent(
    componentContext: ComponentContext,
    private val questions: List<Question>,
    private val saveQuiz: (List<Question>) -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val back: () -> Unit
) : IQuizEditComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizEditComponent.Model())
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
    ): IQuizEditComponent.Child =
        when(config) {
            is Config.EditQuestionDialog ->
                IQuizEditComponent.Child.EditQuestionDialog(
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
                IQuizEditComponent.Child.AddQuestionDialog(
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
            Config.AddQuestionDialog(model.value.questions.lastIndex)
        )
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

    override fun onCloseClick() {
        saveQuiz(model.value.questions)
        back()
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

    private fun addQuestion(question: Question) {
        val newQuestions = model.value.questions.toMutableList()
        newQuestions.add(question)

        _model.update {
            it.copy(questions = newQuestions)
        }
    }

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