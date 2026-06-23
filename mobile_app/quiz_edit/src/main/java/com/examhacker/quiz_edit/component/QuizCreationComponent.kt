package com.examhacker.quiz_edit.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

interface IQuizCreationComponent {
    val childStack: Value<ChildStack>

    sealed class ChildStack {
        data class Name(val component: IQuizNameComponent) : ChildStack()
        data class Generate(val component: IQuizGenerateComponent) : ChildStack()
        data class Edit(val component: IQuizEditComponent) : ChildStack()
    }

    fun goBack()
    fun navigateToEdit()
}

class QuizCreationComponent(
    componentContext: ComponentContext,
    private val onFinish: () -> Unit
) : IQuizCreationComponent, ComponentContext by componentContext {

    // Текущий экран (храним вручную)
    private var currentScreen: Screen = Screen.NAME

    // Создаём компоненты
    private val nameComponent = QuizNameComponent(
        componentContext,
        onNext = { navigateToGenerate() }
    )

    private val generateComponent = QuizGenerateComponent(componentContext)
    private val editComponent = QuizEditComponent(componentContext)

    // Стек дочерних компонентов (активный компонент)
    private val _childStack = MutableValue<IQuizCreationComponent.ChildStack>(
        IQuizCreationComponent.ChildStack.Name(nameComponent)
    )
    override val childStack: Value<IQuizCreationComponent.ChildStack> = _childStack

    private enum class Screen {
        NAME, GENERATE, EDIT
    }

    private fun navigateToGenerate() {
        currentScreen = Screen.GENERATE
        _childStack.value = IQuizCreationComponent.ChildStack.Generate(generateComponent)
    }

    override fun navigateToEdit() {
        currentScreen = Screen.EDIT
        _childStack.value = IQuizCreationComponent.ChildStack.Edit(editComponent)
    }

    override fun goBack() {
        when (currentScreen) {
            Screen.NAME -> { /* Ничего не делаем, это первый экран */ }
            Screen.GENERATE -> {
                currentScreen = Screen.NAME
                _childStack.value = IQuizCreationComponent.ChildStack.Name(nameComponent)
            }
            Screen.EDIT -> {
                currentScreen = Screen.GENERATE
                _childStack.value = IQuizCreationComponent.ChildStack.Generate(generateComponent)
            }
        }
    }
}