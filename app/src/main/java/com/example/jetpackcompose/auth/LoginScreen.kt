package com.example.jetpackcompose.auth
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val uiState = vm.uiState
    LoginContent(
        uiState = uiState,
        onAccountChange = vm::onAccountChange,
        onPasswordChange = vm::onPasswordChange,
        onLoginClick = { vm.tryLogin(onSuccess) }
    )
}
