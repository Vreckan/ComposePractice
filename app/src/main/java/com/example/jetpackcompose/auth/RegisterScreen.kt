package com.example.jetpackcompose.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    navController: NavController,
    vm: RegisterViewModel = viewModel()
) {
    val ui by vm.uiState.collectAsState()

    RegisterContent(
        ui = ui,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onConfirmChange = vm::onConfirmChange,
        onRegisterClick = {
            vm.onRegisterClick {
                // 註冊成功後導回登入畫面
                navController.popBackStack()
            }
        },
        onBackToLoginClick = { navController.popBackStack() }
    )
}
