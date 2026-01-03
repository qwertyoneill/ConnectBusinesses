package com.tiagovaz.connectbusinesses.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tiagovaz.connectbusinesses.data.network.MatchItem

@Composable
fun MatchCard(match: MatchItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "💥 Match!",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = match.lead.companyName ?: "Empresa")
            Text(text = match.lead.city ?: "")
        }
    }
}
