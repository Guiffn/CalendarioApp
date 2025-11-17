package com.example.calendario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
// --- MUDANÇAS DE IMPORTAÇÃO ---
import kotlinx.datetime.Instant // Usado para converter o Long do Firebase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate // Usado para CONVERTER para java.time
import kotlinx.datetime.toLocalDateTime
// A importação "kotlinx.datetime.LocalDate" foi REMOVIDA
import java.text.SimpleDateFormat
import java.time.LocalDate // MUDANÇA: AGORA SÓ USAMOS ESTE LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController, viewModel: EventoViewModel) {

    val eventos by viewModel.eventos.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // --- MUDANÇA DE TIPO ---
    // 1. Estado de seleção: Agora usa 'java.time.LocalDate'
    var selection by remember { mutableStateOf<LocalDate?>(null) }

    // 2. Lógica dos marcadores: Converte o Long para 'java.time.LocalDate'
    val diasComEvento by remember(eventos) {
        derivedStateOf {
            eventos.map {
                // Converte Long -> Instant -> kotlinx.datetime.LocalDate -> java.time.LocalDate
                Instant.fromEpochMilliseconds(it.data)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .toJavaLocalDate() // Converte para o tipo que a biblioteca do calendário usa
            }.toSet()
        }
    }

    // 3. Lógica para filtrar a lista (agora usa o novo 'selection')
    val eventosDoDiaSelecionado by remember(selection, eventos) {
        derivedStateOf {
            val dataSelecionada = selection
            if (dataSelecionada == null) {
                emptyList()
            } else {
                eventos.filter {
                    // Converte o Long do evento para java.time.LocalDate para comparar
                    val dataEvento = Instant.fromEpochMilliseconds(it.data)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                        .toJavaLocalDate() // Converte para o tipo que a seleção usa
                    dataEvento == dataSelecionada // A comparação agora funciona
                }
            }
        }
    }

    // --- MUDANÇA NA OBTENÇÃO DA DATA ---
    // 4. Configuração do estado do calendário (nova biblioteca)
    val today = LocalDate.now() // Usa java.time.LocalDate.now()
    val currentMonth = YearMonth.from(today)
    val startMonth = currentMonth.minusMonths(100)
    val endMonth = currentMonth.plusMonths(100)
    val firstDayOfWeek = daysOfWeek().first()

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Calendário") },
                actions = {
                    IconButton(onClick = {
                        // Como removemos a lógica de login,
                        // também removemos a de signOut para evitar outro crash.
                        // Firebase.auth.signOut() <-- REMOVIDO

                        // CORREÇÃO: Navega de volta ao Login ao sair
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
            HorizontalCalendar(
                state = calendarState,
                monthHeader = { month ->
                    MonthHeader(
                        month = month,
                        onPrevClicked = {
                            coroutineScope.launch {
                                calendarState.animateScrollToMonth(calendarState.firstVisibleMonth.yearMonth.minusMonths(1))
                            }
                        },
                        onNextClicked = {
                            coroutineScope.launch {
                                calendarState.animateScrollToMonth(calendarState.firstVisibleMonth.yearMonth.plusMonths(1))
                            }
                        }
                    )
                },
                dayContent = { day ->
                    DayContent(
                        day = day,
                        isSelected = selection == day.date,
                        temEvento = diasComEvento.contains(day.date)
                    ) { clickedDay ->
                        selection = if (selection == clickedDay.date) null else clickedDay.date
                    }
                }
            )

            Button(
                onClick = { navController.navigate(Screen.ListaEventos.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Ver todos os eventos (Lista Completa)")
            }

            Text(
                text = if (selection != null) "Eventos do dia" else "Selecione um dia",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(eventosDoDiaSelecionado) { evento ->
                    // Esta chamada agora usa a EventoItem deste ficheiro
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

// Composable para o dia
@Composable
fun DayContent(
    day: CalendarDay,
    isSelected: Boolean,
    temEvento: Boolean,
    onClick: (CalendarDay) -> Unit
) {
    if (day.position == DayPosition.MonthDate) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable { onClick(day) },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
                if (temEvento) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                    )
                } else {
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
    }
}

// Composable para o cabeçalho do mês
@Composable
fun MonthHeader(
    month: CalendarMonth,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevClicked) {
            Icon(Icons.Default.KeyboardArrowLeft, "Mês anterior")
        }
        Text(
            text = "${
                month.yearMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                )
            } ${month.yearMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onNextClicked) {
            Icon(Icons.Default.KeyboardArrowRight, "Próximo mês")
        }
    }
}

// --- ESTA FUNÇÃO É A ÚNICA FONTE ---
// Ela será usada por CalendarioScreen e importada por ListaEventosScreen
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