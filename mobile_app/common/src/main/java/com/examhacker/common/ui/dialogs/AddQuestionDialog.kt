package com.examhacker.common.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.examhacker.resources.R
import com.examhacker.common.utility.dialogs.IAddQuestionDialogComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun AddQuestionDialog(component: IAddQuestionDialogComponent) {
    val model by component.model.subscribeAsState()

    Dialog(
        onDismissRequest = { component.onCloseDialog() },
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        AddQuestionDialogUI(
            model = model,
            onCloseClick = component::onCloseDialog,
            onQuestionDescriptionChange = component::onQuestionDescriptionChange,
            onVariantDescriptionChange = component::onVariantDescriptionChange,
            onVariantStatusChange = component::onVariantStatusChange,
            onAddVariantClick = component::onAddVariant,
            onAddQuestionClick = component::onAddQuestion
        )
    }
}

@Composable
private fun AddQuestionDialogUI(
    model: IAddQuestionDialogComponent.Model,
    onCloseClick: () -> Unit,
    onQuestionDescriptionChange: (String) -> Unit,
    onVariantDescriptionChange: (Int, String) -> Unit,
    onVariantStatusChange: (Int) -> Unit,
    onAddVariantClick: () -> Unit,
    onAddQuestionClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(Dimensions.DefaultCardRadius),
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
                title = "${stringResource(R.string.add_dialog_title)} ${model.questionNumber}",
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
                onVariantDescriptionChange = onVariantDescriptionChange,
                onVariantStatusChange = onVariantStatusChange,
                onAddVariant = onAddVariantClick,
                modifier = Modifier.fillMaxWidth()
            )

            AddQuestionButton(
                onAddQuestionClick = onAddQuestionClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddQuestionButton(
    onAddQuestionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onAddQuestionClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.PositivePrimary,
            contentColor = ColorPreset.BackgroundVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add_plus),
                contentDescription = "",
                modifier = Modifier.size(Dimensions.CheckBoxIconSize)
            )

            Text(
                text = stringResource(R.string.add_question_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddQuestionDialogUIPreview() {
    AddQuestionDialogUI(
        model = IAddQuestionDialogComponent.Model(),
        onCloseClick = {},
        onQuestionDescriptionChange = {},
        onVariantDescriptionChange = {_, _ ->},
        onVariantStatusChange = {},
        onAddVariantClick = {},
        onAddQuestionClick = {}
    )
}