package com.examhacker.mobile.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.ComponentContext

import kotlinx.serialization.Serializable

import com.examhacker.authentication.component.AuthenticationComponent
import com.examhacker.authentication.component.IAuthenticationComponent

interface IRootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Authentication(
            val component: IAuthenticationComponent
        ) : Child()
    }
}

class RootComponent(private val componentContext: ComponentContext)
    : ComponentContext by componentContext, IRootComponent {

    private val navigation = StackNavigation<Config>()

    @Serializable
    sealed class Config {
        @Serializable
        data object Authentication : Config()
    }

    override val stack: Value<ChildStack<*, IRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = null,
            initialConfiguration = Config.Authentication,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: ComponentContext,)
    : IRootComponent.Child =
        when (config) {
            Config.Authentication ->
                IRootComponent.Child.Authentication(
                    AuthenticationComponent(componentContext)
                )
        }
}