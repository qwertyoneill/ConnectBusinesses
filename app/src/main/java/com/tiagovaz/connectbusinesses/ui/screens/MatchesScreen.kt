package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.ui.components.MatchCard
import com.tiagovaz.connectbusinesses.viewmodel.MatchesViewModel

@Composable
fun MatchesScreen(
    navController: NavController,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadMatches()
        viewModel.markMatchesAsSeen()
    }

    LaunchedEffect(state.openChatError) {
        state.openChatError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearOpenChatError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    Text("Ainda não tens matches 🤝")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items) { match ->
                        val matchId = match.match_id
                        val isOpening = state.openingMatchId == matchId

                        MatchCard(
                            match = match,
                            isOpening = isOpening,
                            onClick = {
                                if (matchId != null && !isOpening) {
                                    viewModel.openMatchConversation(matchId) { conversationId ->
                                        val fullName = listOfNotNull(
                                            match.other_first_name,
                                            match.other_last_name
                                        ).joinToString(" ").ifBlank { "Utilizador" }

                                        val safeOtherUserName = java.net.URLEncoder.encode(
                                            fullName,
                                            java.nio.charset.StandardCharsets.UTF_8.toString()
                                        )

                                        val safeLeadName = java.net.URLEncoder.encode(
                                            match.lead_name ?: "",
                                            java.nio.charset.StandardCharsets.UTF_8.toString()
                                        )

                                        navController.navigate(
                                            "chat/$conversationId/$safeOtherUserName/${match.lead_id}/$safeLeadName"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}