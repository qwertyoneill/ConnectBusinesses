package com.tiagovaz.connectbusinesses.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.tiagovaz.connectbusinesses.ui.navigation.BottomNavBar
import com.tiagovaz.connectbusinesses.ui.navigation.NavGraph
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel

@Composable
fun MainAppWithBottomNav(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            paddingValues = innerPadding,
            authViewModel = authViewModel
        )
    }
}

