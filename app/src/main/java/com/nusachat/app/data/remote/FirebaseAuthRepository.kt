package com.nusachat.app.data.remote

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nusachat.app.domain.model.AuthState
import com.nusachat.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class FirebaseAuthRepository(private val context: Context) : AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun signInWithPhone(phoneNumber: String) {
        _authState.value = AuthState.Loading

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyPhoneNumber(otpCode: String) {
        _authState.value = AuthState.Loading
        verificationId?.let { vid ->
            val credential = PhoneAuthProvider.getCredential(vid, otpCode)
            signInWithCredential(credential)
        } ?: run {
            _authState.value = AuthState.Error(
                message = "Kode verifikasi tidak valid. Silakan coba lagi."
            )
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _authState.value = AuthState.Success(
                        uid = user?.uid ?: "",
                        phoneNumber = user?.phoneNumber ?: ""
                    )
                } else {
                    when (val e = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            _authState.value = AuthState.Error(
                                message = "Kode verifikasi salah. Silakan coba lagi."
                            )
                        }
                        else -> {
                            _authState.value = AuthState.Error(
                                message = "Verifikasi gagal: ${e?.message ?: "Terjadi kesalahan tidak diketahui"}"
                            )
                        }
                    }
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("FirebaseAuthRepository", "onVerificationCompleted: PNV otomatis berhasil")
            signInWithCredential(credential)
        }

        override fun onVerificationFailed(firebaseException: FirebaseException) {
            Log.e("FirebaseAuthRepository", "onVerificationFailed", firebaseException)
            _authState.value = when {
                firebaseException is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                    AuthState.Error(message = "Nomor telepon tidak valid.")
                }
                else -> {
                    AuthState.Error(
                        message = "Verifikasi gagal: ${firebaseException.message ?: "Terjadi kesalahan"}"
                    )
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d("FirebaseAuthRepository", "onCodeSent: fallback ke SMS OTP")
            this@FirebaseAuthRepository.verificationId = verificationId
            this@FirebaseAuthRepository.resendToken = token
            _authState.value = AuthState.Success(uid = "", phoneNumber = "")
        }

        override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
            Log.d("FirebaseAuthRepository", "onCodeAutoRetrievalTimeOut")
            this@FirebaseAuthRepository.verificationId = verificationId
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Idle
        verificationId = null
        resendToken = null
    }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    override fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    fun submitOtp(otpCode: String) {
        verifyPhoneNumber(otpCode)
    }

    fun getVerificationId(): String? = verificationId
    fun getResendToken(): PhoneAuthProvider.ForceResendingToken? = resendToken
}