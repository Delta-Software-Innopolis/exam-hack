package com.examhacker.authentication.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update

interface IAuthenticationComponent {

    data class Errors(
        val email: String? = null,
        val password: String? = null,
        val repeatedPassword: String? = null
    )
    val model: Value<Model>

    data class Model(
        val screenMode: ScreenMode = ScreenMode.REGISTER,
        val email: String = "",
        val password: String = "",
        val repeatedPassword: String = "",
        val errors: Errors = Errors()
    )

    fun switchScreenMode(mode: ScreenMode)
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
    fun onRepeatedPasswordChange(repeatedPassword: String)
    fun onSignUp()
    fun onLogin()

    fun back()
}

class AuthenticationComponent(
    componentContext: ComponentContext,
    private val goToQuizList: () -> Unit,
    private val goBack: () -> Unit
)    : IAuthenticationComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IAuthenticationComponent.Model())
    override val model: Value<IAuthenticationComponent.Model> = _model

    override fun switchScreenMode(mode: ScreenMode) {
        _model.update {
            it.copy(
                screenMode = mode,
                email = "",
                password = "",
                repeatedPassword = ""
            )
        }
    }

    override fun onEmailChange(email: String) {
        _model.update {
            it.copy(
                email = email,
                errors = it.errors.copy(email = null)
            )
        }
    }

    override fun onPasswordChange(password: String) {
        _model.update {
            it.copy(
                password = password,
                errors = it.errors.copy(password = null)
            )
        }
    }

    override fun onRepeatedPasswordChange(repeatedPassword: String) {
        _model.update {
            it.copy(
                repeatedPassword = repeatedPassword,
                errors = it.errors.copy(repeatedPassword = null)
            )
        }
    }

    override fun onSignUp() {
        val model = _model.value

        val emailError =
            if (model.email.isBlank())
                "Введите email"
            else
                null

        val passwordError =
            if (model.password.isBlank())
                "Введите пароль"
            else
                null

        val repeatedPasswordError =
            when {
                model.repeatedPassword.isBlank() ->
                    "Повторите пароль"

                model.password != model.repeatedPassword ->
                    "Пароли не совпадают"

                else ->
                    null
            }

        val errors = IAuthenticationComponent.Errors(
            email = emailError,
            password = passwordError,
            repeatedPassword = repeatedPasswordError
        )

        _model.update {
            it.copy(errors = errors)
        }

        if (emailError == null &&
            passwordError == null &&
            repeatedPasswordError == null
        ) {
            goToQuizList()
        }
    }

    override fun onLogin() {
        val model = _model.value

        val emailError =
            if (model.email.isBlank())
                "Введите email"
            else
                null

        val passwordError =
            if (model.password.isBlank())
                "Введите пароль"
            else
                null

        val errors = IAuthenticationComponent.Errors(
            email = emailError,
            password = passwordError
        )

        _model.update {
            it.copy(errors = errors)
        }

        if (emailError == null &&
            passwordError == null
        ) {
            goToQuizList()
        }
    }

    override fun back() {
        goBack()
    }
}

enum class ScreenMode {
    LOGIN,
    REGISTER,
    DEMO_END
}