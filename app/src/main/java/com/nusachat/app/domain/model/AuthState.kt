package com.nusachat.app.domain.model

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val uid: String, val phoneNumber: String) : AuthState()
    data class Error(val message: String) : AuthState()
}