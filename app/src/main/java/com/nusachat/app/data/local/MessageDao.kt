package com.nusachat.app.data.local

interface MessageDao {
    fun insert(message: MessageEntity): String
    fun getByConversation(conversationId: String): List<MessageEntity>
    fun markAsRead(messageIds: List<String>)
    fun upsertConversation(conversation: ConversationEntity): String
    fun getConversation(conversationId: String): ConversationEntity?
}