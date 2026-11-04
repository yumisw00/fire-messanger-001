package com.nusachat.app.domain.usecase

import com.nusachat.app.domain.model.User
import com.nusachat.app.data.AppRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val repository: AppRepository) {
    fun execute(userId: String): Flow<User?> {
        return repository.getUserFlow(userId)
    }
}