package com.example.jetpackcompose.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcompose.auth.LoginScreen
import com.example.jetpackcompose.auth.RegisterScreen
import com.example.jetpackcompose.list.ListScreen
import com.example.jetpackcompose.presentation.avatar.AvatarScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1) login
        composable("login") {
            LoginScreen(
                onSuccess = { navController.navigate("list") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // 2) register
        composable("register") {
            RegisterScreen(navController = navController)
        }

        // 3) list
        composable("list") {
            // 這裡不建 ViewModel、不聽 savedStateHandle，全部交給 ListScreen 做
            ListScreen(
                navController = navController,
                onAvatarClick = { memberId, memberName ->
                    val encoded = URLEncoder.encode(
                        memberName,
                        StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate("avatar/$memberId/$encoded")
                }
            )
        }

        // 4) avatar
        composable(
            route = "avatar/{memberId}/{memberName}",
            arguments = listOf(
                navArgument("memberId") { type = NavType.LongType },
                navArgument("memberName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getLong("memberId") ?: 0L
            val rawName = backStackEntry.arguments?.getString("memberName") ?: ""
            val memberName = try {
                URLDecoder.decode(rawName, StandardCharsets.UTF_8.toString())
            } catch (_: Exception) {
                rawName
            }

            AvatarScreen(
                memberId = memberId,
                memberName = memberName,
                onFinished = { updatedId, oldOwnerId ->
                    // 丟回上一頁 (list) 的 savedStateHandle
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("avatar_changed_member_id", updatedId)

                    if (oldOwnerId != null) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("avatar_swapped_member_id", oldOwnerId)
                    }

                    navController.popBackStack("list", false)
                }
            )
        }
    }
}