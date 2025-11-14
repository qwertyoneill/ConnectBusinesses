package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Outlined.Home, "Home")
    object Leads : BottomNavItem("leads", Icons.Outlined.Search, "Leads")
    object Businesses : BottomNavItem("businesses", Icons.Outlined.Business, "Empresas")
    object Profile : BottomNavItem("profile", Icons.Outlined.Person, "Perfil")
}
