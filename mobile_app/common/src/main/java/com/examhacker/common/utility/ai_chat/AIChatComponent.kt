package com.examhacker.common.utility.ai_chat

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.domain.model.ChatMessage
import com.examhacker.domain.model.MessageAuthor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface IAIChatComponent {

    val model: Value<Model>

    data class Model(
        val messages: List<ChatMessage> = emptyList(),
        val messageInput: String = "",
        val isChatLoading: Boolean = false,
        val isAnswerLoading: Boolean = false
    )

    fun onInputChange(input: String)
    fun onSendMessage()
    fun onAskHint(hintText: String)
    fun onCloseChat()
}

class AIChatComponent(
    componentContext: ComponentContext,
    private val goBack: () -> Unit
) : IAIChatComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IAIChatComponent.Model())
    override val model = _model

    override fun onInputChange(input: String) {
        _model.update {
            it.copy(messageInput = input)
        }
    }

    override fun onSendMessage() {
        // TODO Use chat service to send message

        val newMessages = model.value.messages.toMutableList()
        newMessages.add(
            ChatMessage(
                text = model.value.messageInput,
                author = MessageAuthor.USER,
                isProposal = false
            )
        )

        _model.update {
            it.copy(
                messages = newMessages,
                messageInput = ""
            )
        }

        sendMockAnswerFromAI()
    }

    override fun onAskHint(hintText: String) {
        // TODO Think about multiple proposals handling

        val newMessages = model.value.messages.toMutableList()
        newMessages.add(
            ChatMessage(
                text = hintText,
                author = MessageAuthor.USER,
                isProposal = true
            )
        )

        _model.update {
            it.copy(messages = newMessages)
        }

        sendMockAnswerFromAI()
    }

    private fun sendMockAnswerFromAI() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("AI_CHAT", "Started waiting")
            delay(5_000L)

            val newMessages = model.value.messages.toMutableList()
            newMessages.add(
                ChatMessage(
                    text = "Sorry, the feature is not implemented yet",
                    author = MessageAuthor.AI,
                    isProposal = false
                )
            )

            withContext(Dispatchers.Main) {
                _model.update {
                    it.copy(messages = newMessages)
                }
            }
        }
    }

    override fun onCloseChat() {
        goBack()
    }
}