package com.example.jetpackcompose.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.R

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

@Composable
fun RegisterContent(
    ui: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var showPwd by remember { mutableStateOf(false) }

    Surface(color = Color(0xFFF6F1EB), modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo（沿用你的 login 設計）
                Image(
                    painter = painterResource(R.drawable.logo_twjoin),
                    contentDescription = "TWJOIN",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(335f / 173.08f)
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 8.dp),
                    contentScale = ContentScale.Fit
                )

                // Email
                OutlinedTextField(
                    value = ui.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please enter member account") },
                    singleLine = true,
                    isError = ui.error != null,
                    enabled = !ui.loading
                )

                // Password
                OutlinedTextField(
                    value = ui.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please enter password") },
                    singleLine = true,
                    isError = ui.error != null,
                    visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPwd = !showPwd }) {
                            Icon(
                                painter = painterResource(
                                    if (showPwd) R.drawable.eye else R.drawable.eye_close
                                ),
                                contentDescription = if (showPwd) "Hide password" else "Show password",
                                modifier = Modifier.size(22.dp),
                                tint = Color.Unspecified
                            )
                        }
                    },
                    enabled = !ui.loading
                )

                // Confirm Password（沿用同一個眼睛切換）
                OutlinedTextField(
                    value = ui.confirm,
                    onValueChange = onConfirmChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please confirm password") },
                    singleLine = true,
                    isError = ui.error != null,
                    visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPwd = !showPwd }) {
                            Icon(
                                painter = painterResource(
                                    if (showPwd) R.drawable.eye else R.drawable.eye_close
                                ),
                                contentDescription = if (showPwd) "Hide password" else "Show password",
                                modifier = Modifier.size(22.dp),
                                tint = Color.Unspecified
                            )
                        }
                    },
                    enabled = !ui.loading
                )

                // 錯誤提示
                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                // Register 按鈕（與 Login 一致的深灰色樣式）
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A4A4A),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    enabled = !ui.loading && ui.email.isNotBlank() && ui.password.isNotBlank() && ui.confirm.isNotBlank()
                ) {
                    Text(
                        text = if (ui.loading) "Processing..." else "Register",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp,
                            letterSpacing = 0.25.sp
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.arrow_right),
                        contentDescription = "arrow",
                        modifier = Modifier.size(18.dp),
                        tint = LocalContentColor.current
                    )
                }

                // 回到登入
                TextButton(
                    onClick = onBackToLoginClick,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !ui.loading
                ) { Text("← Back to Login") }
            }
        }
    }
}

@Preview(name = "Register - empty", showBackground = true)
@Composable
fun RegisterContentPreview() {
    RegisterContent(
        ui = RegisterUiState(),
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmChange = {},
        onRegisterClick = {},
        onBackToLoginClick = {}
    )
}

@Preview(name = "Register - error", showBackground = true)
@Composable
fun RegisterContentErrorPreview() {
    RegisterContent(
        ui = RegisterUiState(
            email = "test@example.com",
            password = "12345678",
            confirm = "1234",
            error = "Passwords do not match"
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmChange = {},
        onRegisterClick = {},
        onBackToLoginClick = {}
    )
}
