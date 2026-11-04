package com.nusachat.app.domain.model

data class Message(
    val id: String?,
    val conversationId: String,
    val senderId: String,
    val ciphertext: ByteArray,
    val timestamp: Long,
    val status: MessageStatus
)