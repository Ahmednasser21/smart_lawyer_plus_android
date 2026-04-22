package com.smartfingers.smartlawyerplus.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerTextField
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة مهمة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Task number (read-only)
            OutlinedTextField(
                value = state.taskNumber,
                onValueChange = {},
                readOnly = true,
                label = { Text("رقم المهمة") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )

            // Secret checkbox
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.onSecretToggle() }) {
                Checkbox(
                    checked = state.isSecret,
                    onCheckedChange = { viewModel.onSecretToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = Primary),
                )
                Text("سري", style = MaterialTheme.typography.bodyMedium)
            }

            // Task name
            SmartLawyerTextField(
                value = state.taskName,
                onValueChange = viewModel::onNameChange,
                placeholder = "اسم المهمة",
                modifier = Modifier.fillMaxWidth(),
                errorText = state.taskNameError,
            )

            // Priority dropdown
            DropdownSelector(
                label = "الأولوية",
                selected = state.selectedPriority?.name ?: "",
                options = state.priorities.map { it.name },
                onSelect = { name -> state.priorities.firstOrNull { it.name == name }?.let { viewModel.onPrioritySelected(it) } },
            )

            // Category dropdown
            DropdownSelector(
                label = "تصنيف المهمة",
                selected = state.selectedCategory?.name ?: "",
                options = state.categories.map { it.name },
                onSelect = { name -> state.categories.firstOrNull { it.name == name }?.let { viewModel.onCategorySelected(it) } },
            )

            // Employees multi-select
            MultiSelectChips(
                label = "المكلفون",
                allOptions = state.employees,
                selected = state.selectedEmployees,
                getLabel = { it.name },
                onToggle = viewModel::onEmployeeToggled,
            )

            // Case dropdown
            DropdownSelector(
                label = "القضية",
                selected = state.selectedCase?.name ?: "",
                options = listOf("بدون") + state.cases.map { it.name },
                onSelect = { name ->
                    if (name == "بدون") viewModel.onCaseSelected(null)
                    else state.cases.firstOrNull { it.name == name }?.let { viewModel.onCaseSelected(it) }
                },
            )

            // Request message
            OutlinedTextField(
                value = state.requestMessage,
                onValueChange = viewModel::onMessageChange,
                label = { Text("المستندات المطلوبة") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )

            if (state.error.isNotEmpty()) {
                Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.ifBlank { label },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onSelect(opt); expanded = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> MultiSelectChips(
    label: String,
    allOptions: List<T>,
    selected: List<T>,
    getLabel: (T) -> String,
    onToggle: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        if (allOptions.isEmpty()) {
            Text("جار التحميل...", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allOptions.forEach { option ->
                    val isSelected = selected.any { getLabel(it) == getLabel(option) }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggle(option) },
                        label = { Text(getLabel(option), style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }
        }
    }
}