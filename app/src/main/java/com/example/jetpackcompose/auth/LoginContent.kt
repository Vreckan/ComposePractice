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


@Composable
fun LoginContent(
    uiState: LoginUiState,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
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
                // Logo
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

                // 帳號輸入
                OutlinedTextField(
                    value = uiState.account,
                    onValueChange = onAccountChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please enter member account") },
                    singleLine = true,
                    isError = uiState.error != null
                )

                // 密碼輸入
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Please enter password") },
                    singleLine = true,
                    isError = uiState.error != null,
                    visualTransformation = if (showPwd)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
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
                    }
                )

                // 錯誤提示
                uiState.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                // 登入按鈕
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A4A4A),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Login",
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
            }
        }
    }
}

@Preview(name = "normal", showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        uiState = LoginUiState(account = "", password = "", error = null),
        onAccountChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}

@Preview(name="error", showBackground = true)
@Composable
fun LoginPreview_Error() {
    LoginContent(
        uiState = LoginUiState(account = "Test1", password = "Test1", error = "account or password incorrect"),
        onAccountChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}