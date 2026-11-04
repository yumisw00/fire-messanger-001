package com.nusachat.app.domain.usecase

import com.nusachat.app.domain.repository.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) {
    fun execute(conversationId: String, senderId: String, ciphertext: String) {
        repository.sendMessage(conversationId, senderId, ciphertext)
    }
}