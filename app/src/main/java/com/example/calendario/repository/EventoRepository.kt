package com.example.calendario.repository // Verifique o nome do pacote

import com.example.calendario.model.EventoAniversario
import com.example.calendario.model.EventoCalendario
import com.example.calendario.model.EventoReuniao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventoRepository {


    private val db = FirebaseFirestore.getInstance().collection("eventos")

    suspend fun adicionarEvento(evento: EventoCalendario) {
        try {

            db.add(evento).await()
        } catch (e: Exception) {

            println("Erro ao adicionar evento: ${e.message}")
        }
    }

    suspend fun buscarTodosEventos(): List<EventoCalendario> {
        return try {
            val snapshot = db.orderBy("data").get().await()

            snapshot.documents.mapNotNull { document ->
                when (document.getString("tipo")) {
                    "ANIVERSARIO" -> document.toObject(EventoAniversario::class.java)
                    "REUNIAO" -> document.toObject(EventoReuniao::class.java)
                    else -> document.toObject(EventoCalendario::class.java)
                }
            }
        } catch (e: Exception) {
            println("Erro ao buscar eventos: ${e.message}")
            emptyList()
        }
    }

    suspend fun atualizarEvento(evento: EventoCalendario) {
        evento.id?.let { id ->
            try {
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
