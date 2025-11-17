package com.example.calendario.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calendario.model.EventoCalendario
import com.example.calendario.ui.Screen
import com.example.calendario.viewmodel.EventoViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Esta é a nova tela principal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController, viewModel: EventoViewModel) {

    // 1. Pega a lista completa de eventos do ViewModel
    val eventos by viewModel.eventos.collectAsState()

    // 2. Processa a lista de eventos para saber quais dias têm eventos (para os marcadores)
    val diasComEvento by remember(eventos) {
        derivedStateOf {
            eventos.map {
                // Converte o Long (milisegundos) para um LocalDate (kotlinx-datetime)
                Instant.fromEpochMilliseconds(it.data)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            }.toSet() // .toSet() remove duplicatas e é rápido de consultar
        }
    }

    // 3. Controla o estado do calendário (qual dia está selecionado)
    var selectionState by remember { mutableStateOf<DynamicSelectionState?>(null) }
    selectionState = rememberSelectableCalendarState(
        selectionState = DynamicSelectionState(
            selection = emptyList(),
            selectionMode = SelectionMode.Single
        )
    )

    // 4. Filtra a lista de eventos para mostrar apenas os do dia selecionado
    val eventosDoDiaSelecionado by remember(selectionState?.selection, eventos) {
        derivedStateOf {
            val dataSelecionada = selectionState?.selection?.firstOrNull()
            if (dataSelecionada == null) {
                emptyList()
            } else {
                eventos.filter {
                    val dataEvento = Instant.fromEpochMilliseconds(it.data)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                    dataEvento == dataSelecionada
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Calendário") },
                // Botão de "Sair" (Logout)
                actions = {
                    IconButton(onClick = {
                        Firebase.auth.signOut()
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
            // Botão de "Adicionar" (FAB)
            FloatingActionButton(onClick = {
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
            // 5. A Grade do Calendário
            SelectableCalendar(
                calendarState = selectionState!!,
                modifier = Modifier.padding(horizontal = 16.dp),
                // 6. Customização dos dias (para mostrar os marcadores)
                dayContent = { dayState ->
                    // 'dayState' tem informações sobre o dia (data, se está selecionado, etc.)
                    // 'diasComEvento' é a nossa lista de dias que têm eventos.
                    DayContent(dayState = dayState, temEvento = diasComEvento.contains(dayState.date))
                }
            )

            // 7. Botão para ver a lista completa
            Button(
                onClick = { navController.navigate(Screen.ListaEventos.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Ver todos os eventos (Lista Completa)")
            }


            // 8. A Lista de eventos do dia selecionado
            Text(
                text = if (selectionState?.selection?.firstOrNull() != null) "Eventos do dia" else "Selecione um dia",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(eventosDoDiaSelecionado) { evento ->
                    // Usamos o mesmo 'EventoItem' da sua ListaEventosScreen
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

/**
 * Composable customizado para desenhar o dia no calendário.
 * Ele mostra um ponto (marcador) se houver um evento.
 */
@Composable
fun DayContent(dayState: DayState<DynamicSelectionState>, temEvento: Boolean) {
    val data = dayState.date
    val estaSelecionado = dayState.selectionState.selection.contains(data)

    // Define a cor de fundo (se está selecionado)
    val backgroundColor = if (estaSelecionado) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    // Define a cor do texto (se está selecionado)
    val textColor = if (estaSelecionado) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    // Caixa para centralizar o texto e o marcador
    Box(
        modifier = Modifier
            .size(40.dp) // Tamanho fixo para o dia
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable {
                dayState.selectionState.onDateSelected(data)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = data.dayOfMonth.toString(),
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            // 9. O Marcador (ponto)
            if (temEvento) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (estaSelecionado) textColor else MaterialTheme.colorScheme.primary)
                )
            } else {
                Spacer(modifier = Modifier.size(4.dp)) // Espaço vazio para alinhar
            }
        }
    }
}


/**
 * Esta função foi COPIADA do seu arquivo ListaEventosScreen.kt
 * para que possamos usá-la aqui e mostrar os eventos da mesma forma.
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