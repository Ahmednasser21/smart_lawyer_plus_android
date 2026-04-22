package com.smartfingers.smartlawyerplus.ui.screens.cases

import com.smartfingers.smartlawyerplus.domain.model.CaseAttachment
import com.smartfingers.smartlawyerplus.domain.model.CaseClient
import com.smartfingers.smartlawyerplus.domain.model.CaseDetails
import com.smartfingers.smartlawyerplus.domain.model.CaseListItem
import com.smartfingers.smartlawyerplus.domain.model.CaseStatusFilter

// ── Cases list state ──────────────────────────────────────────────────────────

data class CasesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val cases: List<CaseListItem> = emptyList(),
    val filters: List<CaseStatusFilter> = emptyList(),
    val selectedFilter: CaseStatusFilter? = null,
    val hasMore: Boolean = false,
    val page: Int = 0,
    val error: String = "",
)

// ── Case details state ────────────────────────────────────────────────────────

enum class CaseDetailsTab { DATA, DOCS, CLIENTS }

data class CaseDetailsUiState(
    val isLoading: Boolean = false,
    val details: CaseDetails? = null,
    val selectedTab: CaseDetailsTab = CaseDetailsTab.DATA,
    val error: String = "",
    // Docs sub-state
    val attachments: List<CaseAttachment> = emptyList(),
    val isLoadingAttachments: Boolean = false,
    val hasMoreAttachments: Boolean = false,
    val attachmentsPage: Int = 0,
    // Clients sub-state
    val clients: List<CaseClient> = emptyList(),
    val isLoadingClients: Boolean = false,
    val hasMoreClients: Boolean = false,
    val clientsPage: Int = 0,
)