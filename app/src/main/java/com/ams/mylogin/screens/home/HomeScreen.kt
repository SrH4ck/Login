package com.ams.mylogin.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

import com.ams.mylogin.model.AuthResult
import com.ams.mylogin.navigation.NavRoutes
import com.ams.mylogin.screens.login.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authResult by authViewModel.authResult.collectAsState()

    // Observa los cambios en authResult
    LaunchedEffect(authResult) {
        if (authResult !is AuthResult.Success) {
            // Si el usuario no está autenticado, navega al login
            navController.navigate(NavRoutes.Login.route) {
                popUpTo(NavRoutes.Home.route) { inclusive = true }
            }
        }
    }

    // Interfaz de usuario de la pantalla de inicio
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a la pantalla de inicio")

        Button(onClick = {
            authViewModel.logout()
        }) {
            Text("Cerrar Sesión")
        }
    }
}




