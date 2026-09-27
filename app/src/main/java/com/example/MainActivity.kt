package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.navigation.QuranKitaApp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.QuranKitaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(AppThemeMode.DARK) }

            QuranKitaTheme(themeMode = currentTheme) {
                QuranKitaApp(
                    onThemeChanged = { newTheme ->
                        currentTheme = newTheme
                    }
                )
            }
        }
    }
}
