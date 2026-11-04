package com.nusachat.app.data.local

import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class MessageDatabase(context: Context) : SQLiteOpenHelper(
    context,
    EncryptedDatabaseHelper.DATABASE_NAME,
    null,
    EncryptedDatabaseHelper.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS messages (
                id TEXT PRIMARY KEY,
                conversation_id TEXT NOT NULL,
                sender_id TEXT NOT NULL,
                ciphertext BLOB NOT NULL,
                timestamp INTEGER NOT NULL,
                status TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS conversations (
                id TEXT PRIMARY KEY,
                participant_ids TEXT NOT NULL,
                last_message_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS messages")
        db.execSQL("DROP TABLE IF EXISTS conversations")
        onCreate(db)
    }

    fun getEncryptedWritableDatabase(): SQLiteDatabase {
        return getWritableDatabase(EncryptedDatabaseHelper.getPassphrase())
    }

    fun getEncryptedReadableDatabase(): SQLiteDatabase {
        return getReadableDatabase(EncryptedDatabaseHelper.getPassphrase())
    }
}