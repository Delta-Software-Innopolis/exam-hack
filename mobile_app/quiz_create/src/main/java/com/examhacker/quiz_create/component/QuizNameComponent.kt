package com.examhacker.quiz_create.component

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

internal interface IQuizNameComponent {
    val model: Value<Model>

    data class Model(
        val name: String = "",
        val description: String = "",
        val nextEnabled: Boolean = false,
        val forthEnabled: Boolean = false
    )

    fun onNameChange(name: String)
    fun onDescriptionChange(description: String)
    fun onNextClick()
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun back()
}

internal class QuizNameComponent(
    componentContext: ComponentContext,
    private val updateInfo: (String, String) -> Unit,
    private val goToGenerate: () -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val goBack: () -> Unit
) : IQuizNameComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizNameComponent.Model())
    override val model: Value<IQuizNameComponent.Model> = _model

    override fun onNameChange(name: String) {
        val enabled = model.value.name.isNotEmpty()
            && model.value.name.isNotBlank()

        _model.update {
            it.copy(name = name, nextEnabled = enabled)
        }
    }

    override fun onDescriptionChange(description: String) {
        _model.update { it.copy(description = description) }
    }

    override fun onNextClick() {
        Log.d("QuizName", "Next click registered")
        if (model.value.nextEnabled) {
            updateInfo(model.value.name, model.value.description)

            _model.update {
                it.copy(forthEnabled = true)
            }
            goToGenerate()
        }
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