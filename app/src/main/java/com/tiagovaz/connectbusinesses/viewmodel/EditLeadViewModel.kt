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
class EditLeadViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditLeadUiState())
    val uiState = _uiState.asStateFlow()

    fun loadLead(leadId: String) {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Inicia sessão para editar a lead."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val lead = repository.fetchLeadDetails(token, leadId)

            if (lead != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        leadId = lead.id,
                        name = lead.companyName.orEmpty(),
                        type = lead.type.orEmpty(),
                        description = lead.need.orEmpty(),
                        location = lead.city.orEmpty(),
                        backgroundFile = lead.backgroundFile,
                        imageAccessToken = token,
                        selectedImageUri = null,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar a lead."
                    )
                }
            }
        }
    }

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
                    it.copy(errorMessage = "Inicia sessão para guardar alterações.")
                }
                return@launch
            }

            val leadId = state.leadId
            if (leadId == null) {
                _uiState.update {
                    it.copy(errorMessage = "Lead inválida.")
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
            } ?: state.backgroundFile

            repository.updateLead(
                token = token,
                id = leadId,
                name = state.name,
                type = state.type,
                description = state.description,
                location = state.location,
                backgroundFile = backgroundFileId
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Lead atualizada com sucesso.",
                        backgroundFile = backgroundFileId,
                        imageAccessToken = token
                    )
                }
                onSuccess()
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.message ?: "Erro ao atualizar lead."
                    )
                }
            }
        }
    }
}