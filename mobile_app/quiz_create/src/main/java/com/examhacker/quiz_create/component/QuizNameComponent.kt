package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update   // <--- ДОБАВИЛИ

internal interface IQuizNameComponent {
    val model: Value<Model>

    data class Model(
        val name: String = "",
        val description: String = ""
    )

    fun onNameChange(name: String)
    fun onDescriptionChange(description: String)
    fun onNextClick()
}

internal class QuizNameComponent(
    componentContext: ComponentContext,
    private val onNext: () -> Unit
) : IQuizNameComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizNameComponent.Model())
    override val model: Value<IQuizNameComponent.Model> = _model

    override fun onNameChange(name: String) {
        _model.update { it.copy(name = name) }
    }

    override fun onDescriptionChange(description: String) {
        _model.update { it.copy(description = description) }
    }

    override fun onNextClick() {
        if (model.value.name.isNotBlank()) {
            onNext()
        }
    }
}