package com.nusachat.app.domain.usecase

import com.nusachat.app.domain.repository.AuthRepository

class SignInWithPhoneUseCase(private val repository: AuthRepository) {
    fun execute(phoneNumber: String) {
        repository.signInWithPhone(phoneNumber)
    }
}