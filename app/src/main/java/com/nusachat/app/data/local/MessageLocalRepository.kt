package com.nusachat.app.data.local

import com.nusachat.app.domain.model.Message
import com.nusachat.app.domain.model.MessageStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessageLocalRepository(private val messageDao: MessageDao) {

    suspend fun insert(message: Message): String {
        return withContext(Dispatchers.IO) {
            val entity = MessageMapper.toEntity(message)
            messageDao.insert(entity)
        }
    }

    suspend fun insertBatch(messages: List<Message>): List<String> {
        return withContext(Dispatchers.IO) {
            messages.map { insert(it) }
        }
    }

    suspend fun getByConversation(conversationId: String): List<Message> {
        return withContext(Dispatchers.IO) {
            messageDao.getByConversation(conversationId).map { MessageMapper.toDomain(it) }
        }
    }

    suspend fun markAsRead(messageIds: List<String>) {
        withContext(Dispatchers.IO) {
            messageDao.markAsRead(messageIds)
        }
    }

    suspend fun upsertConversation(id: String, participantIds: String, lastMessageAt: Long): String {
        return withContext(Dispatchers.IO) {
            val entity = ConversationEntity(
                id = id,
                participantIds = participantIds,
                lastMessageAt = lastMessageAt
            )
            messageDao.upsertConversation(entity)
        }
    }

    suspend fun getConversation(conversationId: String): ConversationEntity? {
        return withContext(Dispatchers.IO) {
            messageDao.getConversation(conversationId)
        }
    }
}