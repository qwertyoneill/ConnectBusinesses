package com.tiagovaz.connectbusinesses.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickReplyChips(
    onReplySelected: (String) -> Unit
) {

    val scrollState = rememberScrollState()

    val quickReplies = listOf(
        "Tenho interesse" to
                "Olá! Tenho interesse neste lead. Podemos falar melhor sobre esta oportunidade?",

        "Quero mais detalhes" to
                "Olá! Vi o teu lead e tenho interesse. Podes dar-me mais detalhes?",

        "Agendar chamada" to
                "Olá! Este lead faz sentido para mim. Queres agendar uma chamada rápida?",

        "Enviar proposta" to
                "Olá! Posso preparar uma proposta para este lead. Queres que avance?"
    )

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        quickReplies.forEach { (label, message) ->

            AssistChip(
                onClick = { onReplySelected(message) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}