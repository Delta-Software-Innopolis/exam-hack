package com.examhacker.quiz_hub.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

interface IQuizHubComponent {

    val model: Value<Model>

    data class Model(
        val searchInput: String = ""
    )

    fun onSearchInputChange(input: String)
    fun onSearchClick()
    fun goToQuizList()
    fun goToProfile()
    fun goToSettings()
}

class QuizHubComponent(
    componentContext: ComponentContext,
    private val toQuizList: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit
) : IQuizHubComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizHubComponent.Model())
    override val model = _model

    override fun onSearchInputChange(input: String) {
        _model.update {
            it.copy(searchInput = input)
        }
    }

    override fun onSearchClick() {}

    override fun goToQuizList() {
        toQuizList()
    }

    override fun goToProfile() {
        toProfile()
    }

    override fun goToSettings() {
        toSettings()
    }
}
