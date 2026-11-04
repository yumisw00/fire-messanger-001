package com.nusachat.app.domain.model

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val ciphertext: String,
    val timestamp: Long,
    val isSentByMe: Boolean,
    val status: MessageStatus = MessageStatus.SENT
)