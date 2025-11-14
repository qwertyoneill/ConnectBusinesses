package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tiagovaz.connectbusinesses.viewmodel.AuthViewModel
import com.tiagovaz.connectbusinesses.viewmodel.LeadDetailsViewModel

@Composable
fun LeadDetailsScreen(
    leadId: String,
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val token by authViewModel.token.collectAsState()
    val viewModel: LeadDetailsViewModel = hiltViewModel()

    val leadDetails by viewModel.leadDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 🚀 Garante que o detalhe é carregado apenas uma vez
    LaunchedEffect(token, leadId) {
        if (token != null) {
            viewModel.fetchLeadDetails(token!!, leadId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            leadDetails != null -> {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = leadDetails?.companyName ?: "Empresa desconhecida",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Cidade: ${leadDetails?.city ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Necessidade: ${leadDetails?.need ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Voltar")
                    }
                }
            }

            else -> {
                Text(
                    text = "Erro ao carregar detalhes.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
