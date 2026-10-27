package com.moodverses.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moodverses.app.ui.MoodVersesApp
import com.moodverses.app.ui.theme.MoodVersesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodVersesTheme {
                MoodVersesApp()
            }
        }
    }
}
