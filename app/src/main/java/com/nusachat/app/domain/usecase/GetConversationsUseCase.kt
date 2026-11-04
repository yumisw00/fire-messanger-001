package com.nusachat.app.domain.usecase

import com.nusachat.app.domain.model.Conversation
import com.nusachat.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetConversationsUseCase(private val repository: ChatRepository) {
    fun execute(): Flow<List<Conversation>> = repository.getConversations()
}