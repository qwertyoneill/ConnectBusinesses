package com.tiagovaz.connectbusinesses.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import com.tiagovaz.connectbusinesses.ui.navigation.BottomNavBar
import com.tiagovaz.connectbusinesses.ui.navigation.NavGraph
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel

@Composable
fun MainAppWithBottomNav(
    authViewModel: AuthViewModel,
    newMatchesCount: Int = 0,
    unreadChatsCount: Int = 0
) {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val showBottomBar = !currentRoute.startsWith("chat/")

    Scaffold(
        bottomBar = {

            if (showBottomBar) {

                BottomNavBar(
                    navController = navController,
                    newMatchesCount = newMatchesCount,
                    unreadChatsCount = unreadChatsCount
                )

            }

        }
    ) { innerPadding ->

        NavGraph(
            navController = navController,
            paddingValues = if (showBottomBar) innerPadding else PaddingValues(0.dp),
            authViewModel = authViewModel
        )

    }
}