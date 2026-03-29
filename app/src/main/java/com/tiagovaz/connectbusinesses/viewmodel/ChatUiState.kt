package com.tiagovaz.connectbusinesses.viewmodel

import com.tiagovaz.connectbusinesses.data.network.ConversationMessageItem

data class ChatUiState(
    val initialLoading: Boolean = false,
    val refreshing: Boolean = false,
    val sending: Boolean = false,
    val messages: List<ConversationMessageItem> = emptyList(),
    val currentUserId: String? = null,
    val error: String? = null,
    val sendError: String? = null
)