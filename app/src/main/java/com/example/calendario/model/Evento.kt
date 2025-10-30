package com.example.calendario.model // Verifique se o nome do seu pacote está correto

import com.google.firebase.firestore.DocumentId

open class EventoCalendario(
    // Agora que o import está correto, o erro aqui some.
    @DocumentId
    var id: String? = null,
    var titulo: String = "",
    var data: Long = 0L,
    var tipo: String = "GERAL"
) {
    // O Firestore precisa de um construtor vazio para funcionar.
    // Ele usa este construtor para criar o objeto antes de preencher os campos.
    constructor() : this(null, "", 0L, "GERAL")

    open fun exibirDetalhes(): String {
        return "Evento Padrão: $titulo"
    }
}


data class EventoAniversario(
    val aniversariante: String = ""
) : EventoCalendario() { // A herança acontece aqui, nos dois pontos ':'

    // O bloco 'init' é executado assim que um objeto 'EventoAniversario' é criado.
    // É o lugar perfeito para definir o tipo específico.
    init {
        tipo = "ANIVERSARIO"
    }

    // Usamos 'override' para SOBRESCREVER a função da classe Pai.
    // Agora, quando chamarmos 'exibirDetalhes()' em um EventoAniversario, este texto será mostrado.
    override fun exibirDetalhes(): String {
        return "Hoje é dia de festa! Aniversário de $aniversariante! 🥳"
    }
}

data class EventoReuniao(
    val local: String = "",
    val participantes: List<String> = emptyList() // Uma lista para guardar os nomes
) : EventoCalendario() {

    init {
        tipo = "REUNIAO"
    }

    // Novamente, sobrescrevemos a função para dar detalhes específicos de uma reunião.
    override fun exibirDetalhes(): String {
        // 'joinToString' é uma função útil do Kotlin para formatar listas.
        val listaDeNomes = participantes.joinToString(separator = ", ")
        return "Reunião em '$local'. Participantes: $listaDeNomes."
    }
}
