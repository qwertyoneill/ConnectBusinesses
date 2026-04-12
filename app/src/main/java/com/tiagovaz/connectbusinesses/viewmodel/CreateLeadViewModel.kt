package com.tiagovaz.connectbusinesses.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateLeadViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateLeadUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onTypeChange(value: String) {
        _uiState.update { it.copy(type = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(location = value) }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun submit(
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "Inicia sessão para criar leads.")
                }
                return@launch
            }

            if (state.name.trim().isBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "O nome da lead é obrigatório.")
                }
                return@launch
            }

            if (state.type.trim().isBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "O tipo da lead é obrigatório.")
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    successMessage = null,
                    errorMessage = null
                )
            }

            val backgroundFileResult = if (state.selectedImageUri != null) {
                repository.uploadImage(
                    token = token,
                    context = context,
                    uri = state.selectedImageUri
                )
            } else {
                null
            }

            val backgroundFileId = backgroundFileResult?.getOrElse { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "Erro ao enviar imagem."
                    )
                }
                return@launch
            }

            repository.createLead(
                token = token,
                name = state.name,
                type = state.type,
                description = state.description,
                location = state.location,
                backgroundFile = backgroundFileId
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Lead criada com sucesso."
                    )
                }
                onSuccess()
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.message ?: "Erro ao criar lead."
                    )
                }
            }
        }
    }
}