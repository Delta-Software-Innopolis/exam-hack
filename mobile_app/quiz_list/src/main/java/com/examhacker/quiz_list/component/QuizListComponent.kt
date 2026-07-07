package com.examhacker.quiz_list.component

import com.arkivanov.decompose.ComponentContext

interface IQuizListComponent {

    fun goToQuizCreation()
    fun onQuizClick()

    fun goToQuizHub()

    fun goToProfile()

    fun goToSettings()

    fun back()
}

class QuizListComponent(
    componentContext: ComponentContext,
    private val toQuizInfo: () -> Unit,
    private val toQuizCreation: () -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val goBack: () -> Unit
) : IQuizListComponent,
    ComponentContext by componentContext {

    override fun goToQuizCreation() {
        toQuizCreation()
    }

    override fun onQuizClick() {
        toQuizInfo()
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
}