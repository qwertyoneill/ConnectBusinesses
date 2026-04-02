package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.network.LeadItem
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeadDetailsUiState(
    val isLoading: Boolean = false,
    val lead: LeadItem? = null,
    val error: String? = null
)

@HiltViewModel
class LeadDetailsViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeadDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadLead(leadId: String) {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Inicia sessão para ver a lead."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            val lead = repository.fetchLeadDetails(token, leadId)

            if (lead != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lead = lead,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lead = null,
                        error = "Erro ao carregar detalhes."
                    )
                }
            }
        }
    }
}