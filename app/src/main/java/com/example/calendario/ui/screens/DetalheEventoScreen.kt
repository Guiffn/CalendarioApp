package com.example.calendario.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.calendario.ui.Screen 
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (evento == null) {
                    Text("Evento não encontrado ou carregando...")
                } else {
                    DetalheConteudo(evento!!)

                    Spacer(modifier = Modifier.weight(1f))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                // --- CHAMADA DE ROTA CORRIGIDA ---
                                navController.navigate(Screen.CriarEvento.createRoute(evento!!.data, evento?.id))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, "Editar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                eventoId?.let {
                                    viewModel.deletarEvento(it)
                                    navController.popBackStack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, "Excluir")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Excluir")
                        }
                    }
                }
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

    Text(text = "Detalhes: ${evento.exibirDetalhes()}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))

    when (evento) {
        is EventoAniversario -> {
            Text("Aniversariante: ${evento.aniversariante}", style = MaterialTheme.typography.bodyMedium)
        }
        is EventoReuniao -> {
            Text("Local: ${evento.local}", style = MaterialTheme.typography.bodyMedium)
            Text("Participantes: ${evento.participantes.joinToString()}", style = MaterialTheme.typography.bodyMedium)
        }
    }

}
