package com.examhacker.quiz_solve.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.examhacker.common.data.ChatMessage
import com.examhacker.common.data.MessageAuthor
import com.examhacker.resources.R
import com.examhacker.quiz_solve.component.IAIChatComponent
import com.examhacker.resources.ColorPreset
import com.examhacker.resources.Dimensions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatBottomSheet(component: IAIChatComponent) {
    val model by component.model.subscribeAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = component::onCloseChat,
        shape = RoundedCornerShape(
            topStart = Dimensions.BottomSheetRadius,
            topEnd = Dimensions.BottomSheetRadius
        ),
        containerColor = ColorPreset.BackgroundVariant,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = Dimensions.BottomSheetDragHandleWidth,
                height = Dimensions.BottomSheetDragHandleHeight,
                shape = RoundedCornerShape(Dimensions.BottomSheetDragHandleRadius),
                color = ColorPreset.BorderDefault
            )
        },
        contentWindowInsets = { WindowInsets.safeContent },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
            shouldDismissOnClickOutside = true
        )
    ) {
        AIChatUI(
            model = model,
            sheetStateValue = sheetState.currentValue,
            onCloseChat = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    component.onCloseChat()
                }
            },
            onInputChange = component::onInputChange,
            onSendMessage = component::onSendMessage,
            onAskHint = component::onAskHint
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AIChatUI(
    model: IAIChatComponent.Model,
    sheetStateValue: SheetValue,
    onCloseChat: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onAskHint: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.ScreenPadding,
                end = Dimensions.ScreenPadding,
                bottom = Dimensions.BottomSheetBottomPadding
            )
    ) {
        ChatWithTopBar(
            messages = model.messages,
            isChatLoading = model.isChatLoading,
            isAnswerLoading = model.isAnswerLoading,
            onCloseChat = onCloseChat,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (sheetStateValue == SheetValue.PartiallyExpanded)
                        Modifier.fillMaxHeight(0.4f)
                    else
                        Modifier
                )
        )

        ChatInputWithProposals(
            input = model.messageInput,
            onInputChange = onInputChange,
            onSendMessage = onSendMessage,
            onAskHint = onAskHint,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        )
    }
}

@Composable
private fun ChatWithTopBar(
    messages: List<ChatMessage>,
    isChatLoading: Boolean,
    isAnswerLoading: Boolean,
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.AIChatSpacing),
        modifier = modifier
    ) {
        AIChatTopBar(
            onCloseChat = onCloseChat,
            modifier = Modifier.fillMaxWidth()
        )

        Chat(
            messages = messages,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.ScreenPadding)
        )
    }
}

@Composable
private fun AIChatTopBar(
    onCloseChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = ColorPreset.Secondary)) {
                    append(stringResource(R.string.chat_with))
                }
                append(" ")
                withStyle(SpanStyle(brush = ColorPreset.AIBackground)) {
                    append(stringResource(R.string.ai))
                }
            },
            fontSize = Dimensions.SubtitleFontSize,
            fontWeight = FontWeight.Bold
        )

        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "",
            tint = ColorPreset.Secondary,
            modifier = Modifier.clickable { onCloseChat() }
        )
    }
}

@Composable
private fun Chat(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.AIChatSpacing),
        modifier = modifier//.heightIn(min = Dimensions.ChatMinHeight)
    ) {
        items(messages) { message ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                MessageBox(
                    text = message.text,
                    author = message.author,
                    isProposal = message.isProposal,
                    modifier = Modifier.align(
                        alignment =
                            if (message.author == MessageAuthor.USER)
                                Alignment.CenterEnd
                            else
                                Alignment.CenterStart
                    )
                )
            }
        }
    }
}

@Composable
private fun MessageBox(
    text: String,
    author: MessageAuthor,
    isProposal: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = remember {
        if (author == MessageAuthor.USER) {
            RoundedCornerShape(
                topStart = Dimensions.MessageBoxRadius,
                bottomStart = Dimensions.MessageBoxRadius,
                bottomEnd = Dimensions.MessageBoxRadius,
                topEnd = 0.dp
            )
        } else {
            RoundedCornerShape(
                bottomStart = Dimensions.MessageBoxRadius,
                bottomEnd = Dimensions.MessageBoxRadius,
                topEnd = Dimensions.MessageBoxRadius,
                topStart = 0.dp
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .widthIn(max = Dimensions.MessageBoxMaxWidth)
            .clip(shape)
            .background(
                shape = shape,
                color =
                    if (isProposal)
                        ColorPreset.BackgroundWarningSecondary
                    else if (author == MessageAuthor.AI)
                        ColorPreset.BackgroundDefaultPrimary
                    else
                        ColorPreset.PositiveSecondary
            )
            .padding(
                vertical = Dimensions.MessageBoxVerticalPadding,
                horizontal = Dimensions.MessageBoxHorizontalPadding
            )
    ) {
        Text(
            text = text,
            fontSize = Dimensions.ChatMessageFontSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            color =
                if (isProposal)
                    ColorPreset.TextWarningTertiary
                else
                    ColorPreset.Black
        )
    }
}

@Composable
private fun ChatInputWithProposals(
    input: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onAskHint: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimensions.ChatInputSpacing),
        modifier = modifier
    ) {
        MessageProposals(onAskHint = onAskHint)

        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            shape = RoundedCornerShape(Dimensions.InputFieldRadius),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.input_field_label),
                    fontSize = Dimensions.InputLabelFontSize,
                    fontWeight = FontWeight.Normal
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_paper_plane),
                    contentDescription = "",
                    modifier = Modifier.clickable { onSendMessage() }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ColorPreset.Black,
                unfocusedTextColor = ColorPreset.Black,

                focusedBorderColor = ColorPreset.Secondary,
                unfocusedBorderColor = ColorPreset.Secondary,

                focusedPlaceholderColor = ColorPreset.Secondary,
                unfocusedPlaceholderColor = ColorPreset.Secondary,

                focusedTrailingIconColor = ColorPreset.Secondary,
                unfocusedTrailingIconColor = ColorPreset.Secondary
            )
        )
    }
}

@Composable
private fun MessageProposals(
    onAskHint: (String) -> Unit
) {
    // TODO Add multiple proposals handling

    val hintText = stringResource(R.string.hint_proposal_text)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        MessageBox(
            text = hintText,
            author = MessageAuthor.AI,
            isProposal = true,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onAskHint(hintText) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AIChatUIPreview() {
    AIChatUI(
        model = createMockData(),
        sheetStateValue = SheetValue.PartiallyExpanded,
        onCloseChat = {},
        onInputChange = {},
        onSendMessage = {},
        onAskHint = {}
    )
}

private fun createMockData(): IAIChatComponent.Model =
    IAIChatComponent.Model(
        messages = listOf(
            ChatMessage("Give me a hint \uD83D\uDCA1", MessageAuthor.USER, true),
            ChatMessage("You should think about dogs, this question is related to dogs, trust me", MessageAuthor.AI, false),
            ChatMessage("I don’t understand the idea, could you explain further?", MessageAuthor.USER, false),
            ChatMessage("The dogs are the main source of love for many humans, that’s why option 4 is correct", MessageAuthor.AI, false)
        ),
        messageInput = "",
        isChatLoading = false,
        isAnswerLoading = false
    )