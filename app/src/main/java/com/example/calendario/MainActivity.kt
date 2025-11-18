package com.example.calendario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calendario.ui.AppNavigation
import com.example.calendario.ui.theme.CalendarioTheme
// --- IMPORTAÇÕES ADICIONADAS ---
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Firebase.initialize(this)

        enableEdgeToEdge()
        setContent {
            CalendarioTheme {
                AppNavigation()
            }
        }
    }
}