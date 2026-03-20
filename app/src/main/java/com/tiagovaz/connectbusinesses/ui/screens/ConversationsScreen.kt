package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.viewmodel.ConversationsViewModel

@Composable
fun ConversationsScreen(
    navController: NavController,
    viewModel: ConversationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
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

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error ?: "Erro desconhecido",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        state.items.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ainda não tens conversas 💬")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items) { conversation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    "chat/${conversation.conversation_id}/" +
                                            "${conversation.other_first_name ?: ""}/" +
                                            "${conversation.other_last_name ?: ""}"
                                )
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = listOfNotNull(
                                    conversation.other_first_name,
                                    conversation.other_last_name
                                ).joinToString(" ").ifBlank { "Utilizador" },
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = conversation.lead_name ?: "Lead",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = conversation.last_message_text ?: "Sem mensagens ainda",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (conversation.unread_count > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                AssistChip(
                                    onClick = {},
                                    label = { Text("${conversation.unread_count} por ler") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}