package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.usecase.report.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReportViewModel @Inject constructor(
    private val getHearingTypes: GetReportHearingTypesUseCase,
    private val getFormTemplates: GetFormTemplatesUseCase,
    private val getJudgmentTypes: GetJudgmentTypesUseCase,
    private val getReport: GetReportUseCase,
    private val addReport: AddReportUseCase,
    private val updateReport: UpdateReportUseCase,
    private val uploadAttachment: UploadReportAttachmentUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReportUiState())
    val uiState: StateFlow<AddReportUiState> = _uiState

    // Will be set from screen
    private var hearingId: Int = 0
    private var existingReportId: Int? = null
    // subHearingTypeName from the session — used to pre-select hearing type
    private var sessionSubHearingTypeName: String? = null

    fun init(hearingId: Int, reportId: Int?, subHearingTypeName: String?) {
        this.hearingId = hearingId
        this.existingReportId = reportId
        this.sessionSubHearingTypeName = subHearingTypeName

        loadDropdownData()

        if (reportId != null) {
            _uiState.update { it.copy(isEditMode = true) }
            loadExistingReport(reportId)
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private fun loadDropdownData() {
        viewModelScope.launch {
            when (val r = getHearingTypes()) {
                is Result.Success -> {
                    _uiState.update { it.copy(hearingTypes = r.data) }
                    // Pre-select from session's subHearingTypeName
                    sessionSubHearingTypeName?.let { name ->
                        val match = r.data.firstOrNull {
                            it.name.equals(name, ignoreCase = true)
                        }
                        if (match != null && _uiState.value.selectedHearingType == null) {
                            onHearingTypeSelected(match)
                        }
                    }
                }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getFormTemplates()) {
                is Result.Success -> _uiState.update { it.copy(formTemplates = r.data) }
                else -> Unit
            }
        }
    }

    private fun loadJudgmentTypesIfNeeded() {
        if (_uiState.value.judgmentTypes.isNotEmpty()) return
        viewModelScope.launch {
            when (val r = getJudgmentTypes()) {
                is Result.Success -> _uiState.update { it.copy(judgmentTypes = r.data) }
                else -> Unit
            }
        }
    }

    private fun loadExistingReport(reportId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingReport = true) }
            when (val r = getReport(reportId)) {
                is Result.Success -> {
                    val d = r.data
                    _uiState.update { state ->
                        state.copy(
                            isLoadingReport = false,
                            sessionSummary = d.report ?: "",
                            courtDecision = d.statement ?: "",
                            judgmentDate = d.judgmentReceiptDate?.take(10) ?: "",
                            judgmentSummary = d.judgmentText ?: "",
                            isAppealable = d.isAddNextCaseEnabled,
                            isUrgentAppeal = d.isAddNextCaseUrgent,
                            attachments = d.attachments,
                        )
                    }
                    // Pre-select hearing type by id
                    d.hearingTypeId?.let { htId ->
                        val ht = _uiState.value.hearingTypes.firstOrNull { it.id == htId }
                        if (ht != null) onHearingTypeSelected(ht)
                    }
                    // Pre-select form template
                    d.formTemplateId?.let { ftId ->
                        val ft = _uiState.value.formTemplates.firstOrNull { it.id == ftId }
                        _uiState.update { it.copy(selectedFormTemplate = ft) }
                    }
                    // Pre-select judgment type
                    if (d.judgmentType != null) {
                        loadJudgmentTypesIfNeeded()
                        // Try to match after load
                        viewModelScope.launch {
                            val jt = _uiState.value.judgmentTypes.firstOrNull { it.id == d.judgmentType }
                            _uiState.update { it.copy(selectedJudgmentType = jt) }
                        }
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoadingReport = false, error = r.message)
                }
                else -> Unit
            }
        }
    }

    // ── Field updates ─────────────────────────────────────────────────────────

    fun onHearingTypeSelected(ht: HearingType?) {
        val showDecision = ht?.name?.contains("نطق", ignoreCase = true) == true ||
                ht?.name?.contains("حكم", ignoreCase = true) == true
        _uiState.update {
            it.copy(
                selectedHearingType = ht,
                showDecisionSection = showDecision,
            )
        }
        if (showDecision) loadJudgmentTypesIfNeeded()
    }

    fun onFormTemplateSelected(ft: FormTemplate?) =
        _uiState.update { it.copy(selectedFormTemplate = ft) }

    fun onJudgmentTypeSelected(jt: JudgmentType?) =
        _uiState.update { it.copy(selectedJudgmentType = jt) }

    fun onSessionSummaryChange(v: String) = _uiState.update { it.copy(sessionSummary = v) }
    fun onCourtDecisionChange(v: String) = _uiState.update { it.copy(courtDecision = v) }
    fun onJudgmentDateSelected(d: String) = _uiState.update { it.copy(judgmentDate = d) }
    fun onJudgmentSummaryChange(v: String) = _uiState.update { it.copy(judgmentSummary = v) }

    fun onAppealableToggle() {
        _uiState.update {
            val newAppealable = !it.isAppealable
            it.copy(
                isAppealable = newAppealable,
                isUrgentAppeal = if (!newAppealable) false else it.isUrgentAppeal,
            )
        }
    }

    fun onUrgentAppealToggle() {
        if (!_uiState.value.isAppealable) return
        _uiState.update { it.copy(isUrgentAppeal = !it.isUrgentAppeal) }
    }

    // ── Add new hearing type dialog ───────────────────────────────────────────

    fun openAddHearingTypeDialog() =
        _uiState.update { it.copy(showAddHearingTypeDialog = true) }

    fun dismissAddHearingTypeDialog() =
        _uiState.update { it.copy(showAddHearingTypeDialog = false) }

    fun clearError() = _uiState.update { it.copy(error = "") }

    // ── Attachments ───────────────────────────────────────────────────────────

    fun uploadFile(filePath: String, mimeType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingFile = true) }
            when (val r = uploadAttachment(filePath, mimeType)) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isUploadingFile = false,
                        attachments = it.attachments + r.data,
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isUploadingFile = false, error = r.message)
                }
                else -> Unit
            }
        }
    }

    fun removeAttachment(id: Int) {
        _uiState.update {
            it.copy(attachments = it.attachments.filter { a -> a.id != id })
        }
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    fun save() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            val request = AddReportRequest(
                id = existingReportId,
                hearingId = hearingId,
                hearingTypeId = s.selectedHearingType?.id?.toString(),
                statement = s.courtDecision.ifBlank { null },
                report = s.sessionSummary.ifBlank { null },
                judgmentReceiptDate = s.judgmentDate.ifBlank { null },
                judgmentText = s.judgmentSummary.ifBlank { null },
                judgmentType = if (s.showDecisionSection)
                    s.selectedJudgmentType?.id?.toString() else null,
                formTemplateId = s.selectedFormTemplate?.id?.toString(),
                isAddNextCaseEnabled = s.isAppealable,
                isAddNextCaseUrgent = s.isUrgentAppeal,
                attachments = s.attachments,
            )
            val result = if (existingReportId != null) updateReport(request)
            else addReport(request)

            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        success = true,
                        successMessage = if (existingReportId != null)
                            "تم تعديل التقرير بنجاح" else "تم إضافة التقرير بنجاح",
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }
}