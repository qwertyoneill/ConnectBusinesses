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
import kotlin.collections.isNotEmpty

@Composable
fun LeadsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    leadsViewModel: LeadsViewModel = hiltViewModel()
) {
    val token by authViewModel.token.collectAsState()
    val leads by leadsViewModel.leads.collectAsState(initial = emptyList())
    val isLoading by leadsViewModel.isLoading.collectAsState()
    android.util.Log.d(
        "AUTH_TOKEN",
        "TOKEN NO SCREEN: ${token?.take(40)}"
    )


    LaunchedEffect(token) {
        android.util.Log.d("TOKEN_SCREEN", "TOKEN NO SCREEN: $token")
        if (!token.isNullOrEmpty()) {
            android.util.Log.d("TOKEN_SCREEN", "CHAMAR fetchLeads()")
            leadsViewModel.fetchLeads(token!!)
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

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
                SwipeStack(
                    items = leads,
                    onSwipeLeft = { lead ->
                        token?.let { leadsViewModel.sendSwipe(it, lead.id, "pass") }
                    },
                    onSwipeRight = { lead ->
                        token?.let { leadsViewModel.sendSwipe(it, lead.id, "like") }
                    }
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
