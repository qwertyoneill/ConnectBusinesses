package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
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
            HomeScreen(authViewModel)
        }

        composable(BottomNavItem.Leads.route) {
            LeadsScreen(navController)
        }

        composable(BottomNavItem.Businesses.route) {
            BusinessesScreen()
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(navController, authViewModel)
        }

        composable("leadDetails/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")!!
            LeadDetailsScreen(id, navController)
        }
    }
}

