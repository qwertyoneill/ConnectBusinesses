// LeadsViewModel.kt
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
class LeadsViewModel @Inject constructor(
    private val repository: DirectusRepository
) : ViewModel() {

    private val _leads = MutableStateFlow<List<LeadItem>>(emptyList())
    val leads = _leads.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchLeads(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val fetchedLeads = repository.fetchLeads(token)
            _leads.value = fetchedLeads ?: emptyList()
            _isLoading.value = false
        }
    }
}
