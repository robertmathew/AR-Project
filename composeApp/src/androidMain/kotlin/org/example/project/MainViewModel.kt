package org.example.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val isArSupported: Boolean = false,
    val isDepthSupported: Boolean = false
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun updateArStatus(isArSupported: Boolean) {
        _uiState.update {
            it.copy(isArSupported = isArSupported)
        }
    }

    fun updateDepthStatus(isDepthSupported: Boolean) {
        _uiState.update {
            it.copy(isDepthSupported = isDepthSupported)
        }
    }
}