package com.example.calendario.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calendario.ui.screens.CalendarioScreen
import com.example.calendario.ui.screens.CriarEventoScreen
import com.example.calendario.ui.screens.DetalheEventoScreen
import com.example.calendario.ui.screens.SettingsScreen
import com.example.calendario.viewmodel.EventoViewModel

sealed class Screen(val route: String) {
    object Calendario : Screen("calendario")
    object Settings : Screen("settings")
    object DetalheEvento : Screen("detalhe_evento/{eventoId}") {
        fun createRoute(eventoId: String) = "detalhe_evento/$eventoId"
    }
    // --- ROTA ATUALIZADA ---
    object CriarEvento : Screen("criar_evento?dataSelecionada={dataSelecionada}&eventoId={eventoId}") {
        fun createRoute(dataSelecionada: Long, eventoId: String?) = "criar_evento?dataSelecionada=$dataSelecionada&eventoId=$eventoId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val eventoViewModel: EventoViewModel = viewModel()
    val startDestination = Screen.Calendario.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Calendario.route) {
            CalendarioScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }

        composable(
            route = Screen.CriarEvento.route,
            // --- ARGUMENTOS ATUALIZADOS ---
            arguments = listOf(
                navArgument("dataSelecionada") {
                    type = NavType.LongType
                    defaultValue = -1L // Valor padrão, caso nenhuma data seja passada
                },
                navArgument("eventoId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId")
            val dataSelecionada = backStackEntry.arguments?.getLong("dataSelecionada")
            CriarEventoScreen(
                navController = navController,
                viewModel = eventoViewModel,
                eventoId = eventoId,
                // --- NOVO PARÂMETRO ---
                dataSelecionada = if (dataSelecionada == -1L) null else dataSelecionada
            )
        }

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
