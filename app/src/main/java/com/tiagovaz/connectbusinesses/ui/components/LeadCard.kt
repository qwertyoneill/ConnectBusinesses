package com.tiagovaz.connectbusinesses.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tiagovaz.connectbusinesses.data.network.LeadItem

@Composable
fun LeadCard(
    lead: LeadItem,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    shadowColor: Color = Color.Black.copy(alpha = 0.3f),
    borderColor: Color = Color.Transparent
) {
    val imageUrl = lead.imageUrl
        ?: "https://images.unsplash.com/photo-1521737604893-d14cc237f11d?w=1080"

    val cornerRadius by animateDpAsState(
        targetValue = if (isActive) 24.dp else 0.dp,
        animationSpec = tween(300),
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            // 🌟 sombra principal
            .shadow(
                elevation = if (isActive) 30.dp else 10.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            // 🌈 contorno principal
            .border(
                width = if (isActive) 6.dp else 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius))
            // ✨ glow difuso (blur leve do contorno)
            .drawBehind {
                val glowColor = borderColor.copy(alpha = 0.5f)
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        center = center,
                        radius = size.minDimension * 0.8f
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                    blendMode = BlendMode.Plus
                )
            }
    ) {
        // 🖼️ imagem intacta
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = lead.companyName ?: "Imagem da empresa",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🌗 gradiente inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                )
        )

        // 🏷️ informação textual
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = lead.companyName ?: "Empresa desconhecida",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = lead.city ?: "Cidade não informada",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = lead.need ?: "Sem descrição",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}