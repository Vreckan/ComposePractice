package com.example.jetpackcompose.auth

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit, // ✅ 新增註冊導向 callback
    vm: LoginViewModel = viewModel()
) {
    val uiState = vm.uiState

    LoginContent(
        uiState = uiState,
        onAccountChange = vm::onAccountChange,
        onPasswordChange = vm::onPasswordChange,
        onLoginClick = { vm.tryLogin(onSuccess) },
        onSignUpClick = onNavigateToRegister // ✅ 新增傳給下層 UI
    )
}
