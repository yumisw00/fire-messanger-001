package com.nusachat.app.data.local

import org.junit.Test
import org.junit.Assert.*

class SignalEncryptionTest {

    @Test
    fun encryptDecryptRoundTrip() {
        val store = SignalProtocolStore(android.app.ApplicationProvider.getApplicationContext())
        val encryptor = MessageEncryptor(store)

        val plaintext = "Ini adalah pesan rahasia"
        val senderPublicKey = SignalKeyManager.getIdentityPublicKey()
        val recipientPublicKey = SignalKeyManager.getIdentityPublicKey()

        val ciphertext = encryptor.encrypt(plaintext, recipientPublicKey)
        val decrypted = encryptor.decrypt(ciphertext, senderPublicKey)

        assertEquals(plaintext, decrypted)
    }
}