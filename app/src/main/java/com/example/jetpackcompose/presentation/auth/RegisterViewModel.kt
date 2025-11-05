package com.example.jetpackcompose.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    // ---- 使用者輸入 ----
    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onConfirmChange(value: String) {
        _uiState.value = _uiState.value.copy(confirm = value, error = null)
    }

    // ---- 註冊行為 ----
    fun onRegisterClick(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirm = _uiState.value.confirm

        // 基本驗證
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(error = "Invalid email format")
            return
        }
        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 8 characters")
            return
        }
        if (password != confirm) {
            _uiState.value = _uiState.value.copy(error = "Passwords do not match")
            return
        }

        // 開始註冊
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _uiState.value = _uiState.value.copy(loading = false)
                onSuccess()
            } catch (e: Exception) {
                val msg = e.message ?: "Registration failed"
                val errMsg = when {
                    msg.contains("already in use", true) -> "This email is already registered"
                    msg.contains("badly formatted", true) -> "Invalid email format"
                    else -> msg
                }
                _uiState.value = _uiState.value.copy(loading = false, error = errMsg)
            }
        }
    }
}
