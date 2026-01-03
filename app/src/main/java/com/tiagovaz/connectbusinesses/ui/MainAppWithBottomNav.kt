package com.tiagovaz.connectbusinesses.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.tiagovaz.connectbusinesses.ui.navigation.BottomNavBar
import com.tiagovaz.connectbusinesses.ui.navigation.NavGraph
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel
import com.tiagovaz.connectbusinesses.viewmodel.MatchesViewModel

@Composable
fun MainAppWithBottomNav(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val matchesViewModel: MatchesViewModel = hiltViewModel()

    val hasNewMatch by matchesViewModel.hasNewMatch.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                hasNewMatch = hasNewMatch
            )
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            paddingValues = innerPadding,
            authViewModel = authViewModel
        )
    }
}
