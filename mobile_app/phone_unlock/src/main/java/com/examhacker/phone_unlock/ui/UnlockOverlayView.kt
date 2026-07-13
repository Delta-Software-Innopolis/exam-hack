package com.examhacker.phone_unlock.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.phone_unlock.R
import com.examhacker.phone_unlock.controller.UnlockOverlayController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Deprecated("Migrated back to Compose")
class UnlockOverlayView(
    context: Context,
    private val controller: UnlockOverlayController,
    scope: CoroutineScope
) {
    private val root: View =
        LayoutInflater.from(context).inflate(R.layout.unlock_overlay_layout, null)
    private val progressText: TextView =
        root.findViewById(R.id.tvProgress)
    private val progressBar: ProgressBar =
        root.findViewById(R.id.progress)
    private val questionText: TextView =
        root.findViewById(R.id.tvQuestion)
    private val answersContainer: LinearLayout =
        root.findViewById(R.id.answersContainer)
    private val hintButton: ImageButton =
        root.findViewById(R.id.btnHint)

    init {
        hintButton.setOnClickListener {
            controller.takeHint()
        }

        scope.launch {
            controller.state.collect(::render)
        }
    }

    fun getView(): View = root

    private fun render(state: UnlockOverlayController.State) {
        progressText.text =
            "${state.solvedQuestions} / ${state.totalQuestions}"

        progressBar.max = state.totalQuestions

        progressBar.progress = state.solvedQuestions

        questionText.text =
            state.question?.description.orEmpty()

        renderAnswers(state)
    }

    private fun renderAnswers(state: UnlockOverlayController.State) {
        answersContainer.removeAllViews()

        val question = state.question ?: return
        question.variants.forEach { answer ->
            val button = createAnswerButton(answer)
            applyButtonState(button, answer, state)
            answersContainer.addView(button)
        }

        if (state.finalAnswer != null) {
            val button = createProceedButton()
            answersContainer.addView(button)
        }
    }

    private fun createAnswerButton(answer: AnswerVariant): Button {
        val button = LayoutInflater.from(root.context)
            .inflate(
                R.layout.answer_button,
                answersContainer,
                false
            ) as Button

        button.text = answer.description
        button.setOnClickListener {
            controller.submitAnswer(answer)
        }

        return button
    }

    private fun applyButtonState(
        button: Button,
        answer: AnswerVariant,
        state: UnlockOverlayController.State
    ) {
        val finalAnswer = state.finalAnswer
        button.isEnabled = finalAnswer == null

        when {
            finalAnswer == null -> {
                button.setBackgroundResource(R.drawable.bg_answer)
            }

            answer.isCorrect -> {
                button.setBackgroundResource(R.drawable.bg_answer_correct)
            }

            answer == finalAnswer -> {
                button.setBackgroundResource(R.drawable.bg_answer_wrong)
            }
        }
    }

    private fun createProceedButton(): Button {
        val button = LayoutInflater.from(root.context)
            .inflate(
                R.layout.proceed_button,
                answersContainer,
                false
            ) as Button

        button.text = "Proceed"
        button.setOnClickListener {
            controller.proceed()
        }

        return button
    }
}