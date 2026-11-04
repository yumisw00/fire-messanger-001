package com.nusachat.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SQLiteDatabase

object EncryptedDatabaseHelper {

    private const val PREFS_NAME = "nusachat_encrypted_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase"
    const val DATABASE_NAME = "nusachat.db"
    const val DATABASE_VERSION = 1

    private var passphrase: String? = null

    fun init(context: Context) {
        SQLiteDatabase.loadLibs(context)
        passphrase = getOrCreatePassphrase(context)
    }

    fun getPassphrase(): String {
        return passphrase ?: error("Database not initialized. Call EncryptedDatabaseHelper.init(context) first.")
    }

    private fun getOrCreatePassphrase(context: Context): String {
        val masterKey = MasterKey.Builder(context)
            .build()

        val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existing = sharedPreferences.getString(KEY_PASSPHRASE, null)
        return existing ?: generateAndSavePassphrase(sharedPreferences)
    }

    private fun generateAndSavePassphrase(sharedPreferences: SharedPreferences): String {
        val newPassphrase = java.util.UUID.randomUUID().toString() + java.util.UUID.randomUUID().toString()
        sharedPreferences.edit().putString(KEY_PASSPHRASE, newPassphrase).apply()
        return newPassphrase
    }
}