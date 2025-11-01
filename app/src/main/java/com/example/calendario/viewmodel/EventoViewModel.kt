package com.example.calendario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendario.model.EventoAniversario
import com.example.calendario.model.EventoCalendario
import com.example.calendario.model.EventoReuniao
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
            carregarEventos()
        }
    }


}