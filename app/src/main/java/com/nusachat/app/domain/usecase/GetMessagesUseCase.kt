package com.nusachat.app.domain.usecase

import com.nusachat.app.domain.model.ChatMessage
import com.nusachat.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(private val repository: ChatRepository) {
    fun execute(conversationId: String): Flow<List<ChatMessage>> = repository.getMessages(conversationId)
}