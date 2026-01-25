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

    fun loadMatches() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val token = dataStore.getAccessToken()
            if (token == null) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Token inexistente")
                }
                return@launch
            }

            repository.fetchMatches(token)
                .onSuccess { matches ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = matches,
                            hasNewMatch = matches.isNotEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao carregar matches"
                        )
                    }
                }
        }
    }

    fun markMatchesAsSeen() {
        _uiState.update { it.copy(hasNewMatch = false) }
    }
}
