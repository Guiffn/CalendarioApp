package com.example.calendario.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.model.EventoAniversario
import com.example.calendario.model.EventoReuniao
import com.example.calendario.viewmodel.EventoViewModel

// Esta é uma tela de criação simplificada.
// A Pessoa 3 (UI Avançada) irá adicionar o DatePicker aqui.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEventoScreen(navController: NavController, viewModel: EventoViewModel) {

    var titulo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("GERAL") } // "GERAL", "ANIVERSARIO", "REUNIAO"
    var detalhesExtras by remember { mutableStateOf("") } // Para aniversariante ou local

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Novo Evento") },
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
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título do Evento") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // TODO: Pessoa 3 vai substituir isso por um DatePicker
            Text("Aqui entrará o DatePicker da Pessoa 3. Por enquanto, a data será a atual.")
            Spacer(Modifier.height(8.dp))

            // Simples seletor de tipo (Pode ser melhorado com Dropdown)
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it.uppercase() },
                label = { Text("Tipo (GERAL, ANIVERSARIO, REUNIAO)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            if (tipo == "ANIVERSARIO") {
                OutlinedTextField(
                    value = detalhesExtras,
                    onValueChange = { detalhesExtras = it },
                    label = { Text("Nome do Aniversariante") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (tipo == "REUNIAO") {
                OutlinedTextField(
                    value = detalhesExtras,
                    onValueChange = { detalhesExtras = it },
                    label = { Text("Local da Reunião") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Criar o objeto de evento correto com base no tipo
                    val dataAtual = System.currentTimeMillis()

                    val novoEvento = when (tipo) {
                        "ANIVERSARIO" -> EventoAniversario(aniversariante = detalhesExtras).apply {
                            this.titulo = titulo
                            this.data = dataAtual
                        }
                        "REUNIAO" -> EventoReuniao(local = detalhesExtras, participantes = listOf("Eu")).apply {
                            this.titulo = titulo
                            this.data = dataAtual
                        }
                        else -> com.example.calendario.model.EventoCalendario(
                            titulo = titulo,
                            data = dataAtual
                        )
                    }

                    viewModel.adicionarEvento(novoEvento)
                    navController.popBackStack() // Volta para a lista
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Evento")
            }
        }
    }
}