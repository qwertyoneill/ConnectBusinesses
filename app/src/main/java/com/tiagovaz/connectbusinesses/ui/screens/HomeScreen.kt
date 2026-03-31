package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.ui.navigation.BottomNavItem
import com.tiagovaz.connectbusinesses.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHome()
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Olá, ${state.userName} 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tens ${state.newMatchesCount} matches e ${state.unreadConversationsCount} conversas por ler.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeStatCard(
                        title = "Leads",
                        value = state.availableLeadsCount.toString(),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(BottomNavItem.Leads.route) }
                    )
                    HomeStatCard(
                        title = "Matches",
                        value = state.newMatchesCount.toString(),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(BottomNavItem.Matches.route) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeStatCard(
                        title = "Conversas",
                        value = state.unreadConversationsCount.toString(),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(BottomNavItem.Conversations.route) }
                    )
                    HomeStatCard(
                        title = "Meus Leads",
                        value = state.myLeadsCount.toString(),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("myLeads") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Ações rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        text = "Criar Lead",
                        icon = { Icon(Icons.Default.AddBusiness, contentDescription = null) },
                        onClick = {
                            navController.navigate("createLead")
                        }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickSecondaryButton(
                            text = "Ver Leads",
                            icon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(BottomNavItem.Leads.route) }
                        )
                        QuickSecondaryButton(
                            text = "Matches",
                            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(BottomNavItem.Matches.route) }
                        )
                    }

                    QuickSecondaryButton(
                        text = "Abrir Conversas",
                        icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(BottomNavItem.Conversations.route) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Atividade",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                HomeActivityCard(
                    title = "Novos matches",
                    subtitle = if (state.newMatchesCount > 0) {
                        "Tens ${state.newMatchesCount} match(es) para abrir."
                    } else {
                        "Ainda não tens matches novos."
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                HomeActivityCard(
                    title = "Mensagens por ler",
                    subtitle = if (state.unreadConversationsCount > 0) {
                        "Tens ${state.unreadConversationsCount} mensagem(ns) à espera."
                    } else {
                        "Sem mensagens por ler."
                    }
                )

                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    AssistChip(
                        onClick = { viewModel.loadHome() },
                        label = { Text("Erro: $error · Tocar para atualizar") }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1.25f)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun QuickSecondaryButton(
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun HomeActivityCard(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}