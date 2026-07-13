package com.examhacker.common.utility.dialogs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import kotlin.collections.plus

interface IAddQuestionDialogComponent {
    val model: Value<Model>

    data class Model(
        val questionNumber: Int = 0,
        val questionDescription: String = "",
        val variants: List<AnswerVariant> = emptyList()
    )

    fun onQuestionDescriptionChange(newDescription: String)
    fun onVariantDescriptionChange(variantIndex: Int, newDescription: String)
    fun onVariantStatusChange(variantIndex: Int)
    fun onAddVariant()
    fun onAddQuestion()
    fun onCloseDialog()
}

class AddQuestionDialogComponent(
    componentContext: ComponentContext,
    index: Int,
    private val addQuestion: (Question) -> Unit,
    private val back: () -> Unit
) : IAddQuestionDialogComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IAddQuestionDialogComponent.Model())
    override val model = _model

    init {
        _model.update {
            it.copy(
                questionNumber =
                    if (index < 0)
                        1
                    else
                        index + 1
            )
        }
    }

    override fun onQuestionDescriptionChange(newDescription: String) {
        _model.update {
            it.copy(questionDescription = newDescription)
        }
    }

    override fun onVariantDescriptionChange(variantIndex: Int, newDescription: String) {
        val oldVariant = model.value.variants[variantIndex]
        val newVariants = model.value.variants.toMutableList()
        newVariants.removeAt(variantIndex)
        newVariants.add(variantIndex, oldVariant.copy(description = newDescription))

        _model.update {
            it.copy(variants = newVariants)
        }
    }

    override fun onVariantStatusChange(variantIndex: Int) {
        val oldVariant = model.value.variants[variantIndex]
        val newVariants = model.value.variants.toMutableList()
        newVariants.removeAt(variantIndex)
        newVariants.add(variantIndex, oldVariant.copy(isCorrect = !oldVariant.isCorrect))

        _model.update {
            it.copy(variants = newVariants)
        }
    }

    override fun onAddVariant() {
        _model.update {
            it.copy(variants = it.variants + AnswerVariant("", false))
        }
    }

    override fun onAddQuestion() {
        addQuestion(
            Question(
                id = 0, //TODO Remove
                description = model.value.questionDescription,
                variants = model.value.variants
            )
        )

        back()
    }

    override fun onCloseDialog() {
        back()
    }
}