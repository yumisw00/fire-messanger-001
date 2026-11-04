package com.nusachat.app.data.local

import com.nusachat.app.domain.model.MessageStatus
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteStatement

class MessageDaoImpl(private val database: MessageDatabase) : MessageDao {

    override fun insert(message: MessageEntity): String {
        val db = database.getEncryptedWritableDatabase()
        db.beginTransaction()
        return try {
            val stmt: SQLiteStatement = db.compileStatement(
                """
                INSERT INTO messages (id, conversation_id, sender_id, ciphertext, timestamp, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()
            )
            stmt.bindString(1, message.id ?: "")
            stmt.bindString(2, message.conversationId)
            stmt.bindString(3, message.senderId)
            stmt.bindBlob(4, message.ciphertext)
            stmt.bindLong(5, message.timestamp)
            stmt.bindString(6, message.status)
            val rowId = stmt.executeInsert()
            stmt.close()
            db.setTransactionSuccessful()
            rowId.toString()
        } finally {
            db.endTransaction()
        }
    }

    override fun getByConversation(conversationId: String): List<MessageEntity> {
        val db = database.getEncryptedReadableDatabase()
        val cursor = db.query(
            true,
            "messages",
            arrayOf("id", "conversation_id", "sender_id", "ciphertext", "timestamp", "status"),
            "conversation_id = ?",
            arrayOf(conversationId),
            null,
            null,
            "timestamp ASC",
            null
        )

        val messages = mutableListOf<MessageEntity>()
        try {
            while (cursor.moveToNext()) {
                messages.add(
                    MessageEntity(
                        id = cursor.getString(cursor.getColumnIndex("id")),
                        conversationId = cursor.getString(cursor.getColumnIndex("conversation_id")),
                        senderId = cursor.getString(cursor.getColumnIndex("sender_id")),
                        ciphertext = cursor.getBlob(cursor.getColumnIndex("ciphertext")),
                        timestamp = cursor.getLong(cursor.getColumnIndex("timestamp")),
                        status = cursor.getString(cursor.getColumnIndex("status"))
                    )
                )
            }
        } finally {
            cursor.close()
        }
        return messages
    }

    override fun markAsRead(messageIds: List<String>) {
        val db = database.getEncryptedWritableDatabase()
        db.beginTransaction()
        try {
            for (id in messageIds) {
                db.execSQL(
                    "UPDATE messages SET status = ? WHERE id = ?",
                    arrayOf(MessageStatus.READ.name, id)
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun upsertConversation(conversation: ConversationEntity): String {
        val db = database.getEncryptedWritableDatabase()
        db.beginTransaction()
        return try {
            val stmt: SQLiteStatement = db.compileStatement(
                """
                INSERT OR REPLACE INTO conversations (id, participant_ids, last_message_at)
                VALUES (?, ?, ?)
                """.trimIndent()
            )
            stmt.bindString(1, conversation.id ?: "")
            stmt.bindString(2, conversation.participantIds)
            stmt.bindLong(3, conversation.lastMessageAt)
            val rowId = stmt.executeInsert()
            stmt.close()
            db.setTransactionSuccessful()
            rowId.toString()
        } finally {
            db.endTransaction()
        }
    }

    override fun getConversation(conversationId: String): ConversationEntity? {
        val db = database.getEncryptedReadableDatabase()
        val cursor = db.query(
            true,
            "conversations",
            arrayOf("id", "participant_ids", "last_message_at"),
            "id = ?",
            arrayOf(conversationId),
            null,
            null,
            null,
            null
        )

        try {
            if (cursor.moveToNext()) {
                return ConversationEntity(
                    id = cursor.getString(cursor.getColumnIndex("id")),
                    participantIds = cursor.getString(cursor.getColumnIndex("participant_ids")),
                    lastMessageAt = cursor.getLong(cursor.getColumnIndex("last_message_at"))
                )
            }
            return null
        } finally {
            cursor.close()
        }
    }
}