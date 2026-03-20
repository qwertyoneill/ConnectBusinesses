package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController,
                 hasNewMatch: Boolean) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Leads,
        BottomNavItem.Matches,
        BottomNavItem.Conversations,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.route == BottomNavItem.Matches.route && hasNewMatch) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}
