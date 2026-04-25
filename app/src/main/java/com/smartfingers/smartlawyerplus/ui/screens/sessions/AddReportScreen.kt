package com.smartfingers.smartlawyerplus.ui.screens.sessions

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.domain.model.ReportAttachment
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportScreen(
    hearingId: Int,
    reportId: Int?,
    subHearingTypeName: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(hearingId) {
        viewModel.init(hearingId, reportId, subHearingTypeName)
    }

    // Navigate on success
    LaunchedEffect(uiState.success) {
        if (uiState.success) onSaved()
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val path = getRealPath(context, it)
            val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
            if (path != null) viewModel.uploadFile(path, mimeType)
        }
    }

    // Add new hearing type dialog
    if (uiState.showAddHearingTypeDialog) {
        AddNewItemDialog(
            title = "إضافة نوع جلسة",
            placeholder = "اسم النوع",
            onConfirm = { name ->
                viewModel.dismissAddHearingTypeDialog()
                // In a real implementation, this would call addHearingType API
                // For now dismiss — the field remains unchanged
            },
            onDismiss = viewModel::dismissAddHearingTypeDialog,
        )
    }

    // Error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        if (uiState.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.error)
            viewModel.clearError()
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (uiState.isEditMode) "تعديل التقرير" else "إضافة تقرير",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    navigationIcon ={
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back",
                                tint = Primary,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    },
                    actions = {
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            },
        ) { padding ->

            when {
                uiState.isLoadingReport -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {

                        // ── 1. Hearing type dropdown with add button ───────────────
                        SectionLabel("نوع الجلسة")
                        DropdownWithAddButton(
                            label = "اختر نوع الجلسة",
                            selected = uiState.selectedHearingType?.name ?: "",
                            options = uiState.hearingTypes,
                            getLabel = { it.name },
                            onSelect = viewModel::onHearingTypeSelected,
                            onAddNew = viewModel::openAddHearingTypeDialog,
                        )

                        // ── 2. Print model dropdown ───────────────────────────────
                        SectionLabel("نموذج الطباعة")
                        ReportDropdownField(
                            label = "اختر نموذج الطباعة",
                            selected = uiState.selectedFormTemplate?.title ?: "",
                            options = uiState.formTemplates,
                            getLabel = { it.title },
                            onSelect = viewModel::onFormTemplateSelected,
                        )

                        // ── 3. Session summary ────────────────────────────────────
                        SectionLabel("ملخص وقائع الجلسة")
                        OutlinedTextField(
                            value = uiState.sessionSummary,
                            onValueChange = viewModel::onSessionSummaryChange,
                            placeholder = {
                                Text(
                                    "أدخل ملخص وقائع الجلسة",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Divider,
                            ),
                        )

                        // ── 4. Court decision ─────────────────────────────────────
                        SectionLabel("قرار المحكمة")
                        OutlinedTextField(
                            value = uiState.courtDecision,
                            onValueChange = viewModel::onCourtDecisionChange,
                            placeholder = {
                                Text(
                                    "أدخل قرار المحكمة",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Divider,
                            ),
                        )

                        // ── 5. Decision section (animated, shown when "نطق بالحكم") ─
                        AnimatedVisibility(
                            visible = uiState.showDecisionSection,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                                // Judgment type dropdown
                                SectionLabel("تفاصيل الحكم")
                                ReportDropdownField(
                                    label = "اختر نوع الحكم",
                                    selected = uiState.selectedJudgmentType?.name ?: "",
                                    options = uiState.judgmentTypes,
                                    getLabel = { it.name },
                                    onSelect = viewModel::onJudgmentTypeSelected,
                                )

                                // Judgment date picker
                                SectionLabel("تاريخ استلام الحكم")
                                OutlinedButton(
                                    onClick = {
                                        val cal = Calendar.getInstance()
                                        DatePickerDialog(
                                            context,
                                            { _, y, m, d ->
                                                viewModel.onJudgmentDateSelected(
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
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Divider
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                val cal = Calendar.getInstance()
                                                DatePickerDialog(
                                                    context,
                                                    { _, y, m, d ->
                                                        viewModel.onJudgmentDateSelected(
                                                            "$y-${(m + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
                                                        )
                                                    },
                                                    cal.get(Calendar.YEAR),
                                                    cal.get(Calendar.MONTH),
                                                    cal.get(Calendar.DAY_OF_MONTH),
                                                ).show()
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("تاريخ استلام الحكم (م)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                                                Text(
                                                    text = uiState.judgmentDate.ifBlank { "--/--/----" },
                                                    color = if (uiState.judgmentDate.isBlank()) TextSecondary else MaterialTheme.colorScheme.onBackground,
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                showHijriDatePickerDialog(context, uiState.judgmentDateHijri) { hijriDate ->
                                                    viewModel.onJudgmentHijriDateSelected(hijriDate)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("تاريخ استلام الحكم (هـ)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                                                Text(
                                                    text = uiState.judgmentDateHijri.ifBlank { "--/--/----" },
                                                    color = if (uiState.judgmentDateHijri.isBlank()) TextSecondary else MaterialTheme.colorScheme.onBackground,
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                    }
                                }

                                // Judgment summary text
                                SectionLabel("ملخص نطق الحكم")
                                OutlinedTextField(
                                    value = uiState.judgmentSummary,
                                    onValueChange = viewModel::onJudgmentSummaryChange,
                                    placeholder = {
                                        Text(
                                            "أدخل ملخص نطق الحكم",
                                            color = TextSecondary,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    },
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

                                // Checkboxes row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                                ) {
                                    // Appealable
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { viewModel.onAppealableToggle() },
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isAppealable)
                                                Icons.Default.CheckBox
                                            else Icons.Default.CheckBoxOutlineBlank,
                                            contentDescription = null,
                                            tint = Primary,
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            "قابل للاستئناف",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Primary,
                                        )
                                    }
                                    // Urgent appeal
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            if (uiState.isAppealable)
                                                viewModel.onUrgentAppealToggle()
                                        },
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isUrgentAppeal)
                                                Icons.Default.CheckBox
                                            else Icons.Default.CheckBoxOutlineBlank,
                                            contentDescription = null,
                                            tint = if (uiState.isAppealable) Primary
                                            else TextSecondary,
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            "استئناف مستعجل",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (uiState.isAppealable) Primary
                                            else TextSecondary,
                                        )
                                    }
                                }
                            }
                        }

                        // ── 6. Upload docs button ─────────────────────────────────
                        SectionLabel("المرفقات")
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (uiState.isUploadingFile) Primary else Divider,
                            ),
                            enabled = !uiState.isUploadingFile,
                        ) {
                            if (uiState.isUploadingFile) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Primary,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("جاري الرفع...", color = TextSecondary)
                            } else {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = null,
                                    tint = Primary,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "إرفاق ملف",
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }

                        // ── 7. Attachments list ───────────────────────────────────
                        if (uiState.attachments.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.attachments.forEach { att ->
                                    ReportAttachmentRow(
                                        attachment = att,
                                        onRemove = { viewModel.removeAttachment(att.id) },
                                    )
                                }
                            }
                        }

                        // ── 8. Save / Cancel buttons ──────────────────────────────
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SmartLawyerButton(
                                text = "حفظ",
                                onClick = viewModel::save,
                                isLoading = uiState.isLoading,
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
        }
    }
}

// ── Reusable composables ───────────────────────────────────────────────────────

@Composable
private fun <T> ReportDropdownField(
    label: String,
    selected: String,
    options: List<T>,
    getLabel: (T) -> String,
    onSelect: (T?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
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
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
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
                .heightIn(max = 240.dp),
        ) {
            if (selected.isNotBlank()) {
                DropdownMenuItem(
                    text = { Text("الكل", style = MaterialTheme.typography.bodyMedium) },
                    onClick = { onSelect(null); expanded = false },
                )
                HorizontalDivider(color = Divider)
            }
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(getLabel(option), style = MaterialTheme.typography.bodyMedium)
                            if (selected == getLabel(option)) {
                                Text("✓", color = Primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun <T> DropdownWithAddButton(
    label: String,
    selected: String,
    options: List<T>,
    getLabel: (T) -> String,
    onSelect: (T?) -> Unit,
    onAddNew: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
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
                    .heightIn(max = 240.dp),
            ) {
                if (selected.isNotBlank()) {
                    DropdownMenuItem(
                        text = { Text("الكل", style = MaterialTheme.typography.bodyMedium) },
                        onClick = { onSelect(null); expanded = false },
                    )
                    HorizontalDivider(color = Divider)
                }
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(getLabel(option), style = MaterialTheme.typography.bodyMedium)
                                if (selected == getLabel(option)) {
                                    Text("✓", color = Primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        onClick = { onSelect(option); expanded = false },
                    )
                }
            }
        }
        // Add new button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary)
                .clickable { onAddNew() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ReportAttachmentRow(
    attachment: ReportAttachment,
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
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(28.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.name ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (attachment.isApproved) ColorSuccess
                    else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                attachment.createdBy?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
            if (attachment.isApproved) {
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
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = ColorError,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun AddNewItemDialog(
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
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
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
                            placeholder,
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
                            if (text.isBlank()) error = "الحقل مطلوب"
                            else onConfirm(text.trim())
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

// Helper — get real path from URI (add to utils or inline)
private fun getRealPath(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val fileName = if (columnIndex >= 0) it.getString(columnIndex) else "file"
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = java.io.File(context.cacheDir, fileName)
                inputStream?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }
                tempFile.absolutePath
            } else null
        }
    } catch (e: Exception) {
        null
    }
}