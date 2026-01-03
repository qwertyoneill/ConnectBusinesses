package com.tiagovaz.connectbusinesses.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue   // 👈 AQUI
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.ui.components.MatchCard
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel
import com.tiagovaz.connectbusinesses.viewmodel.MatchesViewModel


@Composable
fun MatchesScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val token by authViewModel.token.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(token) {
        token?.let {
            viewModel.loadMatches(it)
            viewModel.markMatchesAsSeen()
        }
    }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        matches.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        onClick = {
                            navController.navigate("leadDetails/${match.lead.id}")
                        }
                    )
                }
            }
        }
    }
}
