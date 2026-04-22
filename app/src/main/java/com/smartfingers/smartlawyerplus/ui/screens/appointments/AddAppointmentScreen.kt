package com.smartfingers.smartlawyerplus.ui.screens.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.screens.tasks.DropdownSelector
import com.smartfingers.smartlawyerplus.ui.screens.tasks.MultiSelectChips
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة موعد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
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
            DropdownSelector(
                label = "نوع الموعد",
                selected = state.selectedType?.name ?: "",
                options = state.types.map { it.name },
                onSelect = { name -> state.types.firstOrNull { it.name == name }?.let { viewModel.onTypeSelected(it) } },
            )

            MultiSelectChips(
                label = "المسؤولون عن الموعد",
                allOptions = state.employees,
                selected = state.selectedEmployees,
                getLabel = { it.name },
                onToggle = viewModel::onEmployeeToggled,
            )

            MultiSelectChips(
                label = "الأطراف",
                allOptions = state.parties,
                selected = state.selectedParties,
                getLabel = { it.name },
                onToggle = viewModel::onPartyToggled,
            )

            DropdownSelector(
                label = "القضية (اختياري)",
                selected = state.selectedCase?.name ?: "",
                options = listOf("بدون") + state.cases.map { it.name },
                onSelect = { name ->
                    if (name == "بدون") viewModel.onCaseSelected(null)
                    else state.cases.firstOrNull { it.name == name }?.let { viewModel.onCaseSelected(it) }
                },
            )

            // Date picker
            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        viewModel.onStartDateSelected("$y-${(m+1).toString().padStart(2,'0')}-${d.toString().padStart(2,'0')}")
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(state.startDate.ifBlank { "تاريخ الموعد (م)" })
            }

            // Time picker
            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    TimePickerDialog(context, { _, h, m ->
                        viewModel.onStartTimeSelected("${h.toString().padStart(2,'0')}:${m.toString().padStart(2,'0')}")
                    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(state.startTime.ifBlank { "وقت الموعد" })
            }

            OutlinedTextField(
                value = state.subject,
                onValueChange = viewModel::onSubjectChange,
                label = { Text("الموضوع") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4,
                shape = RoundedCornerShape(8.dp),
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