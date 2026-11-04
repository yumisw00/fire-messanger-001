package com.nusachat.app.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nusachat.app.domain.model.ChatMessage
import com.nusachat.app.domain.model.Conversation
import com.nusachat.app.domain.repository.ChatRepository
import com.nusachat.app.domain.usecase.GetConversationsUseCase
import com.nusachat.app.domain.usecase.GetMessagesUseCase
import com.nusachat.app.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.Flow

class ChatViewModel(
    private val conversationRepository: ChatRepository,
    private val getConversationsUseCase: GetConversationsUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel() {

    val conversations: Flow<List<Conversation>> = getConversationsUseCase.execute()

    private var currentConversationId: String = ""

    private val _messages = mutableStateOf(emptyList<ChatMessage>())
    val messages: List<ChatMessage> = _messages.value

    fun setConversationId(conversationId: String) {
        currentConversationId = conversationId
    }

    fun sendMessage(senderId: String, ciphertext: String) {
        if (currentConversationId.isEmpty()) return
        sendMessageUseCase.execute(currentConversationId, senderId, ciphertext)
    }
}