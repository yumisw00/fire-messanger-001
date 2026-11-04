package com.nusachat.app.data.local

import com.nusachat.app.domain.model.Message
import com.nusachat.app.domain.model.MessageStatus

object MessageMapper {

    fun toEntity(message: Message): MessageEntity {
        return MessageEntity(
            id = message.id,
            conversationId = message.conversationId,
            senderId = message.senderId,
            ciphertext = message.ciphertext,
            timestamp = message.timestamp,
            status = message.status.name
        )
    }

    fun toDomain(entity: MessageEntity): Message {
        return Message(
            id = entity.id,
            conversationId = entity.conversationId,
            senderId = entity.senderId,
            ciphertext = entity.ciphertext,
            timestamp = entity.timestamp,
            status = MessageStatus.valueOf(entity.status)
        )
    }
}