// app/src/main/java/com/smartfingers/smartlawyerplus/ui/screens/appointments/AddAppointmentScreen.kt

package com.smartfingers.smartlawyerplus.ui.screens.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.smartfingers.smartlawyerplus.domain.model.AppointmentType
import com.smartfingers.smartlawyerplus.domain.model.Party
import com.smartfingers.smartlawyerplus.domain.model.TaskCase
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.screens.sessions.AddNameDialog
import com.smartfingers.smartlawyerplus.ui.screens.sessions.getRealPathFromUri
import com.smartfingers.smartlawyerplus.ui.theme.*
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddAppointmentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.success) { if (state.success) onSaved() }

    // Dialogs
    if (state.showAddClientDialog) {
        AddClientDialog(
            onConfirm = { name, phone, tax -> viewModel.addClient(name, phone, tax) },
            onDismiss = viewModel::dismissAddClientDialog,
        )
    }
    if (state.showAddTypeDialog) {
        AddNameDialog(
            title = "إضافة نوع موعد",
            placeholder = "اسم النوع",
            onConfirm = viewModel::addAppointmentType,
            onDismiss = viewModel::dismissAddTypeDialog,
        )
    }

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
                        "إضافة موعد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {

            // ── 1. Project Type label ──────────────────────────────────────────
            Text(
                "نوع المشروع",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )

            // ── 2. Project Type Row 1: without | case | customer requests ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProjectTypeRadio("بدون", state.projectType == ProjectType.WITHOUT) {
                    viewModel.onProjectTypeSelected(ProjectType.WITHOUT)
                }
                Spacer(Modifier.width(16.dp))
                ProjectTypeRadio("قضية", state.projectType == ProjectType.CASE) {
                    viewModel.onProjectTypeSelected(ProjectType.CASE)
                }
                Spacer(Modifier.width(16.dp))
                ProjectTypeRadio("طلبات عملاء", state.projectType == ProjectType.CUSTOMER_REQUESTS) {
                    viewModel.onProjectTypeSelected(ProjectType.CUSTOMER_REQUESTS)
                }
            }

            // ── 3. Project Type Row 2: consultation | customer requests | other ─
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProjectTypeRadio("استشارة", state.projectType == ProjectType.CONSULTATION) {
                    viewModel.onProjectTypeSelected(ProjectType.CONSULTATION)
                }
                Spacer(Modifier.width(16.dp))
                ProjectTypeRadio("طلبات عملاء", state.projectType == ProjectType.CUSTOMER_REQUESTS) {
                    viewModel.onProjectTypeSelected(ProjectType.CUSTOMER_REQUESTS)
                }
                Spacer(Modifier.width(16.dp))
                ProjectTypeRadio("مشاريع أخرى", state.projectType == ProjectType.OTHER_PROJECTS) {
                    viewModel.onProjectTypeSelected(ProjectType.OTHER_PROJECTS)
                }
            }

            // ── 4. Conditional project dropdown ───────────────────────────────
            if (state.projectType != ProjectType.WITHOUT) {
                val dropdownLabel = when (state.projectType) {
                    ProjectType.CASE -> "القضية"
                    ProjectType.CONSULTATION -> "الاستشارة"
                    ProjectType.CUSTOMER_REQUESTS -> "طلب العميل"
                    ProjectType.OTHER_PROJECTS -> "المشاريع الأخرى"
                    else -> ""
                }
                AppointmentDropdown(
                    label = dropdownLabel,
                    selected = state.selectedProjectName,
                    options = state.projectOptions.map { it.name },
                    onSelect = { name ->
                        val id = state.projectOptions.firstOrNull { it.name == name }?.id?.toString()
                        viewModel.onProjectSelected(id, name)
                    },
                )
            }

            // ── 5. Appointment Type with Add button ───────────────────────────
            AppointmentDropdownWithAdd(
                label = "النوع",
                selected = state.selectedType?.name ?: "",
                options = state.types,
                onSelect = viewModel::onTypeSelected,
                onAddNew = viewModel::openAddTypeDialog,
            )

            // ── 6. Assigned users (multi-select) ──────────────────────────────
            AppointmentMultiSelectDropdown(
                label = "المكلفون بالموعد",
                allOptions = state.employees,
                selected = state.selectedEmployees,
                onToggle = viewModel::onEmployeeToggled,
            )

            // ── 7. Clients dropdown with Add button ───────────────────────────
            AppointmentPartiesDropdownWithAdd(
                label = "العملاء",
                selected = state.selectedParties,
                options = state.parties,
                onToggle = viewModel::onPartyToggled,
                onAddNew = viewModel::openAddClientDialog,
            )

            // ── 8. Date (Gregorian + Hijri) ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val cal = Calendar.getInstance()
                OutlinedButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                viewModel.onDateSelected(
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
                        Text("تاريخ الموعد (م)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                        Text(
                            state.startDate.ifBlank { "--/--/----" },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.startDate.isBlank()) TextSecondary else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
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
                        Text("تاريخ الموعد (هـ)", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                        Text(
                            state.startDateHijri.ifBlank { "--/--/----" },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.startDateHijri.isBlank()) TextSecondary else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            // ── 9. Time (Hour + Minute) ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val cal = Calendar.getInstance()
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, m ->
                                viewModel.onTimeSelected(
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
                        Text("الوقت", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                        Text(
                            state.startTime.ifBlank { "--:--" },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.startTime.isBlank()) TextSecondary else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
            }

            // ── 10. Subject text area ─────────────────────────────────────────
            OutlinedTextField(
                value = state.subject,
                onValueChange = viewModel::onSubjectChange,
                label = { Text("الموضوع", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )

            // ── 11. Attachments ───────────────────────────────────────────────
            Text("المرفقات", style = MaterialTheme.typography.labelMedium, color = Primary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            OutlinedButton(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (state.isUploadingAttachment) Primary else Divider),
                enabled = !state.isUploadingAttachment,
            ) {
                if (state.isUploadingAttachment) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("جاري الرفع...", color = TextSecondary)
                } else {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("إرفاق ملف", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            state.attachments.forEach { att ->
                AppointmentAttachmentRow(
                    name = att.name ?: "-",
                    onRemove = { viewModel.removeAttachment(att.id) },
                )
            }

            // ── Error ─────────────────────────────────────────────────────────
            if (state.error.isNotEmpty()) {
                Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }

            // ── Buttons ───────────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmartLawyerButton(text = "حفظ", onClick = viewModel::save, isLoading = state.isLoading, modifier = Modifier.weight(1f))
                SmartLawyerOutlinedButton(text = "إلغاء", onClick = onBack, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProjectTypeRadio(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() },
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun AppointmentDropdown(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
            trailingIcon = {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(Primary).clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center,
                ) { Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.background).heightIn(max = 220.dp)) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun AppointmentDropdownWithAdd(
    label: String,
    selected: String,
    options: List<AppointmentType>,
    onSelect: (AppointmentType) -> Unit,
    onAddNew: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Primary).clickable { onAddNew() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Add, null, tint = Color.White)
        }
        Box(Modifier.weight(1f)) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                trailingIcon = {
                    Box(Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(Primary).clickable { expanded = !expanded }, contentAlignment = Alignment.Center) {
                        Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.background).heightIn(max = 220.dp)) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                        onClick = { onSelect(option); expanded = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentMultiSelectDropdown(
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
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
            trailingIcon = {
                Box(Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(Primary).clickable { expanded = !expanded }, contentAlignment = Alignment.Center) {
                    Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.background).heightIn(max = 220.dp)) {
            allOptions.forEach { emp ->
                val isChecked = selected.any { it.id == emp.id }
                DropdownMenuItem(
                    text = {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                            Text(emp.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Spacer(Modifier.width(8.dp))
                            Icon(if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank, null, tint = Primary, modifier = Modifier.size(20.dp))
                        }
                    },
                    onClick = { onToggle(emp) },
                )
            }
        }
    }
}

@Composable
private fun AppointmentPartiesDropdownWithAdd(
    label: String,
    selected: List<Party>,
    options: List<Party>,
    onToggle: (Party) -> Unit,
    onAddNew: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = when {
        selected.isEmpty() -> ""
        selected.size == 1 -> selected[0].name
        else -> "${selected[0].name} (+${selected.size - 1})"
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Primary).clickable { onAddNew() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Add, null, tint = Color.White)
        }
        Box(Modifier.weight(1f)) {
            OutlinedTextField(
                value = displayText,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
                trailingIcon = {
                    Box(Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(Primary).clickable { expanded = !expanded }, contentAlignment = Alignment.Center) {
                        Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.background).heightIn(max = 220.dp)) {
                options.forEach { party ->
                    val isChecked = selected.any { it.id == party.id }
                    DropdownMenuItem(
                        text = {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                Text(party.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                Spacer(Modifier.width(8.dp))
                                Icon(if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank, null, tint = Primary, modifier = Modifier.size(20.dp))
                            }
                        },
                        onClick = { onToggle(party) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentAttachmentRow(name: String, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, null, tint = ColorError, modifier = Modifier.size(18.dp))
            }
            Icon(Icons.Default.InsertDriveFile, null, tint = Primary, modifier = Modifier.size(24.dp))
            Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun AddClientDialog(onConfirm: (String, String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("إضافة عميل جديد", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = "" },
                    label = { Text("الاسم", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                    isError = nameError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
                )
                if (nameError.isNotEmpty()) Text(nameError, color = ColorError, style = MaterialTheme.typography.labelSmall)

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; phoneError = "" },
                    label = { Text("رقم الجوال", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                    isError = phoneError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
                )
                if (phoneError.isNotEmpty()) Text(phoneError, color = ColorError, style = MaterialTheme.typography.labelSmall)

                OutlinedTextField(
                    value = tax,
                    onValueChange = { tax = it },
                    label = { Text("الرقم الضريبي", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            var valid = true
                            if (name.isBlank()) { nameError = "الاسم مطلوب"; valid = false }
                            if (phone.isBlank()) { phoneError = "رقم الجوال مطلوب"; valid = false }
                            if (valid) onConfirm(name.trim(), phone.trim(), tax.trim())
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("إضافة") }
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("إلغاء") }
                }
            }
        }
    }
}