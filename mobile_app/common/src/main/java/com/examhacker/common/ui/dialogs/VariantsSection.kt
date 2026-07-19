package com.examhacker.common.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.resources.R
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
fun VariantsSection(
    variants: List<AnswerVariant>,
    onVariantDescriptionChange: (Int, String) -> Unit,
    onVariantStatusChange: (Int) -> Unit,
    onAddVariant: () -> Unit,
    modifier: Modifier = Modifier,
    isVariantsShown: Boolean = true,
    onVariantsShowClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.DialogVerticalElementSpacing),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.variants_section_title),
                fontSize = Dimensions.DialogTitleFontSize,
                fontWeight = FontWeight.Bold,
                color = ColorPreset.SecondaryDimm
            )

            if (isVariantsShown) {
                AddVariantButton(
                    onAddVariant = onAddVariant
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
        }

        if (isVariantsShown) {
            VariantsList(
                variants = variants,
                onVariantDescriptionChange = onVariantDescriptionChange,
                onVariantStatusChange = onVariantStatusChange,
                onAddVariant = onAddVariant,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            VariantsCover(
                onVariantsShowClick = onVariantsShowClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VariantsList(
    variants: List<AnswerVariant>,
    onVariantDescriptionChange: (Int, String) -> Unit,
    onVariantStatusChange: (Int) -> Unit,
    onAddVariant: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimensions.DialogVerticalElementSpacing),
        modifier = modifier
    ) {
        itemsIndexed(variants) { index, variant ->
            VariantCard(
                variant = variant,
                onVariantDescriptionChange = { description -> onVariantDescriptionChange(index, description) },
                onVariantStatusChange = { onVariantStatusChange(index) },
                modifier = Modifier.fillMaxWidth()
            )
        }

//        item {
//            AddVariantButton(
//                onAddVariant = onAddVariant
//            )
//        }
    }
}

@Composable
private fun VariantsCover(
    onVariantsShowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderStroke = remember {
        Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(Dimensions.VariantsPlaceholderHeight)
            .background(
                color = ColorPreset.BackgroundDefaultSecondary,
                shape = RoundedCornerShape(Dimensions.DialogElementRadius)
            )
            .drawBehind {
            drawRoundRect(
                color = ColorPreset.Secondary,
                style = borderStroke,
                cornerRadius = CornerRadius(18f, 18f)
            )
        }
    ) {
        OutlinedButton(
            onClick = onVariantsShowClick,
            shape = RoundedCornerShape(Dimensions.ButtonRadius),
            contentPadding = PaddingValues(Dimensions.ScreenPadding),
            border = BorderStroke(
                width = Dimensions.DefaultBorderWidth,
                color = ColorPreset.SecondaryDimm
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ColorPreset.BackgroundVariant,
                disabledContainerColor = ColorPreset.BackgroundVariant,

                contentColor = ColorPreset.SecondaryDimm,
                disabledContentColor = ColorPreset.SecondaryDimm
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_eye),
                    contentDescription = ""
                )

                Text(
                    text = stringResource(R.string.show_variants_button_label),
                    fontSize = Dimensions.ButtonLabelFontSize,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun VariantCard(
    variant: AnswerVariant,
    onVariantDescriptionChange: (String) -> Unit,
    onVariantStatusChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.DialogElementRadius),
        colors = CardDefaults.cardColors(
            containerColor = ColorPreset.BackgroundDefaultPrimary,
            disabledContainerColor = ColorPreset.BackgroundDefaultPrimary
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.DialogVerticalElementSpacing),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = variant.description,
                onValueChange = onVariantDescriptionChange,
                textStyle = TextStyle(
                    fontSize = Dimensions.DescriptionInputFontSize,
                    fontWeight = FontWeight.Normal
                ),
                decorationBox = { innerTextField ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (variant.description.isEmpty()) {
                            Text(
                                text = stringResource(R.string.variant_description_field_placeholder),
                                fontSize = Dimensions.DescriptionInputFontSize,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    innerTextField()
                }
            )

            CheckBox(
                isChecked = variant.isCorrect,
                onCheck = onVariantStatusChange
            )
        }
    }
}

@Composable
private fun CheckBox(
    isChecked: Boolean,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                color = ColorPreset.BackgroundVariant,
                shape = RoundedCornerShape(Dimensions.DialogElementRadius)
            )
            .border(
                color = ColorPreset.Secondary,
                width = Dimensions.DefaultBorderWidth,
                shape = RoundedCornerShape(Dimensions.DialogElementRadius)
            )
            .clickable { onCheck() }
            .padding(Dimensions.CheckBoxPadding)
    ) {
        Icon(
            painter =
                if (isChecked)
                    painterResource(R.drawable.ic_check)
                else
                    painterResource(R.drawable.ic_cross),
            contentDescription = "",
            modifier = Modifier.size(Dimensions.CheckBoxIconSize),
            tint =
                if (isChecked)
                    ColorPreset.PositivePrimary
                else
                    ColorPreset.ErrorPrimary
        )
    }
}

@Composable
private fun AddVariantButton(
    onAddVariant: () -> Unit,
    modifier: Modifier = Modifier
) {
//    Row(
//        modifier = modifier.clickable { onAddVariant() },
//        horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing, Alignment.Start),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            painter = painterResource(R.drawable.ic_add_plus),
//            contentDescription = "",
//            tint = ColorPreset.SecondaryDimm
//        )
//
//        Text(
//            text = stringResource(R.string.add_variant_button_label),
//            color = ColorPreset.SecondaryDimm,
//            fontSize = Dimensions.ButtonLabelFontSize,
//            fontWeight = FontWeight.Normal
//        )
//    }

    OutlinedButton(
        onClick = onAddVariant,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.DialogElementRadius),
        contentPadding = PaddingValues(horizontal = Dimensions.AddVariantButtonHorizontalPadding),
        border = BorderStroke(
            color = ColorPreset.Secondary,
            width = Dimensions.DefaultBorderWidth
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPreset.BackgroundVariant,
            contentColor = ColorPreset.SecondaryDimm
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add_plus),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.add_variant_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Normal
            )
        }
    }
}