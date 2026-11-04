package com.nusachat.app.data.local

import com.nusachat.app.domain.model.ChatMessage
import com.nusachat.app.domain.model.Conversation
import com.nusachat.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl : ChatRepository {

    private val conversations = listOf(
        Conversation(
            id = "conv-1",
            participantId = "user-b",
            participantName = "Sarah",
            participantAvatarUrl = null,
            lastMessage = "Halo, apa kabar?",
            lastMessageTimestamp = System.currentTimeMillis() - 3600000,
            unreadCount = 2
        ),
        Conversation(
            id = "conv-2",
            participantId = "user-c",
            participantName = "Budi",
            participantAvatarUrl = null,
            lastMessage = "Besok jumpai di kantor",
            lastMessageTimestamp = System.currentTimeMillis() - 7200000,
            unreadCount = 0
        ),
        Conversation(
            id = "conv-3",
            participantId = "user-d",
            participantName = "Maya",
            participantAvatarUrl = null,
            lastMessage = "File sudah saya kirim",
            lastMessageTimestamp = System.currentTimeMillis() - 10800000,
            unreadCount = 1
        )
    )

    private val messagesByConversation: Map<String, List<ChatMessage>> = mapOf(
        "conv-1" to listOf(
            ChatMessage(
                id = "msg-1",
                conversationId = "conv-1",
                senderId = "user-b",
                ciphertext = "U2FsdGVkX1+encrypted...",
                timestamp = System.currentTimeMillis() - 7200000,
                isSentByMe = false
            ),
            ChatMessage(
                id = "msg-2",
                conversationId = "conv-1",
                senderId = "user-a",
                ciphertext = "U2FsdGVkX1+encrypted...",
                timestamp = System.currentTimeMillis() - 3600000,
                isSentByMe = true
            ),
            ChatMessage(
                id = "msg-3",
                conversationId = "conv-1",
                senderId = "user-b",
                ciphertext = "U2FsdGVkX1+encrypted...",
                timestamp = System.currentTimeMillis() - 1800000,
                isSentByMe = false
            )
        ),
        "conv-2" to listOf(
            ChatMessage(
                id = "msg-4",
                conversationId = "conv-2",
                senderId = "user-c",
                ciphertext = "U2FsdGVkX1+encrypted...",
                timestamp = System.currentTimeMillis() - 14400000,
                isSentByMe = false
            )
        ),
        "conv-3" to listOf(
            ChatMessage(
                id = "msg-5",
                conversationId = "conv-3",
                senderId = "user-a",
                ciphertext = "U2FsdGVkX1+encrypted...",
                timestamp = System.currentTimeMillis() - 21600000,
                isSentByMe = true
            )
        )
    )

    override fun getConversations(): Flow<List<Conversation>> = flow { emit(conversations) }

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> =
        flow { emit(messagesByConversation[conversationId] ?: emptyList()) }

    override fun sendMessage(conversationId: String, senderId: String, ciphertext: String) {
        // Simulasi pengiriman pesan - tidak menyimpan plaintext
        println("Message sent to $conversationId by $senderId (ciphertext only)")
    }
}