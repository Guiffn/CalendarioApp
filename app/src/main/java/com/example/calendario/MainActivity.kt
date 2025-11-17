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

        // --- CORREÇÃO DO CRASH (MANTER ISTO) ---
        // Esta linha inicializa o Firebase e corrige o crash do login.
        Firebase.initialize(this)

        enableEdgeToEdge()
        setContent {
            CalendarioTheme {
                // --- CORREÇÃO DO AVISO (SCAFFOLD REMOVIDO) ---
                // Removemos o Scaffold desnecessário que estava aqui.
                // O AppNavigation gere os Scaffolds de cada ecrã.
                AppNavigation()
            }
        }
    }
}