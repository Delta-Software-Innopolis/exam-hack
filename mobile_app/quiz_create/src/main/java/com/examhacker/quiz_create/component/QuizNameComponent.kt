package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update   // <--- ДОБАВИЛИ

interface IQuizNameComponent {
    val model: Value<Model>

    data class Model(
        val title: String = "",
        val description: String = ""
    )

    fun onTitleChange(title: String)
    fun onDescriptionChange(description: String)
    fun onNextClick()
}

class QuizNameComponent(
    componentContext: ComponentContext,
    private val onNext: () -> Unit
) : IQuizNameComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizNameComponent.Model())
    override val model: Value<IQuizNameComponent.Model> = _model

    override fun onTitleChange(title: String) {
        _model.update { it.copy(title = title) }
    }

    override fun onDescriptionChange(description: String) {
        _model.update { it.copy(description = description) }
    }

    override fun onNextClick() {
        if (model.value.title.isNotBlank()) {
            onNext.invoke()
        }
    }
}