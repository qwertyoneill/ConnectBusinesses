package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel
import com.tiagovaz.connectbusinesses.viewmodel.MatchesViewModel

@Composable
fun MatchesScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val token by authViewModel.token.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(token) {
        token?.let { viewModel.loadMatches(it) }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            matches.isEmpty() -> Text("Ainda não tens matches")
            else -> Column {
                matches.forEach { match ->
                    Text("🤝 ${match.lead.companyName}")
                }
            }
        }
    }
}
