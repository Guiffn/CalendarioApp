package com.example.calendario.repository // Verifique o nome do pacote

import com.example.calendario.model.EventoAniversario
import com.example.calendario.model.EventoCalendario
import com.example.calendario.model.EventoReuniao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventoRepository {

    // 1. Pega a instância do Firestore e já aponta para a coleção "eventos".
    // Se a coleção não existir, o Firebase a cria automaticamente no primeiro 'add'.
    private val db = FirebaseFirestore.getInstance().collection("eventos")

    suspend fun adicionarEvento(evento: EventoCalendario) {
        try {
            // '.add()' cria um documento com um ID gerado automaticamente.
            // '.await()' faz a corrotina esperar a operação terminar.
            db.add(evento).await()
        } catch (e: Exception) {
            // Em um app real, aqui você logaria o erro.
            println("Erro ao adicionar evento: ${e.message}")
        }
    }

    suspend fun buscarTodosEventos(): List<EventoCalendario> {
        return try {
            val snapshot = db.orderBy("data").get().await() // Busca e ordena pela data

            // 'mapNotNull' transforma a lista de documentos em uma lista de objetos Kotlin.
            snapshot.documents.mapNotNull { document ->
                // A grande jogada: lê o campo "tipo" do documento...
                when (document.getString("tipo")) {
                    // ...e decide qual tipo de objeto criar!
                    "ANIVERSARIO" -> document.toObject(EventoAniversario::class.java)
                    "REUNIAO" -> document.toObject(EventoReuniao::class.java)
                    else -> document.toObject(EventoCalendario::class.java)
                }
            }
        } catch (e: Exception) {
            println("Erro ao buscar eventos: ${e.message}")
            emptyList() // Se der erro, retorna uma lista vazia para não quebrar o app.
        }
    }

    suspend fun atualizarEvento(evento: EventoCalendario) {
        // A atualização precisa do ID. Se o ID for nulo, não fazemos nada.
        evento.id?.let { id ->
            try {
                // '.document(id).set()' substitui completamente os dados do documento pelos novos.
                db.document(id).set(evento).await()
            } catch (e: Exception) {
                println("Erro ao atualizar evento: ${e.message}")
            }
        }
    }

    suspend fun deletarEvento(eventoId: String) {
        try {
            db.document(eventoId).delete().await()
        } catch (e: Exception) {
            println("Erro ao deletar evento: ${e.message}")
        }
    }
}
