package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tiagovaz.connectbusinesses.ui.screens.*
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {

        composable(BottomNavItem.Home.route) {
            HomeScreen(authViewModel = authViewModel)
        }

        composable(BottomNavItem.Leads.route) {
            LeadsScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(BottomNavItem.Businesses.route) {
            BusinessesScreen()
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("leadDetails/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")!!
            LeadDetailsScreen(id, navController)
        }

        composable("matches") {
            MatchesScreen(navController = navController)
        }
    }
}
