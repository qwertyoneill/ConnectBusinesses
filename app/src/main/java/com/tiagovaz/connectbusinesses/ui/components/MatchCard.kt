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
import com.tiagovaz.connectbusinesses.data.network.MatchItem

@Composable
fun MatchCard(
    match: MatchItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .height(100.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🖼️ Imagem do lead
            Image(
                painter = rememberAsyncImagePainter(
                    match.lead.imageUrl
                        ?: "https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=400"
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // 📄 Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = match.lead.companyName ?: "Empresa",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = match.lead.city ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 🤝 Badge
            AssistChip(
                onClick = {},
                label = { Text("Match") }
            )
        }
    }
}
