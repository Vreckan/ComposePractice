package com.example.jetpackcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.jetpackcompose.data.AvatarRepository
import com.example.jetpackcompose.data.local.AppDatabase
import com.example.jetpackcompose.data.remote.RetrofitProvider
import com.example.jetpackcompose.list.ListViewModel
import com.example.jetpackcompose.nav.AppNav
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 檢查
        val fb = FirebaseApp.getInstance()
        Log.d("FB", "projectId=${fb.options.projectId}")

        // ✅ 第一次啟動塞 members
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val firstRun = prefs.getBoolean("first_run", true)
        if (firstRun) {
            // 1) 先塞 members
            val listVm = ViewModelProvider(this, ListViewModel.Factory)
                .get(ListViewModel::class.java)
            listVm.ensureSeedIfEmpty()

            // 2) 再塞 avatars（這裡直接用 repo 做）
            val db = AppDatabase.getInstance(this)
            val repo = AvatarRepository(
                api = RetrofitProvider.openai(),
                avatarDao = db.avatarDao(),
                appContext = this
            )

            // 這裡要開 coroutine，因為是 suspend
            lifecycleScope.launch {
                repo.preloadFromAssetsIfEmpty()
            }

            prefs.edit().putBoolean("first_run", false).apply()
        }

        setContent {
            androidx.compose.material3.MaterialTheme {
                androidx.compose.material3.Surface {
                    AppNav()
                }
            }
        }
    }
}