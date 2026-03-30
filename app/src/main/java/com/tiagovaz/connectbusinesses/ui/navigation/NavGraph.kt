package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tiagovaz.connectbusinesses.ui.screens.ChatScreen
import com.tiagovaz.connectbusinesses.ui.screens.ConversationsScreen
import com.tiagovaz.connectbusinesses.ui.screens.HomeScreen
import com.tiagovaz.connectbusinesses.ui.screens.LeadsScreen
import com.tiagovaz.connectbusinesses.ui.screens.MatchesScreen
import com.tiagovaz.connectbusinesses.ui.screens.ProfileScreen
import com.tiagovaz.connectbusinesses.ui.navigation.LeadDetailsScreen
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel
import com.tiagovaz.connectbusinesses.ui.screens.CreateLeadScreen

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
            HomeScreen(
                navController = navController
            )
        }

        composable(BottomNavItem.Leads.route) {
            LeadsScreen(
                navController = navController,
                authViewModel = authViewModel
            )
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

        composable(BottomNavItem.Matches.route) {
            MatchesScreen(navController = navController)
        }

        composable(BottomNavItem.Conversations.route) {
            ConversationsScreen(navController = navController)
        }

        composable("chat/{conversationId}") { backStack ->
            val conversationId = backStack.arguments?.getString("conversationId")?.toIntOrNull() ?: 0

            ChatScreen(
                conversationId = conversationId,
                otherUserName = ""
            )
        }
        composable("createLead") {
            CreateLeadScreen(navController = navController)
        }
    }
}