package com.examhacker.settings.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

interface ISettingsComponent {
    val model: Value<Model>

    data class Model(
        val isPhoneUnlockFeatureOn: Boolean = false,
        val isEnglishLanguage: Boolean = true,
        val isLightTheme: Boolean = true
    )

    fun onPhoneUnlockFeatureToggle()
    fun onLanguageToggle()
    fun onThemeToggle()
    fun toProfile()
    fun toQuizList()
    fun toQuizHub()
}

class SettingsComponent(
    componentContext: ComponentContext,
    private val goToQuizList: () -> Unit,
    private val goToProfile: () -> Unit,
    private val goToQuizHub: () -> Unit
) : ISettingsComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ISettingsComponent.Model())
    override val model = _model

    override fun onPhoneUnlockFeatureToggle() {
        _model.update {
            it.copy(isPhoneUnlockFeatureOn = !it.isPhoneUnlockFeatureOn)
        }
    }

    override fun onLanguageToggle() {
        _model.update {
            it.copy(isEnglishLanguage = !it.isEnglishLanguage)
        }
    }

    override fun onThemeToggle() {
        _model.update {
            it.copy(isLightTheme = !it.isLightTheme)
        }
    }

    override fun toProfile() {
        goToProfile()
    }

    override fun toQuizList() {
        goToQuizList()
    }

    override fun toQuizHub() {
        goToQuizHub()
    }
}