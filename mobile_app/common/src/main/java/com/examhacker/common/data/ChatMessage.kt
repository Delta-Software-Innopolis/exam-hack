package com.examhacker.common.data

data class ChatMessage(
    val text: String,
    val author: MessageAuthor,
    val isProposal: Boolean
)

enum class MessageAuthor {
    AI,
    USER
}