package com.tiagovaz.connectbusinesses.viewmodel

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
class MatchesViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.token.collect { token ->
                if (!token.isNullOrBlank()) {
                    loadMatchesWithToken(token)
                }
            }
        }
    }

    fun loadMatches() {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Inicia sessão para ver os matches."
                    )
                }
                return@launch
            }
            loadMatchesWithToken(token)
        }
    }

    private suspend fun loadMatchesWithToken(token: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        repository.fetchMatches(token)
            .onSuccess { matches ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = matches,
                        hasNewMatch = matches.isNotEmpty(),
                        error = null
                    )
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar matches"
                    )
                }
            }
    }

    fun openMatchConversation(matchId: Int, onOpened: (Int) -> Unit) {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(openChatError = "Inicia sessão para abrir o chat.")
                }
                return@launch
            }

            _uiState.update {
                it.copy(openingMatchId = matchId, openChatError = null)
            }

            repository.openConversationFromMatch(token, matchId)
                .onSuccess { conversationId ->
                    _uiState.update {
                        it.copy(openingMatchId = null, openChatError = null)
                    }
                    onOpened(conversationId)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            openingMatchId = null,
                            openChatError = e.message ?: "Erro ao abrir conversa"
                        )
                    }
                }
        }
    }

    fun markMatchesAsSeen() {
        _uiState.update { it.copy(hasNewMatch = false) }
    }

    fun clearOpenChatError() {
        _uiState.update { it.copy(openChatError = null) }
    }
}