package com.examhacker.phone_unlock.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.utility.AnswerVariant
import com.examhacker.common.utility.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface IUnlockOverlayComponent {
    val model: Value<Model>
    data class Model(
        val question: Question? = null,
        val finalAnswer: AnswerVariant? = null
    )

    fun submitAnswer(answer: AnswerVariant)
    fun takeHint()
    fun back()
}

class UnlockOverlayComponent(
    componentContext: ComponentContext,
    private val dismissOverlay: () -> Unit
) : IUnlockOverlayComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IUnlockOverlayComponent.Model())
    override val model: Value<IUnlockOverlayComponent.Model> = _model

    override fun submitAnswer(answer: AnswerVariant) {
        _model.update {
            it.copy(finalAnswer = answer)
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Dispatchers.Main) { dismissOverlay() }
        }
    }

    override fun takeHint() {}

    override fun back() {
        dismissOverlay()
    }
}