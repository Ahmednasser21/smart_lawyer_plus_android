package com.smartfingers.smartlawyerplus.ui.screens.sessions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
fun AddSessionScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddSessionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.success) { if (state.success) onSaved() }

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
                            modifier = Modifier.size(28.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DropdownSelector(
                label = "القضية",
                selected = state.selectedCase?.name ?: "",
                options = state.cases.map { it.name },
                onSelect = { name -> state.cases.firstOrNull { it.name == name }?.let { viewModel.onCaseSelected(it) } },
            )

            MultiSelectChips(
                label = "المكلفون بالجلسة",
                allOptions = state.employees,
                selected = state.selectedEmployees,
                getLabel = { it.name },
                onToggle = viewModel::onEmployeeToggled,
            )

            OutlinedTextField(
                value = state.hearingNumber,
                onValueChange = viewModel::onHearingNumberChange,
                label = { Text("رقم الجلسة") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )

            DropdownSelector(
                label = "نوع الجلسة",
                selected = state.selectedHearingType?.name ?: "",
                options = state.hearingTypes.map { it.name },
                onSelect = { name -> state.hearingTypes.firstOrNull { it.name == name }?.let { viewModel.onHearingTypeSelected(it) } },
            )

            DropdownSelector(
                label = "المحكمة",
                selected = state.selectedCourt?.name ?: "",
                options = state.courts.map { it.name },
                onSelect = { name -> state.courts.firstOrNull { it.name == name }?.let { viewModel.onCourtSelected(it) } },
            )

            // Date picker button
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
                Text(if (state.startDate.isBlank()) "تاريخ الجلسة (م)" else state.startDate)
            }

            // Time picker button
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
                Text(if (state.startTime.isBlank()) "وقت الجلسة" else state.startTime)
            }

            OutlinedTextField(
                value = state.judgeName,
                onValueChange = viewModel::onJudgeNameChange,
                label = { Text("اسم القاضي") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )

            OutlinedTextField(
                value = state.courtCircle,
                onValueChange = viewModel::onCourtCircleChange,
                label = { Text("دائرة المحكمة") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Divider),
            )

            OutlinedTextField(
                value = state.hearingDesc,
                onValueChange = viewModel::onHearingDescChange,
                label = { Text("ملاحظات الجلسة") },
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