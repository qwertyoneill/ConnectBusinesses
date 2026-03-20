package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Leads : BottomNavItem("leads", "Leads", Icons.Default.Search)
    object Matches : BottomNavItem("matches", "Matches", Icons.Default.Favorite)
    object Conversations : BottomNavItem("conversations", "Conversas", Icons.Default.Chat)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
}
