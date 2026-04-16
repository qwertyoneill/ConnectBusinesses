package com.tiagovaz.connectbusinesses.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.tiagovaz.connectbusinesses.utils.LeadImageUtils
import com.tiagovaz.connectbusinesses.viewmodel.LeadDetailsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LeadDetailsScreen(
    id: String,
    navController: NavController,
    viewModel: LeadDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadLead(id)
    }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            navController.popBackStack()
        }
    }

    when {
        state.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null && state.lead == null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AssistChip(
                    onClick = { viewModel.loadLead(id) },
                    label = { Text(state.error ?: "Erro") }
                )
            }
        }

        state.lead != null -> {
            val lead = state.lead!!
            val imageUrl = LeadImageUtils.buildLeadImageUrl(
                fileId = lead.backgroundFile,
                accessToken = state.imageAccessToken,
                width = 1400,
                height = 900,
                quality = 85
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (imageUrl != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = lead.companyName ?: "Imagem da lead",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = lead.companyName ?: "Lead",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Tipo", lead.type ?: "-")
                        DetailRow("Localização", lead.city ?: "-")
                        DetailRow("Descrição", lead.need ?: "-")

                        lead.createdAt?.let {
                            DetailRow("Criada em", formatLeadDate(it))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Interessados (${state.interested.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                state.interestedError?.let { interestedError ->
                    Text(
                        text = interestedError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                state.actionError?.let { actionError ->
                    Text(
                        text = actionError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                when {
                    state.isLoadingInterested -> {
                        CircularProgressIndicator()
                    }

                    state.interested.isEmpty() -> {
                        Text(
                            text = "Ainda ninguém demonstrou interesse neste lead.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.interested.forEach { item ->
                                val name = listOfNotNull(
                                    item.first_name,
                                    item.last_name
                                ).joinToString(" ").ifBlank { "Utilizador" }

                                val isAccepting =
                                    state.acceptingInterestedUserId == item.interested_user_id
                                val isOpening =
                                    state.openingConversationUserId == item.interested_user_id
                                val isBusy = state.isUserBusy(item.interested_user_id)

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = name,
                                            fontWeight = FontWeight.Bold
                                        )

                                        item.email?.let {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                viewModel.acceptInterested(
                                                    item.interested_user_id
                                                ) { conversationId ->
                                                    val safeUserName = URLEncoder.encode(
                                                        name,
                                                        StandardCharsets.UTF_8.toString()
                                                    )

                                                    val safeLeadName = URLEncoder.encode(
                                                        lead.companyName ?: "Lead",
                                                        StandardCharsets.UTF_8.toString()
                                                    )

                                                    navController.navigate(
                                                        "chat/$conversationId/$safeUserName/${lead.id}/$safeLeadName"
                                                    )
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !isBusy
                                        ) {
                                            when {
                                                isAccepting -> {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(16.dp),
                                                            strokeWidth = 2.dp
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("A aceitar...")
                                                    }
                                                }

                                                isOpening -> {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(16.dp),
                                                            strokeWidth = 2.dp
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("A abrir conversa...")
                                                    }
                                                }

                                                else -> {
                                                    Text("Aceitar e abrir conversa")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("editLead/$id") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isDeleting
                ) {
                    if (state.isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Apagar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Voltar")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!state.isDeleting) showDeleteDialog = false
            },
            title = { Text("Apagar lead") },
            text = { Text("Tens a certeza que queres apagar esta lead? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteLead()
                    },
                    enabled = !state.isDeleting
                ) {
                    Text("Apagar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !state.isDeleting
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatLeadDate(raw: String): String {
    return try {
        val date = OffsetDateTime.parse(raw)
        date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (_: Exception) {
        raw
    }
}