package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.usecase.report.UploadReportAttachmentUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.AddHearingActionSampleUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetHearingActionSamplesUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetLastHearingByIdUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetLastHearingNumberByCaseIdUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.tasks.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSessionViewModel @Inject constructor(
    private val getHearingTypes: GetHearingTypesForAddUseCase,
    private val getSubHearingTypes: GetSubHearingTypesForAddUseCase,
    private val getCourts: GetCourtsForAddUseCase,
    private val getCases: GetSessionCasesUseCase,
    private val getEmployees: GetSessionEmployeesUseCase,
    private val addSession: AddSessionUseCase,
    private val getLastHearingNumberByCaseId: GetLastHearingNumberByCaseIdUseCase,
    private val getLastHearingById: GetLastHearingByIdUseCase,
    private val getActionSamples: GetHearingActionSamplesUseCase,
    private val addActionSample: AddHearingActionSampleUseCase,
    private val uploadAttachmentUseCase: UploadReportAttachmentUseCase,
    private val getTaskCategories: GetTaskCategoriesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddSessionUiState())
    val state: StateFlow<AddSessionUiState> = _state
    private var _pendingTaskResult: SessionActionTaskResult? = null
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            when (val r = getCases()) {
                is Result.Success -> _state.update { it.copy(cases = r.data) }
                is Result.Error -> _state.update { it.copy(error = r.message) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getHearingTypes()) {
                is Result.Success -> _state.update { it.copy(hearingTypes = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getSubHearingTypes()) {
                is Result.Success -> _state.update { it.copy(subHearingTypes = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getCourts()) {
                is Result.Success -> _state.update { it.copy(courts = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getEmployees()) {
                is Result.Success -> _state.update { it.copy(employees = r.data) }
                is Result.Error -> _state.update { it.copy(error = r.message) }
                else -> Unit
            }
        }
    }


    fun onCaseSelected(c: TaskCase?) {
        _state.update { it.copy(selectedCase = c) }
        if (c != null) autoFillFromLastHearing(c.id.toString())
    }

    private fun autoFillFromLastHearing(caseId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAutoFilling = true) }
            // reset previous values
            _state.update {
                it.copy(
                    selectedCourt = null,
                    selectedHearingType = null,
                    selectedSubHearingType = null,
                    selectedEmployees = emptyList(),
                    hearingNumber = "",
                    courtCircle = "",
                    judgeName = "",
                    judgeOfficeNumber = "",
                )
            }
            when (val r = getLastHearingNumberByCaseId(caseId)) {
                is Result.Success -> {
                    val lastNum = r.data
                    if ((lastNum.id ?: 0) > 0) {
                        lastNum.hearingNumber?.let { n ->
                            _state.update { it.copy(hearingNumber = "${n + 1}") }
                        }
                        lastNum.courtCircle?.let { cc ->
                            _state.update { it.copy(courtCircle = cc) }
                        }
                        lastNum.id?.let { loadLastHearingById(it) }
                    }
                }
                else -> Unit
            }
            _state.update { it.copy(isAutoFilling = false) }
        }
    }

    private suspend fun loadLastHearingById(hearingId: Int) {
        when (val r = getLastHearingById(hearingId)) {
            is Result.Success -> {
                val lh = r.data
                val s = _state.value
                // court
                lh.courtId?.let { cId ->
                    val court = s.courts.firstOrNull { it.id == cId }
                    _state.update { it.copy(selectedCourt = court) }
                }
                // hearing type
                lh.hearingTypeId?.let { htId ->
                    val ht = s.hearingTypes.firstOrNull { it.id == htId }
                    _state.update { it.copy(selectedHearingType = ht) }
                }
                // sub hearing type
                lh.subHearingTypeId?.let { sId ->
                    val st = s.subHearingTypes.firstOrNull { it.id == sId }
                    _state.update { it.copy(selectedSubHearingType = st) }
                }
                // employees
                lh.assignedUserIds?.let { ids ->
                    val idList = ids.split(",").map { it.trim() }
                    val matched = s.employees.filter { it.id in idList }
                    _state.update { it.copy(selectedEmployees = matched) }
                }
                // judge & circle
                lh.judgeName?.let { j -> _state.update { it.copy(judgeName = j) } }
                lh.judgeOfficeNumber?.let { jo ->
                    _state.update { it.copy(judgeOfficeNumber = jo) }
                }
            }
            else -> Unit
        }
    }

    // ── Field updates ─────────────────────────────────────────────────────────

    fun onHearingTypeSelected(h: HearingType?) =
        _state.update { it.copy(selectedHearingType = h) }

    fun onSubHearingTypeSelected(h: HearingType?) =
        _state.update { it.copy(selectedSubHearingType = h) }

    fun onCourtSelected(c: Court?) = _state.update { it.copy(selectedCourt = c) }

    fun onEmployeeToggled(e: TaskEmployee) = _state.update { s ->
        val list = s.selectedEmployees.toMutableList()
        if (list.any { it.id == e.id }) list.removeAll { it.id == e.id } else list.add(e)
        s.copy(selectedEmployees = list)
    }

    fun onHearingNumberChange(v: String) = _state.update { it.copy(hearingNumber = v) }
    fun onCourtCircleChange(v: String) = _state.update { it.copy(courtCircle = v) }
    fun onJudgeNameChange(v: String) = _state.update { it.copy(judgeName = v) }
    fun onJudgeOfficeNumberChange(v: String) = _state.update { it.copy(judgeOfficeNumber = v) }
    fun onStartTimeSelected(t: String) = _state.update { it.copy(startTime = t) }
    fun onHearingDescChange(v: String) = _state.update { it.copy(hearingDesc = v) }
    fun onRequiredDocsChange(v: String) = _state.update { it.copy(requiredDocs = v) }

    // ── Actions Required management ───────────────────────────────────────────

    fun addActionRequired() {
        _state.update {
            it.copy(actionsRequired = it.actionsRequired + SessionActionRequired())
        }
    }

    fun removeActionRequired(id: String) {
        _state.update {
            it.copy(actionsRequired = it.actionsRequired.filter { a -> a.id != id })
        }
    }

    fun updateActionText(id: String, text: String) {
        _state.update {
            it.copy(
                actionsRequired = it.actionsRequired.map { a ->
                    if (a.id == id) a.copy(text = text) else a
                }
            )
        }
    }

    fun toggleActionChecked(id: String) {
        _state.update {
            it.copy(
                actionsRequired = it.actionsRequired.map { a ->
                    if (a.id == id) a.copy(isChecked = !a.isChecked) else a
                }
            )
        }
    }

    fun toggleActionSelected(id: String) {
        _state.update {
            it.copy(
                actionsRequired = it.actionsRequired.map { a ->
                    if (a.id == id) a.copy(isSelected = !a.isSelected) else a
                }
            )
        }
    }

    // ── Action samples dialog ─────────────────────────────────────────────────

    fun openActionSamplesDialog(targetIndex: Int) {
        val samples = _state.value.actionSamples
        if (samples.isEmpty()) {
            viewModelScope.launch {
                _state.update { it.copy(isLoadingActionSamples = true) }
                when (val r = getActionSamples()) {
                    is Result.Success -> _state.update {
                        it.copy(
                            actionSamples = r.data,
                            isLoadingActionSamples = false,
                            showActionSamplesDialog = true,
                            actionSamplesTargetIndex = targetIndex,
                        )
                    }
                    else -> _state.update { it.copy(isLoadingActionSamples = false) }
                }
            }
        } else {
            _state.update {
                it.copy(
                    showActionSamplesDialog = true,
                    actionSamplesTargetIndex = targetIndex,
                )
            }
        }
    }

    fun dismissActionSamplesDialog() {
        _state.update { it.copy(showActionSamplesDialog = false, actionSamplesTargetIndex = -1) }
    }

    fun selectActionSample(sample: HearingActionSample) {
        val targetIndex = _state.value.actionSamplesTargetIndex
        if (targetIndex >= 0) {
            _state.update {
                val list = it.actionsRequired.toMutableList()
                if (targetIndex < list.size) {
                    list[targetIndex] = list[targetIndex].copy(text = sample.name)
                }
                it.copy(
                    actionsRequired = list,
                    showActionSamplesDialog = false,
                    actionSamplesTargetIndex = -1,
                )
            }
        }
    }

    // ── Add new action dialog ─────────────────────────────────────────────────

    fun openAddActionDialog(targetIndex: Int) {
        _state.update {
            it.copy(showAddActionDialog = true, addActionTargetIndex = targetIndex)
        }
    }

    fun dismissAddActionDialog() {
        _state.update { it.copy(showAddActionDialog = false, addActionTargetIndex = -1) }
    }

    fun confirmAddNewAction(name: String) {
        val targetIndex = _state.value.addActionTargetIndex
        viewModelScope.launch {
            when (val r = addActionSample(name)) {
                is Result.Success -> {
                    // invalidate cache
                    _state.update {
                        val list = it.actionsRequired.toMutableList()
                        if (targetIndex >= 0 && targetIndex < list.size) {
                            list[targetIndex] = list[targetIndex].copy(text = name)
                        }
                        it.copy(
                            actionsRequired = list,
                            actionSamples = emptyList(),
                            showAddActionDialog = false,
                            addActionTargetIndex = -1,
                        )
                    }
                }
                else -> _state.update {
                    it.copy(showAddActionDialog = false, addActionTargetIndex = -1)
                }
            }
        }
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    fun save() {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            val request = AddSessionRequest(
                caseId = s.selectedCase?.id?.toString(),
                assignedUserIds = s.selectedEmployees.joinToString(",") { it.id },
                hearingNumber = s.hearingNumber.ifBlank { null },
                hearingTypeId = s.selectedHearingType?.id?.toString(),
                subHearingTypeId = s.selectedSubHearingType?.id?.toString(),
                courtId = s.selectedCourt?.id?.toString(),
                courtCircle = s.courtCircle.ifBlank { null },
                startDate = s.startDate.ifBlank { null },
                startTime = s.startTime.ifBlank { null },
                judgeName = s.judgeName.ifBlank { null },
                judgeOfficeNumber = s.judgeOfficeNumber.ifBlank { null },
                hearingDesc = s.hearingDesc.ifBlank { null },
                requiredDocs = s.requiredDocs.ifBlank { null },
                hearingDescs = s.actionsRequired.map { action ->
                           val taskResult = if (action.isSelected) _pendingTaskResult else null
                           HearingDescRequest(
                               text = action.text,
                               checked = taskResult?.checked ?: action.isChecked,
                           )
                       }
            )
            when (val r = addSession(request)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                else -> Unit
            }
        }
    }
    // ── Hijri conversion ──────────────────────────────────────────────────────

    private fun toHijri(gregorian: String): String {
        return try {
            val parts = gregorian.split("-")
            if (parts.size != 3) return ""
            val localDate = java.time.LocalDate.of(
                parts[0].toInt(), parts[1].toInt(), parts[2].toInt()
            )
            val hijri = java.time.chrono.HijrahDate.from(localDate)
            val year = hijri.get(java.time.temporal.ChronoField.YEAR)
            val month = hijri.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
            val day = hijri.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
            "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        } catch (_: Exception) { "" }
    }

    fun onStartDateSelected(d: String) {
        _state.update { it.copy(startDate = d, startDateHijri = toHijri(d)) }
    }

    fun openAddHearingTypeDialog() =
        _state.update { it.copy(showAddHearingTypeDialog = true) }

    fun dismissAddHearingTypeDialog() =
        _state.update { it.copy(showAddHearingTypeDialog = false) }

    fun confirmAddHearingType(name: String) {
        val newType = com.smartfingers.smartlawyerplus.domain.model.HearingType(
            id = -(System.currentTimeMillis().toInt()),
            name = name,
        )
        _state.update {
            it.copy(
                hearingTypes = it.hearingTypes + newType,
                selectedHearingType = newType,
                showAddHearingTypeDialog = false,
            )
        }
    }

    fun openAddSubHearingTypeDialog() =
        _state.update { it.copy(showAddSubHearingTypeDialog = true) }

    fun dismissAddSubHearingTypeDialog() =
        _state.update { it.copy(showAddSubHearingTypeDialog = false) }

    fun confirmAddSubHearingType(name: String) {
        val newType = com.smartfingers.smartlawyerplus.domain.model.HearingType(
            id = -(System.currentTimeMillis().toInt()),
            name = name,
        )
        _state.update {
            it.copy(
                subHearingTypes = it.subHearingTypes + newType,
                selectedSubHearingType = newType,
                showAddSubHearingTypeDialog = false,
            )
        }
    }

    fun openAddCourtDialog() =
        _state.update { it.copy(showAddCourtDialog = true) }

    fun dismissAddCourtDialog() =
        _state.update { it.copy(showAddCourtDialog = false) }

    fun confirmAddCourt(name: String) {
        val newCourt = com.smartfingers.smartlawyerplus.domain.model.Court(
            id = -(System.currentTimeMillis().toInt()),
            name = name,
        )
        _state.update {
            it.copy(
                courts = it.courts + newCourt,
                selectedCourt = newCourt,
                showAddCourtDialog = false,
            )
        }
    }

    // ── Attachments ───────────────────────────────────────────────────────────

    fun uploadAttachment(filePath: String, mimeType: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAttachment = true) }
            when (val r = uploadAttachmentUseCase(filePath, mimeType)) {
                is Result.Success -> _state.update {
                    it.copy(
                        isUploadingAttachment = false,
                        attachments = it.attachments + r.data,
                    )
                }
                is Result.Error -> _state.update {
                    it.copy(isUploadingAttachment = false, error = r.message)
                }
                else -> Unit
            }
        }
    }

    fun removeAttachment(id: Int) {
        _state.update { it.copy(attachments = it.attachments.filter { a -> a.id != id }) }
    }

    fun openAddToTaskDialog() {
        val hasSelected = _state.value.actionsRequired.any { it.isSelected }
        if (!hasSelected) {
            _state.update { it.copy(error = "لم يتم تحديد أي إجراء") }
            return
        }
        _state.update { it.copy(showAddToTaskScreen = true) }
        if (_state.value.taskCategories.isEmpty()) loadTaskCategories()
    }

    fun dismissAddToTaskScreen() {
        _state.update { it.copy(showAddToTaskScreen = false) }
    }

    private fun loadTaskCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingTaskCategories = true) }
            when (val r = getTaskCategories()) {
                is Result.Success -> _state.update {
                    it.copy(taskCategories = r.data, isLoadingTaskCategories = false)
                }
                else -> _state.update { it.copy(isLoadingTaskCategories = false) }
            }
        }
    }
    fun onStartHijriDateSelected(hijri: String) {
        val greg = fromHijri(hijri)
        _state.update { it.copy(startDate = greg, startDateHijri = hijri) }
    }

    private fun fromHijri(hijri: String): String {
        return try {
            val parts = hijri.split("-")
            if (parts.size != 3) return ""
            val hd = java.time.chrono.HijrahDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val ld = java.time.LocalDate.from(java.time.chrono.HijrahChronology.INSTANCE.date(hd))
            "${ld.year}-${ld.monthValue.toString().padStart(2, '0')}-${ld.dayOfMonth.toString().padStart(2, '0')}"
        } catch (_: Exception) { "" }
    }

    fun applyActionTaskResult(result: com.smartfingers.smartlawyerplus.ui.screens.sessions.SessionActionTaskResult) {
        _state.update { s ->
            s.copy(
                actionsRequired = s.actionsRequired.map { action ->
                    if (action.isSelected) {
                        action.copy(
                            isChecked = result.checked,
                            // store extra task data in the text field is not right;
                            // the DTO already carries these via HearingDescRequest.
                            // We keep isSelected=true so the UI shows them as "assigned".
                        )
                    } else action
                },
                showAddToTaskScreen = false,
            )
        }
        // Keep the result so save() can attach it to the matching descs:
        _pendingTaskResult = result
    }
}