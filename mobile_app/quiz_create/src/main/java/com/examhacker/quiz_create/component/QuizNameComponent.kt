package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

internal interface IQuizNameComponent {
    val model: Value<Model>

    data class Model(
        val name: String = "",
        val description: String = ""
    )

    fun onNameChange(name: String)
    fun onDescriptionChange(description: String)
    fun isNextEnabled(): Boolean
    fun onNextClick()
}

internal class QuizNameComponent(
    componentContext: ComponentContext,
    private val goToGenerate: () -> Unit,
    private val updateName: (String) -> Unit,
    private val updateDescription: (String) -> Unit
) : IQuizNameComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizNameComponent.Model())
    override val model: Value<IQuizNameComponent.Model> = _model

    override fun onNameChange(name: String) {
        _model.update { it.copy(name = name) }
    }

    override fun onDescriptionChange(description: String) {
        _model.update { it.copy(description = description) }
    }

    override fun isNextEnabled(): Boolean {
        return model.value.name.isNotEmpty()
            && model.value.name.isNotBlank()
    }

    override fun onNextClick() {
        if (isNextEnabled()) {
            updateName(model.value.name)
            updateDescription(model.value.description)

            goToGenerate()
        }
    }
}