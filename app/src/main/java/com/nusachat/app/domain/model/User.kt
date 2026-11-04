package com.nusachat.app.domain.model

data class User(
    val id: String,
    val phoneNumber: String,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)