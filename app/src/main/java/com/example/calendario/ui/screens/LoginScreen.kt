package com.example.calendario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.calendario.ui.Screen

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    // Usamos um Scaffold para dar uma cor de fundo que combina com o tema
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp), // Aumentamos o padding
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Ícone
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Ícone do Calendário",
                tint = MaterialTheme.colorScheme.primary, // Cor do tema
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Títulos
            Text(
                "Meu Calendário",
                style = MaterialTheme.typography.headlineLarge, // Fonte maior
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Entre para organizar seus eventos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 3. Card para agrupar os campos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Person, "Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text("Senha") },
                        leadingIcon = { Icon(Icons.Default.Lock, "Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 4. Botão de Login (Corrigido)
            Button(
                onClick = {
                    // --- CORREÇÃO IMPORTANTE ---
                    // Navega para a TELA CERTA (Calendario) após o login.
                    navController.navigate(Screen.Calendario.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }

            // 5. Botão de Registro (Corrigido e com texto)
            TextButton(
                onClick = {
                    // --- CORREÇÃO IMPORTANTE ---
                    // Navega para a TELA CERTA (Calendario) após o "registro".
                    navController.navigate(Screen.Calendario.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            ) {
                Text("Não tem uma conta? Registre-se")
            }
        }
    }
}