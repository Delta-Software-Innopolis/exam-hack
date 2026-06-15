package com.examhacker.mobile.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value

import com.arkivanov.decompose.ComponentContext

class RootComponent (private val componentContext: ComponentContext)
    : ComponentContext by componentContext, IRootComponent {

    private val navigation = StackNavigation<Config>()

    sealed class Config {
        data object Initial : Config() // temporary enter point
    }

    override val stack: Value<ChildStack<*, IRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = null,
            initialConfiguration = Config.Initial,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(config: Config, componentContext: ComponentContext) :
            IRootComponent.Child {
        TODO ("Children will be implemented later")
    }
}