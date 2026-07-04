package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext

internal interface IQuizGenerateComponent {
    val hasFiles: Boolean
    fun addFile()
}

internal class QuizGenerateComponent(
    componentContext: ComponentContext
) : IQuizGenerateComponent, ComponentContext by componentContext {

    override val hasFiles: Boolean = false

    override fun addFile() {
        // TODO
    }
}