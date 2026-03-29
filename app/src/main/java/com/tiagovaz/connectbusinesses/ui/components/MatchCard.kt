package com.tiagovaz.connectbusinesses.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tiagovaz.connectbusinesses.data.network.MatchViewItem

@Composable
fun MatchCard(
    match: MatchViewItem,
    isOpening: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOpening) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .height(100.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    match.other_avatar
                        ?: "https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=400"
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = match.lead_name ?: "Lead",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = listOfNotNull(match.other_first_name, match.other_last_name)
                        .joinToString(" ")
                        .ifBlank { "Utilizador" },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isOpening) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                AssistChip(
                    onClick = onClick,
                    label = { Text("Abrir chat") }
                )
            }
        }
    }
}