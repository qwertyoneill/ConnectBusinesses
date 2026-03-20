package com.tiagovaz.connectbusinesses.viewmodel

import com.tiagovaz.connectbusinesses.data.network.ConversationItem

data class ConversationsUiState(
    val isLoading: Boolean = false,
    val items: List<ConversationItem> = emptyList(),
    val error: String? = null
)
