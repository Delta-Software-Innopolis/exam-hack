package com.examhacker.quiz_list.component

import com.arkivanov.decompose.ComponentContext

interface IQuizListComponent {

    fun goToQuizCreation()

    fun goToQuizHub()

    fun goToProfile()

    fun goToSettings()

    fun back()
}

class QuizListComponent(
    componentContext: ComponentContext,
    private val goToQuizCreation: () -> Unit,
    private val goToQuizHub: () -> Unit,
    private val goToProfile: () -> Unit,
    private val goToSettings: () -> Unit,
    private val goBack: () -> Unit
) : IQuizListComponent,
    ComponentContext by componentContext {

    override fun goToQuizCreation() {
        goToQuizCreation()
    }

    override fun goToQuizHub() {
        goToQuizHub()
    }

    override fun goToProfile() {
        goToProfile()
    }

    override fun goToSettings() {
        goToSettings()
    }

    override fun back() {
        goBack()
    }
}