package com.nusachat.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nusachat.app.domain.model.AuthState
import com.nusachat.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val uiState: StateFlow<AuthState> = repository.authState

    fun signInWithPhone(phoneNumber: String) {
        viewModelScope.launch {
            repository.signInWithPhone(phoneNumber)
        }
    }

    fun submitOtp(otpCode: String) {
        viewModelScope.launch {
            repository.verifyPhoneNumber(otpCode)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}