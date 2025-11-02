package com.example.jetpackcompose.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

// 用來描述畫面需要的狀態（帳號/密碼/錯誤訊息）
data class LoginUiState(
    val account: String = "",
    val password: String = "",
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onAccountChange(v: String) {
        uiState = uiState.copy(account = v, error = null)
    }

    fun onPasswordChange(v: String) {
        uiState = uiState.copy(password = v, error = null)
    }

    // 驗證
    fun tryLogin(onSuccess: () -> Unit) {
        val email = uiState.account.trim()
        val password = uiState.password.trim()

        // ✅ 先檢查是否為內建測試帳號
        if (email == "member" && password == "member") {
            uiState = LoginUiState()
            onSuccess()
            return
        }

        // ✅ 若不是測試帳號，再用 Firebase 驗證
        if (email.isEmpty() || password.isEmpty()) {
            uiState = uiState.copy(error = "Please enter both email and password")
            return
        }
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase 登入成功
                    uiState = LoginUiState()
                    onSuccess()
                } else {
                    // Firebase 登入失敗
                    val message = when (val e = task.exception) {
                        is FirebaseAuthInvalidUserException -> "Account not found"
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password"
                        else -> e?.localizedMessage ?: "Login failed"
                    }
                    uiState = uiState.copy(error = message)
                }
            }
    }

}
