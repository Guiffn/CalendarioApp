package com.example.calendario.model // Verifique se o nome do seu pacote está correto

import com.google.firebase.firestore.DocumentId

open class EventoCalendario(
    @DocumentId
    var id: String? = null,
    var titulo: String = "",
    var data: Long = 0L,
    var tipo: String = "GERAL"
) {

    constructor() : this(null, "", 0L, "GERAL")

    open fun exibirDetalhes(): String {

        return if (titulo.isNotBlank()) "Evento Padrão: $titulo" else "Evento Padrão"
    }
}


data class EventoAniversario(
    var aniversariante: String = ""
) : EventoCalendario() {


    init {
        tipo = "ANIVERSARIO"
    }


    override fun exibirDetalhes(): String {
        return "Hoje é dia de festa! Aniversário de $aniversariante! 🥳"
    }
}

data class EventoReuniao(
    var local: String = "",
    val participantes: List<String> = emptyList()
) : EventoCalendario() {

    init {
        tipo = "REUNIAO"
    }

    override fun exibirDetalhes(): String {
        val listaDeNomes = participantes.joinToString(separator = ", ")
        return "Reunião em '$local'. Participantes: $listaDeNomes."
    }
}