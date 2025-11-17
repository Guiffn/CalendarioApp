package com.example.calendario.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.calendario.ui.Screen
import com.example.calendario.viewmodel.EventoViewModel
// Várias importações desnecessárias foram removidas (Card, Column, etc.)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEventosScreen(navController: NavController, viewModel: EventoViewModel) {

    val eventos by viewModel.eventos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todos os Eventos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {}
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                items(eventos) { evento ->
                    // --- CORREÇÃO DE AMBIGUIDADE ---
                    // Esta chamada agora usa a função 'EventoItem'
                    // do ficheiro 'CalendarioScreen.kt' (pois estão no mesmo pacote).
                    EventoItem(evento = evento, onClick = {
                        evento.id?.let { id ->
                            navController.navigate(Screen.DetalheEvento.createRoute(id))
                        }
                    })
                }
            }
        }
    }
}

// --- FUNÇÃO DUPLICADA REMOVIDA DAQUI ---
// A função @Composable fun EventoItem(...) foi apagada deste ficheiro
// para evitar o conflito.