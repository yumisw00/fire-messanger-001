package com.nusachat.app.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.signal.libsignal.protocol.IdentityKey
import org.signal.libsignal.protocol.IdentityKeyPair
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec

object SignalKeyManager {

    private const val PREFS_NAME = "nusachat_signal_prefs"
    private const val KEY_IDENTITY_PUBLIC = "signal_identity_public"
    private const val KEY_IDENTITY_PRIVATE_ALIAS = "signal_identity_private"

    private var identityKeyPair: IdentityKeyPair? = null

    fun init(context: Context) {
        val existingPublic = getStoredPublicKey(context)
        if (existingPublic != null) {
            identityKeyPair = IdentityKeyPair(existingPublic)
        } else {
            generateAndStoreIdentityKey(context)
        }
    }

    fun getIdentityPublicKey(): ByteArray {
        val pair = identityKeyPair ?: error("SignalKeyManager not initialized")
        return pair.publicKey.encoded
    }

    fun getIdentityPrivateKey(): IdentityKey {
        val pair = identityKeyPair ?: error("SignalKeyManager not initialized")
        return IdentityKey(pair.privateKey.encoded)
    }

    private fun generateAndStoreIdentityKey(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .build()

        val sharedPreferences: android.content.SharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                KEY_IDENTITY_PRIVATE_ALIAS,
                KeyProperties.PURPOSE_SIGN
            )
                .setAlgorithmParameterSpec(
                    ECGenParameterSpec("secp256r1")
                )
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setUserAuthenticationRequired(false)
                .build()
        )
        val keyPair: KeyPair = keyPairGenerator.generateKeyPair()

        val privateKeyBytes = keyPair.privateKey.encoded
        val publicKeyBytes = keyPair.publicKey.encoded

        identityKeyPair = IdentityKeyPair(
            IdentityKey(publicKeyBytes),
            IdentityKey(privateKeyBytes)
        )

        sharedPreferences.edit().putString(KEY_IDENTITY_PUBLIC, Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)).apply()
    }

    private fun getStoredPublicKey(context: Context): ByteArray? {
        val masterKey = MasterKey.Builder(context)
            .build()

        val sharedPreferences: android.content.SharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val encoded = sharedPreferences.getString(KEY_IDENTITY_PUBLIC, null)
        return if (encoded != null) Base64.decode(encoded, Base64.NO_WRAP) else null
    }
}