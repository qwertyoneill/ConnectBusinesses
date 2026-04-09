package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navController: NavController,
    newMatchesCount: Int = 0,
    unreadChatsCount: Int = 0
) {

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
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },

                icon = {

                    BadgedBox(
                        badge = {

                            when (item.route) {

                                BottomNavItem.Matches.route -> {
                                    if (newMatchesCount > 0) {
                                        Badge {
                                            Text(
                                                text =
                                                    if (newMatchesCount > 99) "99+"
                                                    else newMatchesCount.toString()
                                            )
                                        }
                                    }
                                }

                                BottomNavItem.Conversations.route -> {
                                    if (unreadChatsCount > 0) {
                                        Badge {
                                            Text(
                                                text =
                                                    if (unreadChatsCount > 99) "99+"
                                                    else unreadChatsCount.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },

                label = {
                    Text(item.label)
                }
            )
        }
    }
}