package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetHearingDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionDetailsViewModel @Inject constructor(
    private val getHearingDetails: GetHearingDetailsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionDetailsUiState())
    val uiState: StateFlow<SessionDetailsUiState> = _uiState

    fun init(session: Session) {
        _uiState.update { it.copy(session = session) }
        loadDetails(session.id)
    }

    private fun loadDetails(hearingId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            when (val result = getHearingDetails(hearingId)) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, hearingDetails = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }

    fun onTabSelected(tab: Int) = _uiState.update { it.copy(selectedTab = tab) }

    fun refresh() {
        val id = _uiState.value.session?.id ?: return
        loadDetails(id)
    }
}