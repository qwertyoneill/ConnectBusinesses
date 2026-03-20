package com.tiagovaz.connectbusinesses.viewmodel

import com.tiagovaz.connectbusinesses.data.network.ConversationMessageItem

data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ConversationMessageItem> = emptyList(),
    val error: String? = null,
    val sending: Boolean = false
)