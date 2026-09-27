package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.navigation.QuranKitaApp
import com.example.ui.theme.QuranKitaTheme
import com.example.ui.theme.ThemeModeState
import com.example.ui.theme.ThemeSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ThemeModeState.value = ThemeSettings.load(this)
        setContent {
            QuranKitaTheme {
                QuranKitaApp()
            }
        }
    }
}
