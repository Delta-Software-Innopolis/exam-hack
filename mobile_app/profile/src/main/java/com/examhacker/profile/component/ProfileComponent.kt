package com.examhacker.profile.component

import com.arkivanov.decompose.ComponentContext
import com.examhacker.domain.repository.IAuthenticationRepository
import com.examhacker.domain.repository.ITokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface IProfileComponent {
    fun goToQuizHub()
    fun goToQuizList()
    fun goToSettings()
    fun logout()
}

class ProfileComponent(
    componentContext: ComponentContext,
    private val authRepository: IAuthenticationRepository,
    private val tokenStorage: ITokenStorage,
    private val showErrorToast: (String) -> Unit,
    private val toQuizHub: () -> Unit,
    private val toQuizList: () -> Unit,
    private val toSettings: () -> Unit,
    private val toAuthentication: () -> Unit
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
        CoroutineScope(Dispatchers.IO).launch {
            val refreshToken = tokenStorage.getRefreshToken()

            refreshToken?.let { token ->
                authRepository.logout(token)
                    .onSuccess {
                        withContext(Dispatchers.Main) { toAuthentication() }
                    }
                    .onFailure { exception ->
                        exception.message?.let {
                            showErrorToast(it)
                        }
                    }
            }
        }
    }
}