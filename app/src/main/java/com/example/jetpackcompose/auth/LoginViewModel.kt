package com.example.jetpackcompose.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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
        val ok = uiState.account == "member" && uiState.password == "member"
        if (ok) {
            uiState = LoginUiState()
            onSuccess()
        } else {
            uiState = uiState.copy(error = "account or password incorrect")
        }
    }
}
