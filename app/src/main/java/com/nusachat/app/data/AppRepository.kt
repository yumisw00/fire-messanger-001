package com.nusachat.app.data

import com.nusachat.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getCurrentUserId(): String?
    fun saveCurrentUserId(userId: String)
    fun getUserFlow(userId: String): Flow<User?>
    fun signInWithPhone(phoneNumber: String)
    fun signOut()
}

class AppRepositoryImpl : AppRepository {
    override fun getCurrentUserId(): String? {
        // TODO: Implement with SQLCipher local DB
        return null
    }

    override fun saveCurrentUserId(userId: String) {
        // TODO: Implement with SQLCipher local DB
    }

    override fun getUserFlow(userId: String): Flow<User?> {
        // TODO: Implement with local DB + remote
        return kotlinx.coroutines.flow.flow { emit(null) }
    }

    override fun signInWithPhone(phoneNumber: String) {
        // TODO: Implement Firebase Auth phone verification
    }

    override fun signOut() {
        // TODO: Implement sign out
    }
}