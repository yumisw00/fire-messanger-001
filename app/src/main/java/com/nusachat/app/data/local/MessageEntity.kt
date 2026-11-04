package com.nusachat.app.data.local

data class MessageEntity(
    val id: String?,
    val conversationId: String,
    val senderId: String,
    val ciphertext: ByteArray,
    val timestamp: Long,
    val status: String
)

data class ConversationEntity(
    val id: String?,
    val participantIds: String,
    val lastMessageAt: Long
)