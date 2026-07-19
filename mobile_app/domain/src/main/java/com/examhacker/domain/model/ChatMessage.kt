package com.examhacker.domain.model

data class ChatMessage(
    val text: String,
    val author: MessageAuthor,
    val isProposal: Boolean
)

enum class MessageAuthor {
    AI,
    USER
}