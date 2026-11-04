package com.nusachat.app.domain.usecase

import com.nusachat.app.domain.repository.AuthRepository

class VerifyPhoneUseCase(private val repository: AuthRepository) {
    fun execute(otpCode: String) {
        repository.verifyPhoneNumber(otpCode)
    }
}