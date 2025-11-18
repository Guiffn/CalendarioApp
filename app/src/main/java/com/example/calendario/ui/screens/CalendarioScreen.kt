package com.example.calendario.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.model.EventoCalendario
import com.example.calendario.ui.Screen
import com.example.calendario.viewmodel.EventoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController, viewModel: EventoViewModel) {
    val eventos by viewModel.eventos.collectAsState()

    val datePickerState = rememberDatePickerState()

    val selectedDateMillis = datePickerState.selectedDateMillis?.let {
        val calendarUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarUtc.timeInMillis = it

        val calendarLocal = Calendar.getInstance()
        calendarLocal.set(
            calendarUtc.get(Calendar.YEAR),
            calendarUtc.get(Calendar.MONTH),
            calendarUtc.get(Calendar.DAY_OF_MONTH)
        )
        calendarLocal.timeInMillis
    } ?: System.currentTimeMillis()

    val eventosDoDia = remember(selectedDateMillis, eventos) {
        val calSelecionado = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        eventos.filter { evento ->
            val calEvento = Calendar.getInstance().apply { timeInMillis = evento.data }
            calSelecionado.get(Calendar.YEAR) == calEvento.get(Calendar.YEAR) &&
                    calSelecionado.get(Calendar.DAY_OF_YEAR) == calEvento.get(Calendar.DAY_OF_YEAR)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Calendário") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // --- NAVEGAÇÃO ATUALIZADA ---
                navController.navigate(Screen.CriarEvento.createRoute(selectedDateMillis, null))
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
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            if (eventosDoDia.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum evento para este dia.\nToque em '+' para adicionar um.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(eventosDoDia) { evento ->
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
}

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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = evento.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = dataFormatada, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
