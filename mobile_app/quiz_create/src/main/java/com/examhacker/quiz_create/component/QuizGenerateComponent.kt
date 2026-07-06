package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import java.io.File

internal interface IQuizGenerateComponent {
    val model: Value<Model>
    data class Model(
        val files: List<File> = emptyList()
    )

    fun onAddFileClick()
    fun onSkipClick()
    fun onGenerateClick()
    fun goBack()
}

internal class QuizGenerateComponent(
    componentContext: ComponentContext,
    private val back: () -> Unit
) : IQuizGenerateComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizGenerateComponent.Model())
    override val model = _model

    override fun onAddFileClick() {
        // TODO
    }

    override fun onSkipClick() {
        TODO("Not yet implemented")
    }

    override fun onGenerateClick() {
        TODO("Not yet implemented")
    }

    override fun goBack() {
        back()
    }
}