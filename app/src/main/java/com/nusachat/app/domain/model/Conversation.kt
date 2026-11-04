package com.nusachat.app.domain.model

data class Conversation(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatarUrl: String?,
    val lastMessage: String?,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0
)