package com.tiagovaz.connectbusinesses.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.tiagovaz.connectbusinesses.ui.login.LoginScreen
import com.tiagovaz.connectbusinesses.ui.login.RegisterScreen
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel

@Composable
fun ConnectBusinessesRoot(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onCreateAccount = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack() // volta ao login
                },
                onBackToLogin = {
                    navController.popBackStack() // também volta ao login
                }
            )
        }


        composable("main") {
            MainAppWithBottomNav(authViewModel)
        }
    }
}


