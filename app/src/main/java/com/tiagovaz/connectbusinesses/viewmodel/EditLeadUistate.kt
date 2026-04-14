package com.tiagovaz.connectbusinesses.viewmodel

import android.net.Uri

data class EditLeadUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val leadId: Int? = null,
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val location: String = "",
    val selectedImageUri: Uri? = null,
    val backgroundFile: String? = null,
    val imageAccessToken: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)