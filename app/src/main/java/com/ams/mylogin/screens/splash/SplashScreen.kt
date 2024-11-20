package com.ams.mylogin.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ams.mylogin.navigation.NavRoutes
import com.ams.mylogin.screens.login.AuthViewModel
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.scale
import com.ams.mylogin.model.AuthResult

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val fullText = "NETFLIX"
    val letters = fullText.map { it.toString() }
    val visibleLetters = remember { mutableStateListOf(*letters.toTypedArray()) }
    // Verificar si ya hay un usuario autenticado
    val authResult by authViewModel.authResult.collectAsState()

    // Animar la escala de la 'N'
    val nScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Mostrar el texto completo durante 1 segundo
        delay(1000)

        // Animar la desaparición de las letras
        for (i in letters.size - 1 downTo 1) {
            visibleLetters.removeAt(i)
            delay(100) // Esperar 100 milisegundos entre cada letra que desaparece
        }

        // Aplicar efecto de zoom a la 'N'
        nScale.animateTo(
            targetValue = 5f, // Escala final deseada
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutLinearInEasing
            )
        )

        // Esperar antes de navegar
        delay(500)




        // Navegar basado en el estado de autenticación gestionado por AuthViewModel
        if (authResult is AuthResult.Success) {
            // Si el usuario ya está autenticado, navega directamente a Home
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Splash.route) { inclusive = true }  // Elimina Splash de la pila de navegación
            }
        } else {
            // Si no está autenticado, navega a Login
            navController.navigate(NavRoutes.Login.route) {
                popUpTo(NavRoutes.Splash.route) { inclusive = true }  // Elimina Splash de la pila de navegación
            }
        }
    }

    // Diseño de la pantalla de splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in letters.indices) {
                val isVisible = index < visibleLetters.size
                AnimatedVisibility(
                    visible = isVisible,
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis = 150
                        )
                    )
                ) {
                    if (index == 0) {
                        // La 'N', aplicar animación de escala

                        Text(
                            text = letters[index],
                            color = Color.Red,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.scale(nScale.value)
                        )
                    } else {
                        // Otras letras
                        Text(
                            text = letters[index],
                            color = Color.Red,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
