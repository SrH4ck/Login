package com.ams.mylogin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.ams.mylogin.screens.home.HomeScreen
import com.ams.mylogin.screens.login.AuthViewModel
import com.ams.mylogin.screens.login.LoginScreen
import com.ams.mylogin.screens.login.RegisterScreen
import com.ams.mylogin.screens.splash.SplashScreen

/*@Composable
fun AppNavigation(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = NavRoutes.Splash.route) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController, authViewModel)
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController, authViewModel)
        }
    }
}*/
