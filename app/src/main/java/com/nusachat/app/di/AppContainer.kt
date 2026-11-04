package com.nusachat.app.di

import android.content.Context
import com.nusachat.app.data.AppRepository
import com.nusachat.app.data.AppRepositoryImpl
import com.nusachat.app.data.local.AuthLocalDataSource
import com.nusachat.app.data.local.ChatRepositoryImpl
import com.nusachat.app.data.local.EncryptedDatabaseHelper
import com.nusachat.app.data.local.MessageDatabase
import com.nusachat.app.data.local.MessageDaoImpl
import com.nusachat.app.data.local.MessageLocalRepository
import com.nusachat.app.data.remote.FirebaseAuthRepository
import com.nusachat.app.domain.repository.AuthRepository
import com.nusachat.app.domain.repository.ChatRepository
import com.nusachat.app.domain.usecase.GetConversationsUseCase
import com.nusachat.app.domain.usecase.GetMessagesUseCase
import com.nusachat.app.domain.usecase.GetUserUseCase
import com.nusachat.app.domain.usecase.SendMessageUseCase
import com.nusachat.app.ui.viewmodel.AuthViewModel
import com.nusachat.app.ui.viewmodel.ChatViewModel

class AppContainer(private val context: Context) {
    val repository: AppRepository = AppRepositoryImpl()
    val getUserUseCase: GetUserUseCase = GetUserUseCase(repository)

    val authRepository: AuthRepository = FirebaseAuthRepository(context)
    val authLocalDataSource: AuthLocalDataSource = AuthLocalDataSource(context)
    val authViewModel: AuthViewModel = AuthViewModel(authRepository)

    // Encrypted local database for messages
    val encryptedDatabaseHelper: EncryptedDatabaseHelper = EncryptedDatabaseHelper
    val messageDatabase: MessageDatabase = MessageDatabase(context)
    val messageDao: MessageDaoImpl = MessageDaoImpl(messageDatabase)
    val messageLocalRepository: MessageLocalRepository = MessageLocalRepository(messageDao)

    // Chat repository and use cases
    val chatRepository: ChatRepository = ChatRepositoryImpl()
    val getConversationsUseCase: GetConversationsUseCase = GetConversationsUseCase(chatRepository)
    val getMessagesUseCase: GetMessagesUseCase = GetMessagesUseCase(chatRepository)
    val sendMessageUseCase: SendMessageUseCase = SendMessageUseCase(chatRepository)

    fun createChatViewModel(): ChatViewModel = ChatViewModel(
        conversationRepository = chatRepository,
        getConversationsUseCase = getConversationsUseCase,
        getMessagesUseCase = getMessagesUseCase,
        sendMessageUseCase = sendMessageUseCase
    )

    init {
        EncryptedDatabaseHelper.init(context)
    }
}