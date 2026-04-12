package com.tiagovaz.connectbusinesses.viewmodel

data class CreateLeadUiState(
    val isSubmitting: Boolean = false,
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val location: String = "",
    val selectedImageUri: android.net.Uri? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
