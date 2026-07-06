package com.examhacker.quiz_create.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.examhacker.resources.R
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.data.PickedFile
import com.examhacker.common.ui.AppNavigationBar
import com.examhacker.common.ui.NavigationTab
import com.examhacker.quiz_create.component.IQuizGenerateComponent
import com.examhacker.quiz_create.ui.common.CreationStage
import com.examhacker.quiz_create.ui.common.QuizCreationTopBar
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions

@Composable
internal fun QuizGenerateScreen(component: IQuizGenerateComponent) {
    val model by component.model.subscribeAsState()

    QuizGenerateUI(
        model = model,
        onAddFileClick = component::onAddFileClick,
        onRemoveFileClick = component::onRemoveFileClick,
        onSkipClick = component::onSkipClick,
        onGenerateClick = component::onGenerateClick,
        goBack = component::goBack
    )
}

@Composable
private fun QuizGenerateUI(
    model: IQuizGenerateComponent.Model,
    onAddFileClick: () -> Unit,
    onRemoveFileClick: (PickedFile) -> Unit,
    onSkipClick: () -> Unit,
    onGenerateClick: () -> Unit,
    goBack: () -> Unit
) {
    Scaffold(
        topBar = {
            QuizCreationTopBar(
                creationStage = CreationStage.AI_GENERATION,
                onBackClick = {},
                onForthClick = {},
                isForthEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        bottomBar = {
            AppNavigationBar(
                selectedTab = NavigationTab.QUIZ_LIST,
                onQuizListClick = {},
                onQuizHubClick = {},
                onProfileClick = {},
                onSettingsClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        containerColor = ColorPreset.BackgroundVariant
    ) { contentPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Dimensions.ScreenPadding)
        ) {
            ScreenTitleWithInstruction(Modifier.fillMaxWidth())

            FileInputWithButtons(
                files = model.files,
                onAddFileClick = onAddFileClick,
                onRemoveFileClick = onRemoveFileClick,
                onSkipClick = onSkipClick,
                onGenerateClick = onGenerateClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (model.isGenerationInProgress) {
        GenerateProgressDialog()
    }

    BackHandler { goBack() }
}

@Composable
private fun ScreenTitleWithInstruction(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.ScreenPadding)
    ) {
        AIGenerationScreenTitle(Modifier.fillMaxWidth())

        AIGenerationInstruction(Modifier.fillMaxWidth())
    }
}

@Composable
private fun FileInputWithButtons(
    files: List<PickedFile>,
    onAddFileClick: () -> Unit,
    onRemoveFileClick: (PickedFile) -> Unit,
    onSkipClick: () -> Unit,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.InputButtonSpacing),
        modifier = modifier
    ) {
        FileInput(
            files = files,
            onAddFileClick = onAddFileClick,
            onRemoveFileClick = onRemoveFileClick,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.GenerationButtonSpacing)
        ) {
            DefaultOutlinedButton(
                onClick = onSkipClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.manual_creation_button_label),
                    fontSize = Dimensions.ButtonLabelFontSize,
                    fontWeight = FontWeight.Normal
                )
            }

            AIGenerateButton(
                onClick = onGenerateClick,
                enabled = false,
                modifier = Modifier.weight(2.5f)
            )
        }
    }
}

@Composable
private fun FileInput(
    files: List<PickedFile>,
    onAddFileClick: () -> Unit,
    onRemoveFileClick: (PickedFile) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimensions.FileInputElementSpacing),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.file_input_label),
            fontSize = Dimensions.InputLabelFontSize,
            fontWeight = FontWeight.Bold,
            color = ColorPreset.Black,
            textAlign = TextAlign.Start
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.InputFieldRadius),
            border = BorderStroke(
                width = Dimensions.DefaultBorderWidth,
                color = ColorPreset.BorderDefault
            ),
            colors = CardDefaults.outlinedCardColors(
                containerColor = ColorPreset.BackgroundVariant,
                disabledContainerColor = ColorPreset.BackgroundVariant
            )
        ) {
            if (files.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = Dimensions.FileInputFieldMinHeight)
                ) {
                    Text(
                        text = stringResource(R.string.file_input_placeholder),
                        fontSize = Dimensions.InputLabelFontSize,
                        fontWeight = FontWeight.Normal,
                        color = ColorPreset.TextDefaultSecondary
                    )
                }
            } else {
                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.FileListSpacing),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = Dimensions.FileInputFieldMinHeight)
                        .padding(Dimensions.ScreenPadding)
                ) {
                    items(files) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = ColorPreset.BorderDangerTertiary)) {
                                    append("X")
                                }
                                append("  ")
                                withStyle(SpanStyle(color = ColorPreset.TextDefaultSecondary)) {
                                    append(it.name)
                                }
                            },
                            fontSize = Dimensions.InputLabelFontSize,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { onRemoveFileClick(it) }
                        )
                    }
                }
            }
        }

        DefaultOutlinedButton(
            onClick = onAddFileClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.FileInputElementSpacing),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_file_add),
                    contentDescription = "",
                )

                Text(
                    text = stringResource(R.string.add_file_button_label),
                    fontSize = Dimensions.ButtonLabelFontSize,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun AIGenerationScreenTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = ColorPreset.Black)) {
                    append(stringResource(R.string.use))
                }
                append(" ")
                withStyle(SpanStyle(brush = ColorPreset.AIBackground)) {
                    append(stringResource(R.string.ai))
                }
            },
            fontSize = Dimensions.ScreenTitleFontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )

        Text(
            text = stringResource(R.string.quiz_generate_title_lower_part),
            fontSize = Dimensions.AIGenerateTitleLowerFontSize,
            fontWeight = FontWeight.Bold,
            color = ColorPreset.Black,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun AIGenerationInstruction(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.quiz_generate_instruction_first)
            + "\n" + stringResource(R.string.quiz_generate_instruction_second)
            + "\n" + stringResource(R.string.quiz_generate_instruction_third),
        fontSize = Dimensions.GenerateInstructionFontSize,
        fontWeight = FontWeight.Normal,
        color = ColorPreset.TextDefaultSecondary,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

@Composable
private fun DefaultOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        border = BorderStroke(
            Dimensions.DefaultBorderWidth,
            ColorPreset.BorderDefault
        ),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ColorPreset.SecondaryDimm,
            disabledContentColor = ColorPreset.SecondaryDimm,
            containerColor = ColorPreset.Transparent,
            disabledContainerColor = ColorPreset.Transparent
        ),
        content = content
    )
}

@Composable
private fun AIGenerateButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val notEnabledStroke = Stroke(
        width = 6f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.then(
            if (!enabled)
                Modifier.drawBehind {
                    drawRoundRect(
                        brush = ColorPreset.AIBackground,
                        style = notEnabledStroke,
                        cornerRadius = CornerRadius(
                            40f,
                            40f
                        )
                    )
                }
            else
                Modifier
        ),
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.ButtonRadius),
        border =
            if (enabled) 
                BorderStroke(
                    width = Dimensions.ThickBorderWidth,
                    brush = ColorPreset.AIBackground
                )
            else
                null,
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ColorPreset.Transparent,
            disabledContainerColor = ColorPreset.Transparent,
            contentColor = ColorPreset.Black,
            disabledContentColor = ColorPreset.Secondary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.ButtonContentSpacing)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = ""
            )

            Text(
                text = stringResource(R.string.ai_generation_button_label),
                fontSize = Dimensions.ButtonLabelFontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    device = Devices.DEFAULT,
    showBackground = true
)
@Composable
private fun QuizGenerateScreenPreview() {
    QuizGenerateUI(
        model = IQuizGenerateComponent.Model(),
        onAddFileClick = {},
        onRemoveFileClick = {},
        onSkipClick = {},
        onGenerateClick = {},
        goBack = {}
    )
}