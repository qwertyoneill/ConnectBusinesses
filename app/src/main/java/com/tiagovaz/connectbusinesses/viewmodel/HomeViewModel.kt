package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadHome() {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            val userName = dataStore.userName.first().orEmpty().ifBlank { "Utilizador" }

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = userName,
                        error = "Inicia sessão para carregar o dashboard."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    userName = userName,
                    error = null
                )
            }

            val leads = repository.fetchFeedLeads(token)
            val matchesResult = repository.fetchMatches(token)
            val conversationsResult = repository.fetchConversations(token)

            val matches = matchesResult.getOrElse { emptyList() }
            val conversations = conversationsResult.getOrElse { emptyList() }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userName = userName,
                    availableLeadsCount = leads.size,
                    newMatchesCount = matches.size,
                    unreadConversationsCount = conversations.sumOf { convo -> convo.unread_count },
                    myLeadsCount = 0,
                    error = null
                )
            }
        }
    }
}