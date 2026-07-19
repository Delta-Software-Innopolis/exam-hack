package com.examhacker.quiz_create.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.utility.FilePicker
import com.examhacker.domain.model.PickedFile
import com.examhacker.domain.model.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal interface IQuizGenerateComponent {
    val model: Value<Model>
    data class Model(
        val files: List<PickedFile> = emptyList(),
        val isGenerationInProgress: Boolean = false
    )

    fun onAddFileClick()
    fun onRemoveFileClick(file: PickedFile)
    fun onSkipClick()
    fun onGenerateClick()
    fun goToQuizHub()
    fun goToProfile()
    fun goToSettings()
    fun goBack()
}

internal class QuizGenerateComponent(
    componentContext: ComponentContext,
    private val filePicker: FilePicker,
    private val saveQuestions: (List<Question>) -> Unit,
    private val toReview: () -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val back: () -> Unit
) : IQuizGenerateComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizGenerateComponent.Model())
    override val model = _model

    override fun onAddFileClick() {
        CoroutineScope(Dispatchers.IO).launch {
            val files = filePicker.pickFiles()

            if (files.isNotEmpty()) {
                _model.update {
                    it.copy(
                        files = it.files + files
                    )
                }
            }
        }
    }

    override fun onRemoveFileClick(file: PickedFile) {
        _model.update {
            it.copy(files = it.files - file)
        }
    }

    override fun onSkipClick() {
        toReview()
    }

    override fun onGenerateClick() {
        CoroutineScope(Dispatchers.IO).launch {
            _model.update {
                it.copy(isGenerationInProgress = true)
            }

            delay(10_000L)

            _model.update {
                it.copy(isGenerationInProgress = false)
            }

            withContext(Dispatchers.Main) {
                saveQuestions(emptyList())
                toReview()
            }
        }
    }

    override fun goToQuizHub() {
        toQuizHub()
    }

    override fun goToProfile() {
        toProfile()
    }

    override fun goToSettings() {
        toSettings()
    }

    override fun goBack() {
        back()
    }
}