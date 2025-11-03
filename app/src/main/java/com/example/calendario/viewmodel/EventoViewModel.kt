package com.example.calendario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendario.model.EventoCalendario
// ... (outros imports)
import com.example.calendario.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventoViewModel : ViewModel() {

    private val repository = EventoRepository()

    private val _eventos = MutableStateFlow<List<EventoCalendario>>(emptyList())
    val eventos = _eventos.asStateFlow()

    init {
        carregarEventos()
    }

    fun carregarEventos() {
        viewModelScope.launch {
            _eventos.value = repository.buscarTodosEventos()
        }
    }

    fun buscarEventoPorId(id: String): EventoCalendario? {
        return _eventos.value.find { it.id == id }
    }

    fun adicionarEvento(evento: EventoCalendario) {
        viewModelScope.launch {
            repository.adicionarEvento(evento)
            carregarEventos() // Recarrega a lista
        }
    }

    // --- ADICIONE ESTA FUNÇÃO ---
    fun atualizarEvento(evento: EventoCalendario) {
        viewModelScope.launch {
            repository.atualizarEvento(evento)
            carregarEventos() // Recarrega a lista
        }
    }

    // --- ADICIONE ESTA TAMBÉM (você vai precisar dela na tela de detalhes) ---
    fun deletarEvento(eventoId: String) {
        viewModelScope.launch {
            repository.deletarEvento(eventoId)
            carregarEventos() // Recarrega a lista
        }
    }
}