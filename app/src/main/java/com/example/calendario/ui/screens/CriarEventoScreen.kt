package com.example.calendario.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.model.EventoAniversario
import com.example.calendario.model.EventoCalendario
import com.example.calendario.model.EventoReuniao
import com.example.calendario.viewmodel.EventoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEventoScreen(
    navController: NavController,
    viewModel: EventoViewModel,
    eventoId: String? // --- PARÂMETRO ADICIONADO ---
) {

    // --- LÓGICA DE EDIÇÃO ADICIONADA ---
    val isEditing = eventoId != null
    var eventoToEdit by remember { mutableStateOf<EventoCalendario?>(null) }

    // Campos de estado
    var titulo by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf("GERAL") }
    var detalhesExtras by rememberSaveable { mutableStateOf("") }
    var selectedDateMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    // Busca o evento se estiver editando
    LaunchedEffect(eventoId) {
        if (isEditing) {
            eventoToEdit = viewModel.buscarEventoPorId(eventoId!!)
            eventoToEdit?.let { evento ->
                titulo = evento.titulo
                tipo = evento.tipo
                selectedDateMillis = evento.data
                detalhesExtras = when (evento) {
                    is EventoAniversario -> evento.aniversariante
                    is EventoReuniao -> evento.local
                    else -> ""
                }
            }
        }
    }
    // --- FIM DA LÓGICA DE EDIÇÃO ---

    // --- Lógica do Date/Time Picker (Tarefa Pessoa 3) ---
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    var selectedHour by rememberSaveable(selectedDateMillis) { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by rememberSaveable(selectedDateMillis) { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = true
    )

    val dateTimeFormatter = remember(selectedDateMillis) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDateMillis
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(cal.time)
    }

    val tiposDeEvento = listOf("GERAL", "ANIVERSARIO", "REUNIAO")
    var tipoExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    showTimePicker = true
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            // --- CORREÇÃO AQUI ---
            // O 'state' já contém a data inicial, não precisa passar de novo.
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Selecione a Hora") },
            text = { TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = selectedDateMillis
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    selectedDateMillis = cal.timeInMillis
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // Título dinâmico
                title = { Text(if (isEditing) "Editar Evento" else "Criar Novo Evento") },
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

            OutlinedTextField(
                value = dateTimeFormatter,
                onValueChange = {},
                readOnly = true,
                label = { Text("Data e Hora") },
                trailingIcon = { Icon(Icons.Default.DateRange, "Selecionar Data e Hora") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = tipoExpanded,
                onExpandedChange = { tipoExpanded = !tipoExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Evento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = tipoExpanded,
                    onDismissRequest = { tipoExpanded = false }
                ) {
                    tiposDeEvento.forEach { tipoItem ->
                        DropdownMenuItem(
                            text = { Text(tipoItem) },
                            onClick = {
                                tipo = tipoItem
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }
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
                    val dataFinal = selectedDateMillis
                    // --- LÓGICA DE SALVAR/ATUALIZAR ---
                    if (isEditing) {
                        // Atualiza o objeto existente
                        eventoToEdit?.apply {
                            this.titulo = titulo
                            this.data = dataFinal
                            this.tipo = tipo
                            when (this) {
                                is EventoAniversario -> this.aniversariante = detalhesExtras
                                is EventoReuniao -> this.local = detalhesExtras
                            }
                        }?.let {
                            viewModel.atualizarEvento(it) // <-- Agora funciona
                        }
                    } else {
                        // Cria um novo objeto
                        val novoEvento = when (tipo) {
                            "ANIVERSARIO" -> EventoAniversario(aniversariante = detalhesExtras).apply {
                                this.titulo = titulo
                                this.data = dataFinal
                            }
                            "REUNIAO" -> EventoReuniao(local = detalhesExtras, participantes = listOf("Eu")).apply {
                                this.titulo = titulo
                                this.data = dataFinal
                            }
                            else -> com.example.calendario.model.EventoCalendario(
                                titulo = titulo,
                                data = dataFinal
                            )
                        }
                        viewModel.adicionarEvento(novoEvento)
                    }
                    navController.popBackStack() // Volta para a lista (ou detalhes)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Texto do botão dinâmico
                Text(if (isEditing) "Atualizar Evento" else "Salvar Evento")
            }
        }
    }
}