package com.example.calendario.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.model.EventoCalendario
import com.example.calendario.ui.Screen
import com.example.calendario.viewmodel.EventoViewModel
// Imports do Firebase.auth e FAB foram removidos
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEventosScreen(navController: NavController, viewModel: EventoViewModel) {

    val eventos by viewModel.eventos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todos os Eventos") },
                // --- BOTÃO DE SAIR REMOVIDO ---
                // --- ADICIONADO BOTÃO DE VOLTAR ---
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        // --- BOTÃO DE ADICIONAR (FAB) REMOVIDO ---
        // Ele agora vive na CalendarioScreen
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

/*
   A função EventoItem() foi movida para o arquivo CalendarioScreen.kt,
   mas não há problema em mantê-la aqui também se esta tela ainda a utiliza.
   Para um projeto maior, nós a moveríamos para um arquivo "common" (comum),
   mas para "Ctrl+V" é mais seguro deixá-la aqui E copiá-la para a CalendarioScreen.
*/
@Composable
fun EventoItem(evento: EventoCalendario, onClick: () -> Unit) {
    val dataFormatada = remember(evento.data) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(evento.data))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = evento.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = dataFormatada, style = MaterialTheme.typography.bodyMedium)
            Text(text = evento.exibirDetalhes(), style = MaterialTheme.typography.bodySmall)
        }
    }
}