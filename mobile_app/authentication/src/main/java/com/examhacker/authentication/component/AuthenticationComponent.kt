package com.examhacker.authentication.component

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.domain.repository.IAuthenticationRepository
import com.examhacker.domain.repository.ITokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.examhacker.authentication.component.IAuthenticationComponent.Errors
import kotlinx.coroutines.withContext

interface IAuthenticationComponent {

    val model: Value<Model>

    data class Model(
        val screenMode: ScreenMode = ScreenMode.REGISTER,
        val email: String = "",
        val password: String = "",
        val repeatedPassword: String = "",
        val errors: Errors = Errors()
    )

    data class Errors(
        val email: AuthError? = null,
        val password: AuthError? = null,
        val repeatedPassword: AuthError? = null,
        val general: AuthError? = null
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
    private val authRepository: IAuthenticationRepository,
    private val tokenStorage: ITokenStorage,
    private val goToQuizList: () -> Unit,
    private val goBack: () -> Unit
) : IAuthenticationComponent, ComponentContext by componentContext {

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
                AuthError.BLANK_EMAIL
            else
                null

        val passwordError =
            if (model.password.isBlank())
                AuthError.BLANK_PASSWORD
            else
                null

        val repeatedPasswordError =
            when {
                model.repeatedPassword.isBlank() ->
                    AuthError.BLANK_REPEAT_PASSWORD

                model.password != model.repeatedPassword ->
                    AuthError.NOT_MATCHING_PASSWORDS

                else ->
                    null
            }

        val errors = Errors(
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
            sendCredentialsForRegistration()
        }
    }

    override fun onLogin() {
        val model = _model.value

        val emailError =
            if (model.email.isBlank())
                AuthError.BLANK_EMAIL
            else
                null

        val passwordError =
            if (model.password.isBlank())
                AuthError.BLANK_PASSWORD
            else
                null

        val errors = Errors(
            email = emailError,
            password = passwordError
        )

        _model.update {
            it.copy(errors = errors)
        }

        if (emailError == null &&
            passwordError == null
        ) {
            sendCredentialsForLogin()
        }
    }

    override fun back() {
        goBack()
    }

    private fun sendCredentialsForRegistration() {
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.register(
                username = model.value.email,
                password = model.value.password
            )
            .onSuccess { authResponse ->
                tokenStorage.saveTokens(
                    authResponse.accessToken,
                    authResponse.refreshToken
                )

                withContext(Dispatchers.Main) { goToQuizList() }
            }
            .onFailure { exception ->
                _model.update {
                    it.copy(errors = mapExceptionToError(exception))
                }
            }
        }
    }

    private fun sendCredentialsForLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.login(
                username = model.value.email,
                password = model.value.password
            )
            .onSuccess { authResponse ->
                tokenStorage.saveTokens(
                    authResponse.accessToken,
                    authResponse.refreshToken
                )

                withContext(Dispatchers.Main) { goToQuizList() }
            }
            .onFailure { exception ->
                _model.update {
                    it.copy(errors = mapExceptionToError(exception))
                }
            }
        }
    }

    private fun mapExceptionToError(exception: Throwable): Errors {
        Log.d("Exam Auth", "Error: ${exception.message}")

        return when (exception) {
            is java.net.ConnectException -> Errors(general = AuthError.NO_INTERNET)
            is java.net.SocketTimeoutException -> Errors(general = AuthError.TIMEOUT)
            is io.ktor.client.plugins.ClientRequestException -> {
                when (exception.response.status.value) {
                    400 -> Errors(general = AuthError.INVALID_CREDENTIALS)
                    401 -> Errors(general = AuthError.AUTH_FAILED)
                    409 -> Errors(email = AuthError.EMAIL_ALREADY_REGISTERED)
                    else -> Errors(general = AuthError.SERVER_ERROR)
                }
            }
            else -> Errors(general = AuthError.UNKNOWN_ERROR)
        }
    }
}

enum class ScreenMode {
    LOGIN,
    REGISTER
}

enum class AuthError {
    BLANK_EMAIL,
    BLANK_PASSWORD,
    BLANK_REPEAT_PASSWORD,
    NOT_MATCHING_PASSWORDS,
    NO_INTERNET,
    TIMEOUT,
    INVALID_CREDENTIALS,
    AUTH_FAILED,
    EMAIL_ALREADY_REGISTERED,
    SERVER_ERROR,
    UNKNOWN_ERROR
}