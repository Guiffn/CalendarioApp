package com.example.calendario.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// --- IMPORTAÇÃO ADICIONADA ---
import com.example.calendario.ui.screens.CalendarioScreen
import com.example.calendario.ui.screens.CriarEventoScreen
import com.example.calendario.ui.screens.DetalheEventoScreen
import com.example.calendario.ui.screens.ListaEventosScreen
import com.example.calendario.ui.screens.LoginScreen
import com.example.calendario.viewmodel.EventoViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// 1. Definir as rotas (telas)
sealed class Screen(val route: String) {
    object Login : Screen("login")
    // --- ROTA ADICIONADA ---
    object Calendario : Screen("calendario")
    object ListaEventos : Screen("lista_eventos")
    object DetalheEvento : Screen("detalhe_evento/{eventoId}") {
        fun createRoute(eventoId: String) = "detalhe_evento/$eventoId"
    }
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

    // --- LÓGICA DE LOGIN MODIFICADA ---
    // Decide qual tela mostrar primeiro
    val startDestination = if (Firebase.auth.currentUser != null) {
        // --- MUDANÇA AQUI: A nova tela principal é o Calendário
        Screen.Calendario.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // --- ROTA ADICIONADA ---
        // Agora, quando o LoginScreen navegar para "calendario",
        // esta tela será encontrada e o app não vai mais fechar.
        composable(Screen.Calendario.route) {
            CalendarioScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
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
                eventoId = eventoId
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