package com.tiagovaz.connectbusinesses.viewmodel

data class EditLeadUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val leadId: Int? = null,
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val location: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null
)