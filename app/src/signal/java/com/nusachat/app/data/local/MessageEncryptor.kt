package com.nusachat.app.data.local

import org.signal.libsignal.protocol.CiphertextMessage
import org.signal.libsignal.protocol.IdentityKey
import org.signal.libsignal.protocol.SignalProtocolAddress
import org.signal.libsignal.protocol.SessionCipher

class MessageEncryptor(private val signalProtocolStore: SignalProtocolStore) {

    fun encrypt(plaintext: String, recipientSignalPublicKey: ByteArray): ByteArray {
        val address = SignalProtocolAddress("recipient", 1)
        val sessionCipher = SessionCipher(signalProtocolStore, address)

        val identityKey = IdentityKey(recipientSignalPublicKey)
        val ciphertextMessage = sessionCipher.encrypt(plaintext.toByteArray(), identityKey)
        return ciphertextMessage.serialize()
    }

    fun decrypt(ciphertext: ByteArray, senderSignalPublicKey: ByteArray): String {
        val address = SignalProtocolAddress("sender", 1)
        val sessionCipher = SessionCipher(signalProtocolStore, address)

        val identityKey = IdentityKey(senderSignalPublicKey)
        val ciphertextMessage = CiphertextMessage.parse(ciphertext)
        val plaintext = sessionCipher.decrypt(ciphertextMessage, identityKey)
        return String(plaintext)
    }
}