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
class MyLeadsViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyLeadsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMyLeads() {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            val userId = dataStore.getUserId()

            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Inicia sessão para ver as tuas leads."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.fetchMyLeads(token, userId)
                .onSuccess { leads ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = leads,
                            imageAccessToken = token,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erro ao carregar as tuas leads."
                        )
                    }
                }
        }
    }
}