package com.example.calendario.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.model.EventoAniversario
import com.example.calendario.model.EventoCalendario
import com.example.calendario.model.EventoReuniao
import com.example.calendario.viewmodel.EventoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheEventoScreen(
    navController: NavController,
    viewModel: EventoViewModel,
    eventoId: String?
) {
    var evento by remember { mutableStateOf<EventoCalendario?>(null) }

    // Busca o evento no ViewModel quando o eventoId mudar
    LaunchedEffect(eventoId) {
        if (eventoId != null) {
            evento = viewModel.buscarEventoPorId(eventoId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(evento?.titulo ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (evento == null) {
                Text("Evento não encontrado ou carregando...")
            } else {
                // Exibe detalhes com base no tipo (Polimorfismo)
                DetalheConteudo(evento!!)
            }
        }
    }
}

@Composable
fun DetalheConteudo(evento: EventoCalendario) {
    val dataFormatada = remember(evento.data) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(evento.data))
    }

    Text(text = "Título: ${evento.titulo}", style = MaterialTheme.typography.titleLarge)
    Text(text = "Data: $dataFormatada", style = MaterialTheme.typography.bodyMedium)
    Text(text = "Tipo: ${evento.tipo}", style = MaterialTheme.typography.bodyMedium)

    // Usa o polimorfismo para mostrar detalhes específicos
    Text(text = "Detalhes: ${evento.exibirDetalhes()}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))

    // Mostra campos extras se for um tipo específico
    when (evento) {
        is EventoAniversario -> {
            Text("Aniversariante: ${evento.aniversariante}", style = MaterialTheme.typography.bodyMedium)
        }
        is EventoReuniao -> {
            Text("Local: ${evento.local}", style = MaterialTheme.typography.bodyMedium)
            Text("Participantes: ${evento.participantes.joinToString()}", style = MaterialTheme.typography.bodyMedium)
        }
    }

    // TODO: Pessoa 3 pode adicionar um DatePicker ou botões de editar/excluir aqui
}