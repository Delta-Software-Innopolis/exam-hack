package com.examhacker.common.utility.dialogs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.data.Question

interface IEditQuestionDialogComponent {
    val model: Value<Model>

    data class Model(
        val questionNumber: Int = 0,
        val questionDescription: String = "",
        val variants: List<AnswerVariant> = emptyList(),
        val isVariantsShown: Boolean = false
    )

    fun onDeleteQuestionClick()
    fun onQuestionDescriptionChange(newDescription: String)
    fun onVariantDescriptionChange(variantIndex: Int, newDescription: String)
    fun onVariantStatusChange(variantIndex: Int)
    fun onAddVariant()
    fun onCloseDialog()
    fun onShowVariantsClick()
}

class EditQuestionDialogComponent(
    componentContext: ComponentContext,
    index: Int,
    question: Question,
    private val saveChanges: (Int, Question) -> Unit,
    private val deleteQuestion: () -> Unit,
    private val back: () -> Unit
): IEditQuestionDialogComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IEditQuestionDialogComponent.Model())
    override val model = _model

    init {
        _model.update {
            it.copy(
                questionNumber = index + 1,
                questionDescription = question.description,
                variants = question.variants
            )
        }
    }

    override fun onDeleteQuestionClick() {
        deleteQuestion()
        back()
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

    override fun onCloseDialog() {
        saveChanges(
            model.value.questionNumber - 1,
            Question(
                model.value.questionDescription,
                model.value.variants
            )
        )
        
        back()
    }

    override fun onShowVariantsClick() {
        _model.update {
            it.copy(isVariantsShown = true)
        }
    }
}