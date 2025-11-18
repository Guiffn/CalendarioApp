package com.example.calendario.viewmodel

import androidx.compose.foundation.gestures.forEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendario.model.EventoCalendario
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

    private fun carregarEventos() {
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

    fun atualizarEvento(evento: EventoCalendario) {
        viewModelScope.launch {
            repository.atualizarEvento(evento)
            carregarEventos() // Recarrega a lista
        }
    }

    fun deletarEvento(eventoId: String) {
        viewModelScope.launch {
            repository.deletarEvento(eventoId)
            carregarEventos() // Recarrega a lista
        }
    }

    // --- FUNÇÃO CORRIGIDA ---
    fun deletarTodosEventos() {
        viewModelScope.launch {
            // Pega a lista atual de eventos
            val todosEventos = _eventos.value

            // Itera sobre cada evento e solicita a exclusão no repositório
            todosEventos.forEach { evento ->
                evento.id?.let { repository.deletarEvento(it) }
            }

            // Após deletar todos, recarrega a lista (que agora estará vazia)
            carregarEventos()
        }
    }
}
