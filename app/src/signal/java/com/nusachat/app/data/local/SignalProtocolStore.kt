package com.nusachat.app.data.local

import android.content.Context
import org.signal.libsignal.protocol.IdentityKey
import org.signal.libsignal.protocol.SignalProtocolAddress
import org.signal.libsignal.protocol.state.IdentityKeyStore
import org.signal.libsignal.protocol.state.PreKeyRecord
import org.signal.libsignal.protocol.state.PreKeyStore
import org.signal.libsignal.protocol.state.SessionRecord
import org.signal.libsignal.protocol.state.SessionStore
import org.signal.libsignal.protocol.state.SignedPreKeyRecord
import org.signal.libsignal.protocol.state.SignedPreKeyStore

class SignalProtocolStore(context: Context) :
    IdentityKeyStore,
    SessionStore,
    PreKeyStore,
    SignedPreKeyStore {

    private val db: MessageDatabase
    private val dao: MessageDaoImpl

    init {
        this.db = MessageDatabase(context)
        this.dao = MessageDaoImpl(db)
        SignalKeyManager.init(context)
    }

    override fun getIdentityKey(): IdentityKey {
        return SignalKeyManager.getIdentityPrivateKey()
    }

    override fun saveIdentity(name: String, key: IdentityKey) {}

    override fun saveLocalRegistrationId(registrationId: Int) {}

    override fun getLocalRegistrationId(): Int = 1

    override fun isTrustedIdentity(name: String, key: IdentityKey, direction: Int): Boolean = true

    override fun loadSession(address: SignalProtocolAddress): SessionRecord? = null

    override fun storeSession(address: SignalProtocolAddress, record: SessionRecord) {}

    override fun removeSession(address: SignalProtocolAddress) {}

    override fun removeSessions(address: SignalProtocolAddress) {}

    override fun loadAllSessions(): Map<SignalProtocolAddress, SessionRecord> = emptyMap()

    override fun containsSession(address: SignalProtocolAddress): Boolean = false

    override fun loadPreKey(preKeyId: Int): PreKeyRecord? = null

    override fun storePreKey(preKeyId: Int, record: PreKeyRecord) {}

    override fun removePreKey(preKeyId: Int) {}

    override fun containsPreKey(preKeyId: Int): Boolean = false

    override fun getLastPreKeyId(): Int = 0

    override fun loadSignedPreKey(preKeyId: Int): SignedPreKeyRecord? = null

    override fun storeSignedPreKey(preKeyId: Int, record: SignedPreKeyRecord) {}

    override fun removeSignedPreKey(preKeyId: Int) {}

    override fun containsSignedPreKey(preKeyId: Int): Boolean = false

    override fun getLastSignedPreKeyId(): Int = 0
}