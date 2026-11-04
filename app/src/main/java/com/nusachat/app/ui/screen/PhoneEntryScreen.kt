package com.nusachat.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nusachat.app.domain.model.AuthState
import com.nusachat.app.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PhoneEntryScreen(authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val phoneNumberState = remember { mutableStateOf("+62") }
    val isVerifyingState = remember { mutableStateOf(false) }
    val showOtpFieldState = remember { mutableStateOf(false) }
    val otpCodeState = remember { mutableStateOf("") }
    val errorMessageState = remember { mutableStateOf("") }

    val authState = authViewModel.uiState.collectAsState().value

    val showSuccess = authState is AuthState.Success && authState.uid.length > 0
    if (showSuccess) {
        PhoneVerifiedScreen(uid = authState.uid, phoneNumber = authState.phoneNumber)
        return@PhoneEntryScreen
    }

    if (authState is AuthState.Success) {
        showOtpFieldState.value = true
    }
    if (authState is AuthState.Error) {
        errorMessageState.value = authState.message
        isVerifyingState.value = false
    }
    if (authState is AuthState.Loading) {
        isVerifyingState.value = true
    }

    val currentError = errorMessageState.value
    val notVerifying = isVerifyingState.value == false

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Selamat Datang",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Masukkan nomor telepon Anda",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumberState.value,
            onValueChange = { newValue: String ->
                if (!newValue.startsWith("+62")) {
                    phoneNumberState.value = "+62$newValue"
                } else {
                    phoneNumberState.value = newValue
                }
            },
            label = { Text("Nomor Telepon") },
            placeholder = { Text("8123456789") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

        if (showOtpFieldState.value) {
            Text(
                text = "Kode verifikasi telah dikirim ke nomor Anda",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = otpCodeState.value,
                onValueChange = { newValue: String ->
                    otpCodeState.value = newValue
                },
                label = { Text("Masukkan Kode OTP") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        }

        if (currentError.length > 0) {
            Text(
                text = currentError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).align(Alignment.CenterHorizontally)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val cleaned = phoneNumberState.value.removePrefix("+")
                if (!isValidIndonesianPhone(cleaned)) {
                    errorMessageState.value = "Nomor telepon Indonesia tidak valid. Gunakan format 08xx atau 8xx."
                    return@Button
                }
                isVerifyingState.value = true
                errorMessageState.value = ""
                authViewModel.signInWithPhone("+$cleaned")
            },
            enabled = notVerifying,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isVerifyingState.value) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.width(20.dp).height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(20.dp).height(20.dp))
                }
            } else {
                Text(if (showOtpFieldState.value) "Verifikasi OTP" else "Verifikasi")
            }
        }
    }
}

private fun isValidIndonesianPhone(number: String): Boolean {
    var count = 0
    var first: Char = ' '
    var second: Char = ' '
    for (c in number) {
        if (c.isDigit()) {
            count++
            if (count == 1) first = c
            if (count == 2) second = c
        }
    }
    return if (count >= 2) first == '0' && second == '8' else false
        || count >= 1 && first == '8'
}

@Composable
fun PhoneVerifiedScreen(uid: String, phoneNumber: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verifikasi Berhasil",
            style = MaterialTheme.typography.headlineMedium
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        Text(text = "UID: $uid")
        Text(text = "Nomor: $phoneNumber")
    }
}
