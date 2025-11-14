package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.network.LeadItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadDetailsViewModel @Inject constructor(
    private val repository: DirectusRepository
) : ViewModel() {

    private val _leadDetails = MutableStateFlow<LeadItem?>(null)
    val leadDetails = _leadDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchLeadDetails(token: String, leadId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _leadDetails.value = repository.fetchLeadDetails(token, leadId)
            _isLoading.value = false
        }
    }
}
