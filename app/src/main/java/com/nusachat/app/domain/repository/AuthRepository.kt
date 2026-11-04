package com.nusachat.app.domain.repository

import com.nusachat.app.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    fun signInWithPhone(phoneNumber: String)
    fun verifyPhoneNumber(otpCode: String)
    fun signOut()
    fun isSignedIn(): Boolean
    fun getCurrentUid(): String?
}