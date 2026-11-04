package com.nusachat.app.data.local

import android.content.Context
import android.content.SharedPreferences

class AuthLocalDataSource(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val KEY_UID = "saved_uid"

    fun saveUid(uid: String) {
        prefs.edit().putString(KEY_UID, uid).apply()
    }

    fun loadUid(): String? {
        return prefs.getString(KEY_UID, null)
    }

    fun observeUid(): String? = prefs.getString(KEY_UID, null)
}