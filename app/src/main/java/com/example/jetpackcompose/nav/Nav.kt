package com.example.jetpackcompose.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcompose.auth.LoginScreen
import com.example.jetpackcompose.auth.RegisterScreen
import com.example.jetpackcompose.list.ListScreen

@Composable
fun AppNav() {
    // 建立 NavController（導航控制器）
    val navController = rememberNavController()

    // 定義導航圖
    NavHost(
        navController = navController,
        startDestination = "login" // 一開始從 login 畫面進入
    ) {
        // 登入頁
        composable("login") {
            LoginScreen(
                onSuccess = { navController.navigate("list") }, // 登入成功導向 list
                onNavigateToRegister = { navController.navigate("register") } // 👉 點註冊導向註冊畫面
            )
        }

        // 註冊頁
        composable("register") {
            RegisterScreen(
                navController = navController // 回登入頁使用 popBackStack()
            )
        }

        // 主清單頁
        composable("list") {
            ListScreen()
        }
    }
}
