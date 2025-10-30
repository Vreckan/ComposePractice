package com.example.jetpackcompose.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,                     // 登入成功後由 Nav 來切頁
    vm: LoginViewModel = viewModel()
) {
    val ui = vm.ui
    var showPwd by remember { mutableStateOf(false) }   // 密碼眼睛切換

    // 整體背景做成淡米色，接近 Figma
    Surface(color = Color(0xFFF6F1EB), modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)           // 左右留白
                .verticalScroll(rememberScrollState()),// 小螢幕避免被鍵盤擋住
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ====== Logo 區 ======
                // 如果你有放圖檔到 res/drawable/，把 R.drawable.twjoin_logo 換成你的檔名。
                // 沒有圖也沒關係，可以先用兩行文字暫時頂替（下面 2 行擇一保留）。
                // Image(painterResource(R.drawable.twjoin_logo), contentDescription = "TWJOIN")
                Text("TWJOIN", style = MaterialTheme.typography.headlineLarge)
                Text("哲煜科技", style = MaterialTheme.typography.bodyLarge)

                // ====== 帳號輸入 ======
                OutlinedTextField(
                    value = ui.account,
                    onValueChange = vm::onAccountChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please enter member account") }, // 符合 Figma: placeholder 英文
                    singleLine = true,
                    isError = ui.error != null
                )

                // ====== 密碼輸入（含眼睛圖示） ======
                OutlinedTextField(
                    value = ui.password,
                    onValueChange = vm::onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please enter password") },
                    singleLine = true,
                    isError = ui.error != null,
                    visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPwd = !showPwd }) {
                            Icon(
                                imageVector = if (showPwd) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "toggle password"
                            )
                        }
                    }
                )

                // ====== 錯誤提示（紅字，與 Figma 一致） ======
                if (ui.error != null) {
                    Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                }

                // ====== Login 按鈕（含箭頭 Icon） ======
                Button(
                    onClick = { vm.tryLogin(onSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A4A4A) // Figma 深灰
                    )
                ) {
                    Text("Login")
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
