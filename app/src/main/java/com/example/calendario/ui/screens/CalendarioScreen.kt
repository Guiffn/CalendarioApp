package com.example.calendario.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.model.EventoCalendario
import com.example.calendario.ui.Screen
import com.example.calendario.viewmodel.EventoViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
// --- TODAS AS IMPORTAÇÕES DO CALENDÁRIO FORAM REMOVIDAS ---
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController, viewModel: EventoViewModel) {

    // Carrega os eventos. Isto já sabemos que funciona.
    val eventos by viewModel.eventos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Eventos") }, // Título novo
                actions = {
                    IconButton(onClick = {
                        // Firebase.auth.signOut() // Descomentar se quiser reativar
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Calendario.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Sair")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // O botão de adicionar continua a funcionar
                navController.navigate(Screen.CriarEvento.createRoute(null))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Evento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- CALENDÁRIO REMOVIDO ---

            // O botão para "Ver todos" já não é necessário,
            // pois esta tela JÁ É a lista de todos.
            // Vamos removê-lo para ficar mais limpo.
            /* Button(
                onClick = { navController.navigate(Screen.ListaEventos.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Ver todos os eventos (Lista Completa)")
            }
            */

            Text(
                text = "Próximos Eventos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // A lista de eventos principal
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(eventos) { evento -> // Mostra TODOS os eventos
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

// --- NENHUMA MUDANÇA NECESSÁRIA AQUI EM BAIXO ---

// Esta função é partilhada e continua a funcionar perfeitamente.
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

// As funções DayContent e MonthHeader podem ser apagadas
// ou deixadas em branco, pois não são mais chamadas.
@Composable
fun DayContent(
    day: Any,
    isSelected: Boolean,
    temEvento: Boolean,
    onClick: (Any) -> Unit
) { /* VAZIO */ }

@Composable
fun MonthHeader(
    month: Any,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit
) { /* VAZIO */ }