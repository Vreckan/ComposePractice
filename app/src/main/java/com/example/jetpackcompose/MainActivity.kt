package com.example.jetpackcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackcompose.list.ListViewModel
import com.example.jetpackcompose.nav.AppNav
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val firstRun = prefs.getBoolean("first_run", true)
        val app = FirebaseApp.getInstance()  // 若這行丟例外，表示 JSON/Plugin 沒生效
        Log.d("FB", "projectId=${app.options.projectId}, appId=${app.options.applicationId}")

        if (firstRun) {
            val vm = ViewModelProvider(this, ListViewModel.Factory).get(ListViewModel::class.java)
            vm.ensureSeedIfEmpty()
            prefs.edit().putBoolean("first_run", false).apply()
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNav()
                }
            }
        }
    }
}
