package com.tiagovaz.connectbusinesses.viewmodel

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "Utilizador",
    val availableLeadsCount: Int = 0,
    val newMatchesCount: Int = 0,
    val unreadConversationsCount: Int = 0,
    val myLeadsCount: Int = 0,
    val error: String? = null
)