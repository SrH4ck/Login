package com.ams.mylogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ams.mylogin.model.AuthResult
import com.ams.mylogin.navigation.NavRoutes
import com.ams.mylogin.screens.home.HomeScreen
import com.ams.mylogin.screens.login.AuthViewModel
import com.ams.mylogin.screens.login.LoginScreen
import com.ams.mylogin.screens.login.RegisterScreen
import com.ams.mylogin.screens.splash.SplashScreen
import com.ams.mylogin.ui.theme.MyLoginTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: androidx.navigation.NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("723565181442-hb5ejni7udfjv0dj0qg8vfpvbku0aerc.apps.googleusercontent.com") // Usar el ID de cliente desde strings.xml
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            navController = rememberNavController()
            MyLoginTheme {
                MyApp(
                    authViewModel = authViewModel,
                    startGoogleSignIn = { startGoogleSignIn() }
                )
            }
        }
    }

    private fun startGoogleSignIn() {
        // Cierra la sesión de la cuenta de Google si ya hay una sesión activa
        googleSignInClient.signOut().addOnCompleteListener(this) {

        }
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    authViewModel.signInWithGoogle(
                        idToken,
                        onSuccess = {
                            // No es necesario realizar la navegación aquí
                            // `authResult` se actualizará en `AuthViewModel`
                        },
                        onFailure = { exception ->
                            Log.e("MainActivity", "Error en el inicio de sesión con Google", exception)
                        }
                    )
                }
            } catch (e: ApiException) {
                Log.e("MainActivity", "Error en el inicio de sesión con Google", e)
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

@Composable
fun MyApp(authViewModel: AuthViewModel, startGoogleSignIn: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        modifier = Modifier
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController, authViewModel)
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(navController, authViewModel, startGoogleSignIn)
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController, authViewModel)
        }
    }
}
