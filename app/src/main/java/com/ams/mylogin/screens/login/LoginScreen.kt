package com.ams.mylogin.screens.login


import android.util.Log

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import com.ams.mylogin.R
import com.ams.mylogin.model.AuthResult
import com.ams.mylogin.navigation.NavRoutes


@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startGoogleSignIn: () -> Unit
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authResult by authViewModel.authResult.collectAsState()
    val scrollState = rememberScrollState()


    LaunchedEffect(authResult) {
        if (authResult is AuthResult.Success) {
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        } else if (authResult is AuthResult.Error) {
            // Opcional: muestra un mensaje de error si el inicio de sesión falla
            Log.e("LoginScreen", (authResult as AuthResult.Error).message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_netflix_antes),
            contentDescription = "Logotipo",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(10.dp))

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
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Contraseña") },
            placeholder = { Text("Contraseña", color = Color.Gray) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = description,
                        tint = Color.White
                    )
                }
            },
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

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                    authViewModel.login(emailState.value, passwordState.value)

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),

            ) {
            Text("Iniciar Sesión", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate(NavRoutes.Register.route)
        }) {
            Text("¿No tienes cuenta? Regístrate", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de inicio de sesión con Google
        GoogleSignInButton(
            isLoading = authResult is AuthResult.LoadingGoogle,
            onClicked = {
                startGoogleSignIn()  // Llama a startGoogleSignIn en MainActivity
            }
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar mensajes de estado
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
            else -> { /* No hacer nada */ }
        }
    }
}
