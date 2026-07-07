package com.examhacker.quiz_create.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.data.AnswerVariant
import com.examhacker.common.ui.DeleteButton
import com.examhacker.common.ui.dialogs.QuestionDescriptionInputField
import com.examhacker.common.ui.dialogs.TopDialogBar
import com.examhacker.common.ui.dialogs.VariantsSection
import com.examhacker.resources.R
import com.examhacker.quiz_create.component.IEditQuestionDialogComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun EditQuestionDialog(component: IEditQuestionDialogComponent) {
    val model by component.model.subscribeAsState()

    Dialog(
        onDismissRequest = { component.onCloseDialog() },
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        EditQuestionDialogUI(
            model = model,
            onCloseClick = component::onCloseDialog,
            onQuestionDescriptionChange = component::onQuestionDescriptionChange,
            onVariantDescriptionChange = component::onVariantDescriptionChange,
            onVariantStatusChange = component::onVariantStatusChange,
            onAddVariant = component::onAddVariant,
            onDeleteQuestionClick = component::onDeleteQuestionClick,
            onShowVariantsClick = component::onShowVariantsClick
        )
    }
}

@Composable
private fun EditQuestionDialogUI(
    model: IEditQuestionDialogComponent.Model,
    onCloseClick: () -> Unit,
    onQuestionDescriptionChange: (String) -> Unit,
    onVariantDescriptionChange: (Int, String) -> Unit,
    onVariantStatusChange: (Int) -> Unit,
    onAddVariant: () -> Unit,
    onDeleteQuestionClick: () -> Unit,
    onShowVariantsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(Dimensions.DialogCardRadius),
        colors = CardDefaults.cardColors(
            containerColor = ColorPreset.BackgroundVariant,
            disabledContainerColor = ColorPreset.BackgroundVariant
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.DialogVerticalElementSpacing),
            modifier = Modifier.padding(
                horizontal = Dimensions.DialogHorizontalPadding,
                vertical = Dimensions.DialogVerticalPadding
            )
        ) {
            TopDialogBar(
                title = "${stringResource(R.string.edit_dialog_title)} ${model.questionNumber}",
                onCloseClick = onCloseClick,
                modifier = Modifier.fillMaxWidth()
            )

            QuestionDescriptionInputField(
                description = model.questionDescription,
                onDescriptionChange = onQuestionDescriptionChange,
                modifier = Modifier.fillMaxWidth()
            )

            VariantsSection(
                variants = model.variants,
                isVariantsShown = model.isVariantsShown,
                onVariantDescriptionChange = onVariantDescriptionChange,
                onVariantStatusChange = onVariantStatusChange,
                onAddVariant = onAddVariant,
                onVariantsShowClick = onShowVariantsClick,
                modifier = Modifier.fillMaxWidth()
            )

            DeleteButton(
                onDeleteClick = onDeleteQuestionClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditQuestionDialogUIPreview() {
    EditQuestionDialogUI(
        model = IEditQuestionDialogComponent.Model().copy(
            questionNumber = 1,
            questionDescription = "Why do dogs think their tails are so clingy, they always want to grab it?",
            variants = listOf(
                AnswerVariant("Option 1", false),
                AnswerVariant("Option 2", true),
                AnswerVariant("Option 3", false),
                AnswerVariant("Option 4", false)
            ),
            isVariantsShown = false
        ),
        onCloseClick = {},
        onQuestionDescriptionChange = {},
        onVariantDescriptionChange = {_, _ ->},
        onVariantStatusChange = {},
        onDeleteQuestionClick = {},
        onShowVariantsClick = {},
        onAddVariant = {}
    )
}

