package com.smartfingers.smartlawyerplus.ui.screens.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.CaseStatusFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.usecase.cases.GetCaseAttachmentsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.cases.GetCaseClientsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.cases.GetCaseDetailsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.cases.GetCaseStatusFiltersUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.cases.GetCasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Cases list ViewModel ──────────────────────────────────────────────────────

@HiltViewModel
class CasesViewModel @Inject constructor(
    private val getCaseStatusFiltersUseCase: GetCaseStatusFiltersUseCase,
    private val getCasesUseCase: GetCasesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CasesUiState())
    val uiState: StateFlow<CasesUiState> = _uiState

    init {
        val filters = getCaseStatusFiltersUseCase()
        val selected = filters.first { it.isSelected }
        _uiState.update {
            it.copy(filters = filters, selectedFilter = selected)
        }
        loadCases(refresh = true)
    }

    fun selectFilter(filter: CaseStatusFilter) {
        val updated = _uiState.value.filters.map { it.copy(isSelected = it.id == filter.id) }
        _uiState.update {
            it.copy(
                filters = updated,
                selectedFilter = filter,
                cases = emptyList(),
                page = 0,
                hasMore = false,
            )
        }
        loadCases(refresh = true)
    }

    fun refresh() {
        _uiState.update { it.copy(cases = emptyList(), page = 0, hasMore = false) }
        loadCases(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        loadCases(refresh = false)
    }

    private fun loadCases(refresh: Boolean) {
        val state = _uiState.value
        val page = if (refresh) 1 else state.page
        val statusId = state.selectedFilter?.id ?: 6

        viewModelScope.launch {
            _uiState.update {
                if (refresh) it.copy(isLoading = true, error = "")
                else it.copy(isLoadingMore = true)
            }
            when (val result = getCasesUseCase(page, 10, statusId)) {
                is Result.Success -> {
                    val (newCases, total) = result.data
                    val all = if (refresh) newCases else state.cases + newCases
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            cases = all,
                            page = page + 1,
                            hasMore = total > all.size,
                        )
                    }
                }

                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, isLoadingMore = false, error = result.message)
                }

                else -> Unit
            }
        }
    }
}

// ── Case details ViewModel ────────────────────────────────────────────────────

@HiltViewModel
class CaseDetailsViewModel @Inject constructor(
    private val getCaseDetailsUseCase: GetCaseDetailsUseCase,
    private val getCaseAttachmentsUseCase: GetCaseAttachmentsUseCase,
    private val getCaseClientsUseCase: GetCaseClientsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaseDetailsUiState())
    val uiState: StateFlow<CaseDetailsUiState> = _uiState

    fun loadDetails(caseId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            when (val result = getCaseDetailsUseCase(caseId)) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, details = result.data)
                }

                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }

                else -> Unit
            }
        }
    }

    fun selectTab(tab: CaseDetailsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun loadAttachments(caseId: Int, refresh: Boolean = false) {
        val state = _uiState.value
        if (!refresh && (state.isLoadingAttachments || !state.hasMoreAttachments && state.attachments.isNotEmpty())) return
        val page = if (refresh) 0 else state.attachmentsPage
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAttachments = true) }
            when (val result = getCaseAttachmentsUseCase(caseId, page, 10)) {
                is Result.Success -> {
                    val (items, total) = result.data
                    val all = if (refresh) items else state.attachments + items
                    _uiState.update {
                        it.copy(
                            isLoadingAttachments = false,
                            attachments = all,
                            attachmentsPage = page + 1,
                            hasMoreAttachments = total > all.size,
                        )
                    }
                }

                is Result.Error -> _uiState.update { it.copy(isLoadingAttachments = false) }
                else -> Unit
            }
        }
    }

    fun loadClients(caseId: Int, refresh: Boolean = false) {
        val state = _uiState.value
        if (!refresh && (state.isLoadingClients || !state.hasMoreClients && state.clients.isNotEmpty())) return
        val page = if (refresh) 0 else state.clientsPage
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClients = true) }
            when (val result = getCaseClientsUseCase(caseId, page, 10)) {
                is Result.Success -> {
                    val (items, total) = result.data
                    val all = if (refresh) items else state.clients + items
                    _uiState.update {
                        it.copy(
                            isLoadingClients = false,
                            clients = all,
                            clientsPage = page + 1,
                            hasMoreClients = total > all.size,
                        )
                    }
                }

                is Result.Error -> _uiState.update { it.copy(isLoadingClients = false) }
                else -> Unit
            }
        }
    }
}