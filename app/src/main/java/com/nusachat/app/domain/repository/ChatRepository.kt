package com.nusachat.app.domain.repository

import com.nusachat.app.domain.model.ChatMessage
import com.nusachat.app.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getConversations(): Flow<List<Conversation>>
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    fun sendMessage(conversationId: String, senderId: String, ciphertext: String)
}