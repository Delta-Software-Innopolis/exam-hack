package com.examhacker.authentication.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

interface IAuthenticationComponent {
    val model: Value<Model>

    data class Model(
        val screenMode: ScreenMode = ScreenMode.REGISTER,
        val email: String = "",
        val password: String = "",
        val repeatedPassword: String = ""
    )

    fun switchModeToLogin()
    fun switchModeToRegister()
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
    fun onRepeatedPasswordChange(repeatedPassword: String)
    fun onSignUp()
    fun onLogin()
}

class AuthenticationComponent(componentContext: ComponentContext)
    : IAuthenticationComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IAuthenticationComponent.Model())
    override val model: Value<IAuthenticationComponent.Model> = _model

    override fun switchModeToLogin() {
        _model.update {
            it.copy(
                screenMode = ScreenMode.LOGIN,
                email = "",
                password = "",
                repeatedPassword = ""
            )
        }
    }

    override fun switchModeToRegister() {
        _model.update {
            it.copy(
                screenMode = ScreenMode.REGISTER,
                email = "",
                password = "",
                repeatedPassword = ""
            )
        }
    }

    override fun onEmailChange(email: String) {
        _model.update {
            it.copy(email = email)
        }
    }

    override fun onPasswordChange(password: String) {
        _model.update {
            it.copy(password = password)
        }
    }

    override fun onRepeatedPasswordChange(repeatedPassword: String) {
        _model.update {
            it.copy(repeatedPassword = repeatedPassword)
        }
    }

    override fun onSignUp() {

    }

    override fun onLogin() {

    }


}

enum class ScreenMode {
    LOGIN,
    REGISTER,
    DONE
}