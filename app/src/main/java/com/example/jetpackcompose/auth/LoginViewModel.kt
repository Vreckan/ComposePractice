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

    // Compose 會觀察這個狀態，變了就自動重組 UI
    var ui by mutableStateOf(LoginUiState())
        private set

    fun onAccountChange(v: String) {
        ui = ui.copy(account = v, error = null)   // 打字時清錯誤
    }

    fun onPasswordChange(v: String) {
        ui = ui.copy(password = v, error = null)  // 打字時清錯誤
    }

    // 按下登入鍵時呼叫：驗證固定帳密 member/member
    fun tryLogin(onSuccess: () -> Unit) {
        val ok = ui.account == "member" && ui.password == "member"
        if (ok) {
            ui = LoginUiState()   // 成功後回到初始狀態（題目要求）
            onSuccess()
        } else {
            ui = ui.copy(error = "account or password incorrect")
        }
    }
}
