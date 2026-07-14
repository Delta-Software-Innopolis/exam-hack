package com.examhacker.common.ui.dialogs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import com.examhacker.resources.R

@Composable
fun QuestionDescriptionInputField(
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = TextFieldValue(description),
        onValueChange = { value: TextFieldValue -> onDescriptionChange(value.text) },
        shape = RoundedCornerShape(Dimensions.DialogElementRadius),
        textStyle = TextStyle(
            fontSize = Dimensions.DescriptionInputFontSize,
            fontWeight = FontWeight.Normal
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.description_field_placeholder),
                fontSize = Dimensions.DescriptionInputFontSize,
                fontWeight = FontWeight.Normal
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ColorPreset.BackgroundVariant,
            unfocusedContainerColor = ColorPreset.BackgroundVariant,

            focusedTextColor = ColorPreset.Black,
            unfocusedTextColor = ColorPreset.Black,

            focusedBorderColor = ColorPreset.Secondary,
            unfocusedBorderColor = ColorPreset.Secondary,

            focusedPlaceholderColor = ColorPreset.TextDefaultSecondary,
            unfocusedPlaceholderColor = ColorPreset.TextDefaultSecondary
        ),
        modifier = modifier
    )
}