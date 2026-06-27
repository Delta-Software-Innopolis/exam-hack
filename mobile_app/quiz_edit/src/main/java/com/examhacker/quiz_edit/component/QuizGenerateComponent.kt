package com.examhacker.quiz_edit.component

import com.arkivanov.decompose.ComponentContext

interface IQuizGenerateComponent {
    val hasFiles: Boolean
    fun addFile()
}

class QuizGenerateComponent(
    componentContext: ComponentContext
) : IQuizGenerateComponent, ComponentContext by componentContext {

    override val hasFiles: Boolean = false

    override fun addFile() {
        // TODO
    }
}