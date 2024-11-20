package com.ams.mylogin.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.ams.mylogin.R
import com.ams.mylogin.model.AuthResult
import com.ams.mylogin.navigation.NavRoutes

@Composable
fun RegisterScreen(
    navController: NavHostController? = null,
    authViewModel: AuthViewModel
) {
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val authResult by authViewModel.authResult.collectAsState()

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        Image(
            painter = painterResource(id = R.drawable.logo_netflix_antes),
            contentDescription = "Logotipo",
            modifier = Modifier
                .size(200.dp),
            contentScale = ContentScale.Crop


        )

        // Campo de texto para el nombre
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Nombre") },
            placeholder = { Text("Nombre", color = Color.Gray) },

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.DarkGray,
                focusedContainerColor = Color.DarkGray,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Campo de texto para el email
        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            placeholder = { Text("Email", color = Color.Gray) },

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.DarkGray,
                focusedContainerColor = Color.DarkGray,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Campo de texto para la contraseña
        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Contraseña") },
            placeholder = { Text("Contraseña", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.DarkGray,
                focusedContainerColor = Color.DarkGray,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botón de registro
        Button(
            onClick = {
                authViewModel.register(nameState.value, emailState.value, passwordState.value)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Registrarse", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Navegar a la pantalla de inicio de sesión
        TextButton(onClick = {
            navController?.navigate(NavRoutes.Login.route)
        }) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Observa el resultado de autenticación
        when (authResult) {
            is AuthResult.Loading -> {
                CircularProgressIndicator(color = Color.White)
            }
            is AuthResult.Error -> {
                Text(
                    text = (authResult as AuthResult.Error).message,
                    color = Color.Red
                )
            }
            else -> {
                // No mostrar nada en otros casos
            }
        }
    }

    // Navegar a la pantalla de inicio cuando el registro sea exitoso
    if (authResult is AuthResult.Success) {
        LaunchedEffect(Unit) {
            navController?.navigate(NavRoutes.Login.route) {
                popUpTo(NavRoutes.Register.route) { inclusive = true }
            }
        }
    }
}
