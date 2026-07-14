package com.examhacker.profile.component

import com.arkivanov.decompose.ComponentContext

interface IProfileComponent {
    fun goToQuizHub()
    fun goToQuizList()
    fun goToSettings()
    fun logout()
}

class ProfileComponent(
    componentContext: ComponentContext,
    private val toQuizHub: () -> Unit,
    private val toQuizList: () -> Unit,
    private val toSettings: () -> Unit
) : IProfileComponent,
    ComponentContext by componentContext {

    override fun goToQuizHub() {
        toQuizHub()
    }

    override fun goToQuizList() {
        toQuizList()
    }

    override fun goToSettings() {
        toSettings()
    }

    override fun logout() {
        // TODO Implement logout
    }
}