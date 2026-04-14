package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
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

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
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
                        accessToken = token,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight - 80.dp)
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        isActive = isActive,
                        shadowColor = shadowColor,
                        borderColor = borderColor
                    )
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "🚀",
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Não existem mais leads",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Volta mais tarde para descobrir novas oportunidades.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}