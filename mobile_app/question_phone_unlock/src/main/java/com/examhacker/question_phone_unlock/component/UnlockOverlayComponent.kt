package com.examhacker.question_phone_unlock.component

import android.graphics.ColorSpace
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.utility.AnswerVariant
import com.examhacker.common.utility.Question

interface IUnlockOverlayComponent {
    val model: Value<Model>
    data class Model(
        val question: Question? = null,
//        val selectedVariant: AnswerVariant? = null,
        val finalAnswer: AnswerVariant? = null,
        val isCorrect: Boolean? = null
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
        val isCorrect = model.value.question?.variants?.filter { it.isCorrect }?.contains(answer)
            ?: return

        _model.update {
            it.copy(finalAnswer = answer, isCorrect = isCorrect)
        }
    }

    override fun takeHint() {}

    override fun back() {
        dismissOverlay()
    }
}