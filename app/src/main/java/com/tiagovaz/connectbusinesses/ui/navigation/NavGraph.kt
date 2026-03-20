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

        composable("conversations") {
            ConversationsScreen(navController = navController)
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
        composable("conversations") {
            ConversationsScreen(navController = navController)
        }

        composable("chat/{conversationId}/{firstName}/{lastName}") { backStack ->
            val conversationId = backStack.arguments?.getString("conversationId")?.toIntOrNull() ?: 0
            val firstName = backStack.arguments?.getString("firstName").orEmpty()
            val lastName = backStack.arguments?.getString("lastName").orEmpty()
            val fullName = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

            ChatScreen(
                conversationId = conversationId,
                otherUserName = fullName
            )
        }
    }
}
