package com.smartfingers.smartlawyerplus.ui.screens.sessions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.SessionActionRequired
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddSessionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val today = Calendar.getInstance()

    LaunchedEffect(state.success) { if (state.success) onSaved() }

    // ── Dialogs ───────────────────────────────────────────────────────────────
    if (state.showActionSamplesDialog) {
        ActionSamplesDialog(
            samples = state.actionSamples,
            isLoading = state.isLoadingActionSamples,
            onSelect = viewModel::selectActionSample,
            onDismiss = viewModel::dismissActionSamplesDialog,
        )
    }
    if (state.showAddActionDialog) {
        AddNewActionDialog(
            onConfirm = viewModel::confirmAddNewAction,
            onDismiss = viewModel::dismissAddActionDialog,
        )
    }
    if (state.showAddHearingTypeDialog) {
        AddNameDialog(
            title = "إضافة نوع جلسة",
            placeholder = "اسم النوع",
            onConfirm = viewModel::confirmAddHearingType,
            onDismiss = viewModel::dismissAddHearingTypeDialog,
        )
    }
    if (state.showAddSubHearingTypeDialog) {
        AddNameDialog(
            title = "إضافة نوع جلسة فرعي",
            placeholder = "اسم النوع",
            onConfirm = viewModel::confirmAddSubHearingType,
            onDismiss = viewModel::dismissAddSubHearingTypeDialog,
        )
    }
    if (state.showAddCourtDialog) {
        AddNameDialog(
            title = "إضافة محكمة",
            placeholder = "اسم المحكمة",
            onConfirm = viewModel::confirmAddCourt,
            onDismiss = viewModel::dismissAddCourtDialog,
        )
    }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val path = getRealPathFromUri(context, it)
            val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
            if (path != null) viewModel.uploadAttachment(path, mimeType)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "إضافة جلسة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "رجوع",
                            tint = Primary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            // ── القضية ────────────────────────────────────────────────────────
            SectionDropdown(
                label = "القضية",
                selected = state.selectedCase?.name ?: "",
                options = state.cases.map { it.name },
                onSelect = { name ->
                    state.cases.firstOrNull { it.name == name }
                        ?.let { viewModel.onCaseSelected(it) }
                },
            )

            if (state.isAutoFilling) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Primary)
            }

            // ── المكلفون بالجلسة (multi-select dropdown with checkboxes) ──────
            MultiSelectDropdown(
                label = "المكلفون بالجلسة",
                allOptions = state.employees,
                selected = state.selectedEmployees,
                onToggle = viewModel::onEmployeeToggled,
            )

            // ── رقم الجلسة ────────────────────────────────────────────────────
            RtlOutlinedTextField(
                value = state.hearingNumber,
                onValueChange = viewModel::onHearingNumberChange,
                label = "رقم الجلسة",
            )

            // ── نوع الجلسة (with add button) ──────────────────────────────────
            DropdownWithAdd(
                label = "نوع الجلسة",
                selected = state.selectedHearingType?.name ?: "",
                options = state.hearingTypes.map { it.name },
                onSelect = { name ->
                    state.hearingTypes.firstOrNull { it.name == name }
                        ?.let { viewModel.onHearingTypeSelected(it) }
                },
                onAddNew = viewModel::openAddHearingTypeDialog,
            )

            // ── نوع الجلسة الفرعي (with add button) ──────────────────────────
            DropdownWithAdd(
                label = "نوع الجلسة الفرعي",
                selected = state.selectedSubHearingType?.name ?: "",
                options = state.subHearingTypes.map { it.name },
                onSelect = { name ->
                    state.subHearingTypes.firstOrNull { it.name == name }
                        ?.let { viewModel.onSubHearingTypeSelected(it) }
                },
                onAddNew = viewModel::openAddSubHearingTypeDialog,
            )

            // ── المحكمة (with add button) ─────────────────────────────────────
            DropdownWithAdd(
                label = "المحكمة",
                selected = state.selectedCourt?.name ?: "",
                options = state.courts.map { it.name },
                onSelect = { name ->
                    state.courts.firstOrNull { it.name == name }
                        ?.let { viewModel.onCourtSelected(it) }
                },
                onAddNew = viewModel::openAddCourtDialog,
            )

            // ── تاريخ الجلسة (Gregorian + Hijri) ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Gregorian date
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        val picker = DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                viewModel.onStartDateSelected(
                                    "$y-${(m + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
                                )
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH),
                        )
                        // Prevent past dates
                        picker.datePicker.minDate = today.timeInMillis
                        picker.show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "تاريخ الجلسة (م)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp,
                        )
                        Text(
                            text = state.startDate.ifBlank { "--/--/----" },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.startDate.isBlank()) TextSecondary
                            else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                // Hijri date (read-only, auto-computed from Gregorian)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Divider, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "تاريخ الجلسة (هـ)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp,
                        )
                        Text(
                            text = state.startDateHijri.ifBlank { "--/--/----" },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.startDateHijri.isBlank()) TextSecondary
                            else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            // ── الوقت (Hour + Minute) ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, h, m ->
                                viewModel.onStartTimeSelected(
                                    "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                                )
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true,
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "الوقت",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp,
                        )
                        Text(
                            text = state.startTime.ifBlank { "--:--" },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.startTime.isBlank()) TextSecondary
                            else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
            }

            // ── اسم القاضي ────────────────────────────────────────────────────
            RtlOutlinedTextField(
                value = state.judgeName,
                onValueChange = viewModel::onJudgeNameChange,
                label = "اسم القاضي",
            )

            // ── دائرة المحكمة ────────────────────────────────────────────────
            RtlOutlinedTextField(
                value = state.courtCircle,
                onValueChange = viewModel::onCourtCircleChange,
                label = "دائرة المحكمة",
            )

            // ── بريد الدائرة ──────────────────────────────────────────────────
            RtlOutlinedTextField(
                value = state.judgeOfficeNumber,
                onValueChange = viewModel::onJudgeOfficeNumberChange,
                label = "بريد الدائرة",
            )

            // ── الإجراءات المطلوبة ────────────────────────────────────────────
            ActionsRequiredCard(
                actions = state.actionsRequired,
                onAdd = viewModel::addActionRequired,
                onRemove = viewModel::removeActionRequired,
                onTextChange = viewModel::updateActionText,
                onToggleChecked = viewModel::toggleActionChecked,
                onChooseSample = { index -> viewModel.openActionSamplesDialog(index) },
                onAddNew = { index -> viewModel.openAddActionDialog(index) },
                onAddSelectedToTask = viewModel::openAddToTaskDialog,
            )

            // ── المستندات المطلوبة ────────────────────────────────────────────
            RtlOutlinedTextField(
                value = state.requiredDocs,
                onValueChange = viewModel::onRequiredDocsChange,
                label = "المستندات المطلوبة",
                minLines = 3,
            )

            // ── ملاحظات الجلسة ────────────────────────────────────────────────
            RtlOutlinedTextField(
                value = state.hearingDesc,
                onValueChange = viewModel::onHearingDescChange,
                label = "ملاحظات الجلسة",
                minLines = 3,
            )

            // ── المرفقات ──────────────────────────────────────────────────────
            SectionLabel("المرفقات")
            OutlinedButton(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (state.isUploadingAttachment) Primary else Divider,
                ),
                enabled = !state.isUploadingAttachment,
            ) {
                if (state.isUploadingAttachment) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("جاري الرفع...", color = TextSecondary)
                } else {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("إرفاق ملف", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            if (state.attachments.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.attachments.forEach { att ->
                        AttachmentRow(
                            name = att.name ?: "-",
                            createdBy = att.createdBy,
                            isApproved = att.isApproved,
                            onRemove = { viewModel.removeAttachment(att.id) },
                        )
                    }
                }
            }

            if (state.error.isNotEmpty()) {
                Text(
                    state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // ── Buttons ───────────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmartLawyerButton(
                    text = "حفظ",
                    onClick = viewModel::save,
                    isLoading = state.isLoading,
                    modifier = Modifier.weight(1f),
                )
                SmartLawyerOutlinedButton(
                    text = "إلغاء",
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────── Shared small composables ─────────────────────────────

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = Primary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End,
    )
}

@Composable
fun RtlOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        minLines = minLines,
        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Divider,
        ),
    )
}

/** Simple dropdown, max height 220dp so it doesn't fill screen */
@Composable
fun SectionDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Primary)
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Divider,
            ),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .heightIn(max = 220.dp),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}

/** Dropdown with an "add new" (+) button, like iOS DropDownWithAddButton */
@Composable
fun DropdownWithAdd(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    onAddNew: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Add button on the LEFT (RTL: appears right of dropdown visually)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary)
                .clickable { onAddNew() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة", tint = Color.White)
        }

        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        label,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Primary)
                            .clickable { expanded = !expanded },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .heightIn(max = 220.dp),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        },
                        onClick = { onSelect(option); expanded = false },
                    )
                }
            }
        }
    }
}

/** Multi-select employees dropdown with checkboxes — mirrors iOS multi-select */
@Composable
fun MultiSelectDropdown(
    label: String,
    allOptions: List<TaskEmployee>,
    selected: List<TaskEmployee>,
    onToggle: (TaskEmployee) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = when {
        selected.isEmpty() -> ""
        selected.size == 1 -> selected[0].name
        else -> "${selected[0].name} (+${selected.size - 1})"
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Primary)
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Divider,
            ),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .heightIn(max = 220.dp),
        ) {
            allOptions.forEach { emp ->
                val isChecked = selected.any { it.id == emp.id }
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                emp.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckBox
                                else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                    onClick = { onToggle(emp) },
                )
            }
        }
    }
}

// ── Actions Required Card ──────────────────────────────────────────────────────

@Composable
private fun ActionsRequiredCard(
    actions: List<SessionActionRequired>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onTextChange: (String, String) -> Unit,
    onToggleChecked: (String) -> Unit,
    onChooseSample: (Int) -> Unit,
    onAddNew: (Int) -> Unit,
    onAddSelectedToTask: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row — mirrors iOS card header with two buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // "إضافة" button on left (RTL)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallChipButton(text = "إضافة", onClick = onAdd)
                    SmallChipButton(
                        text = "إضافة للمهام",
                        onClick = onAddSelectedToTask,
                        color = Secondary,
                    )
                }
                Text(
                    text = "الإجراءات المطلوبة في الجلسة",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
            }

            if (actions.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                actions.forEachIndexed { index, action ->
                    ActionRequiredRow(
                        action = action,
                        onRemove = { onRemove(action.id) },
                        onTextChange = { onTextChange(action.id, it) },
                        onToggleChecked = { onToggleChecked(action.id) },
                        onChooseSample = { onChooseSample(index) },
                        onAddNew = { onAddNew(index) },
                    )
                    if (index < actions.lastIndex) Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun ActionRequiredRow(
    action: SessionActionRequired,
    onRemove: () -> Unit,
    onTextChange: (String) -> Unit,
    onToggleChecked: () -> Unit,
    onChooseSample: () -> Unit,
    onAddNew: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Delete
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = ColorError, modifier = Modifier.size(18.dp))
        }

        // Choose from samples
        SmallChipButton(text = "اختيار", onClick = onChooseSample, color = Secondary)

        // Add new action
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Primary)
                .clickable { onAddNew() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }

        // Text input
        OutlinedTextField(
            value = action.text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    "الإجراء",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(6.dp),
            textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.End),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Divider,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
            ),
        )

        // Checkbox
        IconButton(onClick = onToggleChecked, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = if (action.isChecked) Icons.Default.CheckBox
                else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun SmallChipButton(
    text: String,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color = Primary,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

@Composable
private fun AttachmentRow(
    name: String,
    createdBy: String?,
    isApproved: Boolean,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = ColorError, modifier = Modifier.size(18.dp))
            }
            if (isApproved) {
                Text(
                    "معتمد",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorSuccess,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ColorSuccess.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isApproved) ColorSuccess else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
                createdBy?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.End)
                }
            }
            Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
        }
    }
}

// ── Action Samples Dialog ──────────────────────────────────────────────────────

@Composable
private fun ActionSamplesDialog(
    samples: List<HearingActionSample>,
    isLoading: Boolean,
    onSelect: (HearingActionSample) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "الإجراءات المطلوبة في الجلسة",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                if (isLoading) {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (samples.isEmpty()) {
                    Text(
                        "لا توجد إجراءات",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                } else {
                    LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                        itemsIndexed(samples) { _, sample ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(sample) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Primary)
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                ) {
                                    Text("اختيار", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                                Text(
                                    sample.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                    textAlign = TextAlign.End,
                                )
                            }
                            HorizontalDivider(color = Divider)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(120.dp),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("إلغاء") }
            }
        }
    }
}

// ── Add New Action Dialog ──────────────────────────────────────────────────────

@Composable
private fun AddNewActionDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("إضافة إجراء", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; error = "" },
                    placeholder = { Text("اسم الإجراء", color = TextSecondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider, errorBorderColor = ColorError),
                )
                if (error.isNotEmpty()) Text(error, color = ColorError, style = MaterialTheme.typography.labelSmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("إلغاء") }
                    Button(
                        onClick = { if (text.isBlank()) error = "اسم الإجراء مطلوب" else onConfirm(text.trim()) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("إضافة") }
                }
            }
        }
    }
}

@Composable
fun AddNameDialog(
    title: String,
    placeholder: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; error = "" },
                    placeholder = { Text(placeholder, color = TextSecondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider, errorBorderColor = ColorError),
                )
                if (error.isNotEmpty()) Text(error, color = ColorError, style = MaterialTheme.typography.labelSmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("إلغاء") }
                    Button(
                        onClick = { if (text.isBlank()) error = "الحقل مطلوب" else onConfirm(text.trim()) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("إضافة") }
                }
            }
        }
    }
}

// ── File path helper ───────────────────────────────────────────────────────────

fun getRealPathFromUri(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val fileName = if (columnIndex >= 0) it.getString(columnIndex) else "file"
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = java.io.File(context.cacheDir, fileName)
                inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
                tempFile.absolutePath
            } else null
        }
    } catch (e: Exception) {
        null
    }
}