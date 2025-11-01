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
import com.example.calendario.viewmodel.EventoViewModel

// 1. Definir as rotas (telas)
sealed class Screen(val route: String) {
    object ListaEventos : Screen("lista_eventos")
    object DetalheEvento : Screen("detalhe_evento/{eventoId}") {
        fun createRoute(eventoId: String) = "detalhe_evento/$eventoId"
    }
    object CriarEvento : Screen("criar_evento")
}


// 2. Configurar o NavHost (Navigation Controller)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Instância única do ViewModel compartilhada entre as telas
    val eventoViewModel: EventoViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.ListaEventos.route) {

        // Rota para a Tela de Lista (item 3 da sua tarefa)
        composable(Screen.ListaEventos.route) {
            ListaEventosScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }

        // Rota para a Tela de Criar/Editar (item 1 da sua tarefa)
        composable(Screen.CriarEvento.route) {
            CriarEventoScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }

        // Rota para a Tela de Detalhes (item 1 da sua tarefa)
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