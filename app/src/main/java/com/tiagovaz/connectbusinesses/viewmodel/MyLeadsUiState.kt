package com.tiagovaz.connectbusinesses.viewmodel

import com.tiagovaz.connectbusinesses.data.network.MyLeadItem

data class MyLeadsUiState(
    val isLoading: Boolean = false,
    val items: List<MyLeadItem> = emptyList(),
    val error: String? = null
)