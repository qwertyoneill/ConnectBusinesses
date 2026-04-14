package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.network.LeadInterestedItem
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
    val isDeleting: Boolean = false,
    val isLoadingInterested: Boolean = false,
    val isAcceptingInterested: Boolean = false,
    val lead: LeadItem? = null,
    val interested: List<LeadInterestedItem> = emptyList(),
    val imageAccessToken: String? = null,
    val error: String? = null,
    val interestedError: String? = null,
    val actionError: String? = null,
    val deleteSuccess: Boolean = false
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
                        imageAccessToken = token,
                        error = null
                    )
                }
                loadInterested(lead.id)
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

    fun deleteLead() {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            val leadId = _uiState.value.lead?.id

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(error = "Inicia sessão para apagar a lead.")
                }
                return@launch
            }

            if (leadId == null) {
                _uiState.update {
                    it.copy(error = "Lead inválida.")
                }
                return@launch
            }

            _uiState.update { it.copy(isDeleting = true, error = null) }

            repository.deleteLead(token, leadId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            deleteSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            error = e.message ?: "Erro ao apagar lead."
                        )
                    }
                }
        }
    }

    fun loadInterested(leadId: Int) {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(interestedError = "Inicia sessão para ver interessados.")
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoadingInterested = true, interestedError = null)
            }

            repository.fetchInterestedInLead(token, leadId)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            isLoadingInterested = false,
                            interested = list
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingInterested = false,
                            interestedError = e.message
                        )
                    }
                }
        }
    }

    fun acceptInterested(
        interestedUserId: String,
        onConversationReady: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            val leadId = _uiState.value.lead?.id

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(actionError = "Inicia sessão para aceitar interessados.")
                }
                return@launch
            }

            if (leadId == null) {
                _uiState.update {
                    it.copy(actionError = "Lead inválida.")
                }
                return@launch
            }

            _uiState.update {
                it.copy(isAcceptingInterested = true, actionError = null)
            }

            repository.acceptInterestedInLead(token, leadId, interestedUserId)
                .onSuccess { acceptResult ->
                    repository.openConversationFromMatch(token, acceptResult.match_id)
                        .onSuccess { conversationId ->
                            loadInterested(leadId)
                            _uiState.update {
                                it.copy(isAcceptingInterested = false, actionError = null)
                            }
                            onConversationReady(conversationId)
                        }
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(
                                    isAcceptingInterested = false,
                                    actionError = e.message ?: "Erro ao abrir conversa."
                                )
                            }
                        }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isAcceptingInterested = false,
                            actionError = e.message ?: "Erro ao aceitar interessado."
                        )
                    }
                }
        }
    }
}