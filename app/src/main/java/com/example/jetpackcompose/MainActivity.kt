package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackcompose.list.ListViewModel
import com.example.jetpackcompose.nav.AppNav



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val firstRun = prefs.getBoolean("first_run", true)
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
