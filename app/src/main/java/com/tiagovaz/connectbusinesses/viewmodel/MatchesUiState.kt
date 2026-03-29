package com.tiagovaz.connectbusinesses.viewmodel

import com.tiagovaz.connectbusinesses.data.network.MatchViewItem

data class MatchesUiState(
    val isLoading: Boolean = false,
    val items: List<MatchViewItem> = emptyList(),
    val error: String? = null,
    val hasNewMatch: Boolean = false,
    val openingMatchId: Int? = null,
    val openChatError: String? = null
)