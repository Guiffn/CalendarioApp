package com.example.calendario.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calendario.ui.screens.CriarEventoScreen
import com.example.calendario.ui.screens.DetalheEventoScreen
import com.example.calendario.ui.screens.ListaEventosScreen
import com.example.calendario.ui.screens.LoginScreen // Importação da nova tela
import com.example.calendario.viewmodel.EventoViewModel
import com.google.firebase.auth.ktx.auth // Importação do Firebase Auth
import com.google.firebase.ktx.Firebase

// 1. Definir as rotas (telas)
sealed class Screen(val route: String) {
    // --- Rota ADICIONADA ---
    object Login : Screen("login")
    object ListaEventos : Screen("lista_eventos")
    object DetalheEvento : Screen("detalhe_evento/{eventoId}") {
        fun createRoute(eventoId: String) = "detalhe_evento/$eventoId"
    }
    // --- Rota MODIFICADA ---
    // Torna o eventoId opcional. Se não for passado, é uma criação.
    // Se for passado, é uma edição.
    object CriarEvento : Screen("criar_evento?eventoId={eventoId}") {
        fun createRoute(eventoId: String?) = "criar_evento?eventoId=$eventoId"
    }
}


// 2. Configurar o NavHost (Navigation Controller)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Instância única do ViewModel compartilhada entre as telas
    val eventoViewModel: EventoViewModel = viewModel()

    // --- LÓGICA DE LOGIN ADICIONADA ---
    // Decide qual tela mostrar primeiro
    val startDestination = if (Firebase.auth.currentUser != null) {
        Screen.ListaEventos.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // --- Rota ADICIONADA ---
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // Rota para a Tela de Lista
        composable(Screen.ListaEventos.route) {
            ListaEventosScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }

        // Rota para a Tela de Criar/Editar
        composable(
            route = Screen.CriarEvento.route,
            // Define o argumento opcional
            arguments = listOf(navArgument("eventoId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId")
            CriarEventoScreen(
                navController = navController,
                viewModel = eventoViewModel,
                eventoId = eventoId // Passa o ID (pode ser nulo)
            )
        }

        // Rota para a Tela de Detalhes
        composable(
            route = Screen.DetalheEvento.route,
            arguments = listOf(navArgument("eventoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId")
            DetalheEventoScreen(
                navController = navController,
                viewModel = eventoViewModel,
                eventoId = eventoId
            )
        }
    }
}