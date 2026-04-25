package com.smartfingers.smartlawyerplus.ui.screens.sessions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.SessionActionRequired
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.screens.tasks.DropdownSelector
import com.smartfingers.smartlawyerplus.ui.screens.tasks.MultiSelectChips
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

    LaunchedEffect(state.success) { if (state.success) onSaved() }

    // ── Action samples dialog ─────────────────────────────────────────────────
    if (state.showActionSamplesDialog) {
        ActionSamplesDialog(
            samples = state.actionSamples,
            isLoading = state.isLoadingActionSamples,
            onSelect = viewModel::selectActionSample,
            onDismiss = viewModel::dismissActionSamplesDialog,
        )
    }

    // ── Add new action dialog ─────────────────────────────────────────────────
    if (state.showAddActionDialog) {
        AddNewActionDialog(
            onConfirm = viewModel::confirmAddNewAction,
            onDismiss = viewModel::dismissAddActionDialog,
        )
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
                            contentDescription = "Back",
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

            // ── Case ──────────────────────────────────────────────────────────
            DropdownSelector(
                label = "القضية",
                selected = state.selectedCase?.name ?: "",
                options = state.cases.map { it.name },
                onSelect = { name ->
                    state.cases.firstOrNull { it.name == name }
                        ?.let { viewModel.onCaseSelected(it) }
                },
            )

            if (state.isAutoFilling) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary,
                )
            }

            // ── Employees multi-select ────────────────────────────────────────
            MultiSelectChips(
                label = "المكلفون بالجلسة",
                allOptions = state.employees,
                selected = state.selectedEmployees,
                getLabel = { it.name },
                onToggle = viewModel::onEmployeeToggled,
            )

            // ── Hearing number ────────────────────────────────────────────────
            OutlinedTextField(
                value = state.hearingNumber,
                onValueChange = viewModel::onHearingNumberChange,
                label = { Text("رقم الجلسة") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            // ── Hearing type ──────────────────────────────────────────────────
            DropdownSelector(
                label = "نوع الجلسة",
                selected = state.selectedHearingType?.name ?: "",
                options = state.hearingTypes.map { it.name },
                onSelect = { name ->
                    state.hearingTypes.firstOrNull { it.name == name }
                        ?.let { viewModel.onHearingTypeSelected(it) }
                },
            )

            // ── Sub hearing type ──────────────────────────────────────────────
            DropdownSelector(
                label = "نوع الجلسة الفرعي",
                selected = state.selectedSubHearingType?.name ?: "",
                options = state.subHearingTypes.map { it.name },
                onSelect = { name ->
                    state.subHearingTypes.firstOrNull { it.name == name }
                        ?.let { viewModel.onSubHearingTypeSelected(it) }
                },
            )

            // ── Court ─────────────────────────────────────────────────────────
            DropdownSelector(
                label = "المحكمة",
                selected = state.selectedCourt?.name ?: "",
                options = state.courts.map { it.name },
                onSelect = { name ->
                    state.courts.firstOrNull { it.name == name }
                        ?.let { viewModel.onCourtSelected(it) }
                },
            )

            // ── Date ──────────────────────────────────────────────────────────
            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            viewModel.onStartDateSelected(
                                "$y-${(m + 1).toString().padStart(2, '0')}-${
                                    d.toString().padStart(2, '0')
                                }"
                            )
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH),
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        if (state.startDate.isBlank()) "تاريخ الجلسة (م)" else state.startDate,
                        color = if (state.startDate.isBlank()) TextSecondary
                        else MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // ── Time ──────────────────────────────────────────────────────────
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        if (state.startTime.isBlank()) "وقت الجلسة" else state.startTime,
                        color = if (state.startTime.isBlank()) TextSecondary
                        else MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // ── Judge name ────────────────────────────────────────────────────
            OutlinedTextField(
                value = state.judgeName,
                onValueChange = viewModel::onJudgeNameChange,
                label = { Text("اسم القاضي") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            // ── Court circle ──────────────────────────────────────────────────
            OutlinedTextField(
                value = state.courtCircle,
                onValueChange = viewModel::onCourtCircleChange,
                label = { Text("دائرة المحكمة") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            // ── Judge office number ───────────────────────────────────────────
            OutlinedTextField(
                value = state.judgeOfficeNumber,
                onValueChange = viewModel::onJudgeOfficeNumberChange,
                label = { Text("بريد الدائرة") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            // ── Actions required card ─────────────────────────────────────────
            ActionsRequiredCard(
                actions = state.actionsRequired,
                onAdd = viewModel::addActionRequired,
                onRemove = viewModel::removeActionRequired,
                onTextChange = viewModel::updateActionText,
                onToggleChecked = viewModel::toggleActionChecked,
                onChooseSample = { index -> viewModel.openActionSamplesDialog(index) },
                onAddNew = { index -> viewModel.openAddActionDialog(index) },
            )

            // ── Required docs ─────────────────────────────────────────────────
            OutlinedTextField(
                value = state.requiredDocs,
                onValueChange = viewModel::onRequiredDocsChange,
                label = { Text("المستندات المطلوبة") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            // ── Hearing notes ─────────────────────────────────────────────────
            OutlinedTextField(
                value = state.hearingDesc,
                onValueChange = viewModel::onHearingDescChange,
                label = { Text("ملاحظات الجلسة") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            if (state.error.isNotEmpty()) {
                Text(
                    state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
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
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "الإجراءات المطلوبة في الجلسة",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Add new row button
                    SmallActionButton(
                        text = "إضافة",
                        onClick = onAdd,
                    )
                }
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
                    if (index < actions.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                    }
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
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Checkbox
        IconButton(
            onClick = onToggleChecked,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = if (action.isChecked) Icons.Default.CheckBox
                else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp),
            )
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
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(6.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Divider,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
            ),
        )

        // Add new action button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Primary)
                .clickable { onAddNew() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }

        // Choose from samples button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Secondary)
                .clickable { onChooseSample() }
                .padding(horizontal = 6.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("اختيار", style = MaterialTheme.typography.labelSmall, color = Color.White)
        }

        // Delete button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(28.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = ColorError,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SmallActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Primary)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = Color.White)
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
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "الإجراءات المطلوبة في الجلسة",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                if (isLoading) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center,
                    ) {
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
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        itemsIndexed(samples) { _, sample ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(sample) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = sample.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Primary)
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        "اختيار",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                    )
                                }
                            }
                            HorizontalDivider(color = Divider)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(120.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("إلغاء")
                }
            }
        }
    }
}

// ── Add New Action Dialog ──────────────────────────────────────────────────────

@Composable
private fun AddNewActionDialog(
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "إضافة إجراء",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; error = "" },
                    placeholder = {
                        Text(
                            "اسم الإجراء",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Divider,
                        errorBorderColor = ColorError,
                    ),
                )
                if (error.isNotEmpty()) {
                    Text(error, color = ColorError, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            if (text.isBlank()) {
                                error = "اسم الإجراء مطلوب"
                            } else {
                                onConfirm(text.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("إضافة") }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                    ) { Text("إلغاء") }
                }
            }
        }
    }
}