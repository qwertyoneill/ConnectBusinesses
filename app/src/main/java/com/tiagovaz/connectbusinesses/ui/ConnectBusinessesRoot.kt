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
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        composable("main") {
            MainAppWithBottomNav(
                authViewModel = authViewModel,
                newMatchesCount = 0,
                unreadChatsCount = 0
            )
        }
    }
}
