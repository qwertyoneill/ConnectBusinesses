package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.ui.components.LeadCard
import com.tiagovaz.connectbusinesses.ui.components.SwipeStack
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel
import com.tiagovaz.connectbusinesses.viewmodel.LeadsViewModel

@Composable
fun LeadsScreen(
    navController: NavController,
    leadsViewModel: LeadsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val token by authViewModel.token.collectAsState(initial = null)
    val leads by leadsViewModel.leads.collectAsState(initial = emptyList())
    val isLoading by leadsViewModel.isLoading.collectAsState()

    // ⚡️ Buscar leads quando token disponível
    LaunchedEffect(token) {
        token?.let { leadsViewModel.fetchLeads(it) }
    }

    // 📱 Pegar dimensões do ecrã para adaptação automática
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp, vertical = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            leads.isNotEmpty() -> {
                // 💫 SwipeStack ocupa o ecrã todo — imagem fullscreen
                SwipeStack(
                    items = leads,
                    onSwipeLeft = { /* opcional: recusar lead */ },
                    onSwipeRight = { /* opcional: aceitar lead */ }
                ) { lead, isActive, shadowColor, borderColor ->
                    LeadCard(
                        lead = lead,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight - 32.dp)
                            .padding(0.dp),
                        isActive = isActive,
                        shadowColor = shadowColor,
                        borderColor = borderColor
                    )
                }
            }

            else -> {
                Text(
                    text = "Sem leads disponíveis.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
