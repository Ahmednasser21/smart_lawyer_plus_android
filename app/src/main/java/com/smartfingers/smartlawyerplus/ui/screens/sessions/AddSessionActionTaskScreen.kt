package com.smartfingers.smartlawyerplus.ui.screens.sessions

import android.app.DatePickerDialog
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfingers.smartlawyerplus.domain.model.TaskCategory
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.theme.*
import java.util.Calendar

// ── End date type mirrors iOS EndDateType enum ────────────────────────────────
enum class EndDateType { HOUR, DAY, CALENDAR }

// ── Data returned to caller — mirrors iOS HearingDesc fields ──────────────────
data class SessionActionTaskResult(
    val endDateType: String,         // "0"=hour, "1"=day, "2"=calendar
    val taskCategoryId: String?,
    val checked: Boolean,
    val taskUsers: String?,          // comma-separated ids
    val endDate: String?,
    val startDate: String?,
    val period: String?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionActionTaskScreen(
    employees: List<TaskEmployee>,
    categories: List<TaskCategory>,
    isLoadingEmployees: Boolean = false,
    isLoadingCategories: Boolean = false,
    onLoadEmployees: () -> Unit = {},
    onLoadCategories: () -> Unit = {},
    onBack: () -> Unit,
    onSave: (SessionActionTaskResult) -> Unit,
) {
    val context = LocalContext.current

    // ── Local state ───────────────────────────────────────────────────────────
    var selectedEmployees by remember { mutableStateOf<List<TaskEmployee>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }
    var endDateType by remember { mutableStateOf(EndDateType.DAY) }
    var startDate by remember { mutableStateOf("") }
    var startDateHijri by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endDateHijri by remember { mutableStateOf("") }
    var periodCount by remember { mutableStateOf(0) }

    // validation errors
    var categoryError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }
    var periodError by remember { mutableStateOf(false) }

    fun toHijri(gregorian: String): String = try {
        val parts = gregorian.split("-")
        if (parts.size != 3) ""
        else {
            val d = java.time.LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val h = java.time.chrono.HijrahDate.from(d)
            val y = h.get(java.time.temporal.ChronoField.YEAR)
            val m = h.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
            val day = h.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
            "$y-${m.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        }
    } catch (_: Exception) { "" }

    fun fromHijri(hijri: String): String = try {
        val parts = hijri.split("-")
        if (parts.size != 3) ""
        else {
            val hd = java.time.chrono.HijrahDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val ld = java.time.LocalDate.from(java.time.chrono.HijrahChronology.INSTANCE.date(hd))
            "${ld.year}-${ld.monthValue.toString().padStart(2, '0')}-${ld.dayOfMonth.toString().padStart(2, '0')}"
        }
    } catch (_: Exception) { "" }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "إضافة مهمة للإجراءات المحددة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ── المكلف بالجلسة (multi-select) ────────────────────────────────
            MultiSelectDropdown(
                label = "المكلف بالجلسة",
                allOptions = employees,
                selected = selectedEmployees,
                onToggle = { emp ->
                    selectedEmployees = selectedEmployees.toMutableList().also { list ->
                        if (list.any { it.id == emp.id }) list.removeAll { it.id == emp.id }
                        else list.add(emp)
                    }
                },
            )
            if (isLoadingEmployees && employees.isEmpty()) {
                SideEffect { onLoadEmployees() }
            }

            // ── تصنيف المهمة (single select) ─────────────────────────────────
            Column {
                ActionTaskDropdown(
                    label = "تصنيف المهمة",
                    selected = selectedCategory?.name ?: "",
                    options = categories.map { it.name },
                    isError = categoryError,
                    onExpand = { if (categories.isEmpty()) onLoadCategories() },
                    onSelect = { name ->
                        selectedCategory = categories.firstOrNull { it.name == name }
                        categoryError = false
                    },
                )
                if (isLoadingCategories && categories.isEmpty()) {
                    SideEffect { onLoadCategories() }
                }
                if (categoryError) {
                    Text(
                        text = "تصنيف المهمة مطلوب",
                        color = ColorError,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    )
                }
            }

            // ── تاريخ الجلسة (Gregorian + Hijri) ─────────────────────────────
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Gregorian picker
                    DatePickerButton(
                        label = "تاريخ الجلسة (م)",
                        value = startDate,
                        isError = startDateError,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val cal = Calendar.getInstance()
                            val picker = DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val formatted =
                                        "$y-${(m + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
                                    startDate = formatted
                                    startDateHijri = toHijri(formatted)
                                    startDateError = false
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                            )
                            picker.datePicker.minDate = cal.timeInMillis
                            picker.show()
                        },
                    )
                    // Hijri picker (interactive)
                    DatePickerButton(
                        label = "تاريخ الجلسة (هـ)",
                        value = startDateHijri,
                        isError = startDateError,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showHijriDatePickerDialog(context, startDateHijri) { hijriDate ->
                                startDateHijri = hijriDate
                                startDate = fromHijri(hijriDate)
                                startDateError = false
                            }
                        },
                    )
                }
                if (startDateError) {
                    Text(
                        text = "تاريخ البداية مطلوب",
                        color = ColorError,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            // ── تاريخ الانتهاء المحدد ─────────────────────────────────────────
            SectionLabel("تاريخ الانتهاء المحدد")

            // Radio buttons — hour / day / calendar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf(
                    EndDateType.HOUR to "ساعة",
                    EndDateType.DAY to "يوم",
                    EndDateType.CALENDAR to "تاريخ",
                ).forEach { (type, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            endDateType = type
                            // reset values when switching
                            if (type != EndDateType.CALENDAR) {
                                endDate = ""; endDateHijri = ""
                            } else {
                                periodCount = 0
                            }
                            endDateError = false; periodError = false
                        },
                    ) {
                        RadioButton(
                            selected = endDateType == type,
                            onClick = {
                                endDateType = type
                                if (type != EndDateType.CALENDAR) {
                                    endDate = ""; endDateHijri = ""
                                } else {
                                    periodCount = 0
                                }
                                endDateError = false; periodError = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Primary),
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            // ── Stepper (hour / day) or date picker (calendar) ────────────────
            if (endDateType == EndDateType.CALENDAR) {
                // End date pickers
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DatePickerButton(
                            label = "تاريخ الانتهاء (م)",
                            value = endDate,
                            isError = endDateError,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val cal = Calendar.getInstance()
                                val picker = DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        val formatted =
                                            "$y-${
                                                (m + 1).toString().padStart(2, '0')
                                            }-${d.toString().padStart(2, '0')}"
                                        endDate = formatted
                                        endDateHijri = toHijri(formatted)
                                        endDateError = false
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH),
                                )
                                picker.datePicker.minDate = cal.timeInMillis
                                picker.show()
                            },
                        )
                        // Hijri end date picker (interactive)
                        DatePickerButton(
                            label = "تاريخ الانتهاء (هـ)",
                            value = endDateHijri,
                            isError = endDateError,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                showHijriDatePickerDialog(context, endDateHijri) { hijriDate ->
                                    endDateHijri = hijriDate
                                    endDate = fromHijri(hijriDate)
                                    endDateError = false
                                }
                            },
                        )
                    }
                    if (endDateError) {
                        Text(
                            text = "تاريخ الانتهاء مطلوب",
                            color = ColorError,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            } else {
                // Stepper for hour/day count
                Column {
                    PeriodStepper(
                        label = "مدة المهمة",
                        value = periodCount,
                        isError = periodError,
                        onDecrement = { if (periodCount > 0) { periodCount--; periodError = false } },
                        onIncrement = { periodCount++; periodError = false },
                    )
                    if (periodError) {
                        Text(
                            text = "مدة المهمة مطلوبة",
                            color = ColorError,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Action buttons ────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmartLawyerButton(
                    text = "حفظ",
                    onClick = {
                        // validate
                        var valid = true
                        if (selectedCategory == null) { categoryError = true; valid = false }
                        if (startDate.isBlank()) { startDateError = true; valid = false }
                        if (endDateType == EndDateType.CALENDAR && endDate.isBlank()) {
                            endDateError = true; valid = false
                        } else if (endDateType != EndDateType.CALENDAR && periodCount == 0) {
                            periodError = true; valid = false
                        }
                        if (valid) {
                            onSave(
                                SessionActionTaskResult(
                                    endDateType = when (endDateType) {
                                        EndDateType.HOUR -> "0"
                                        EndDateType.DAY -> "1"
                                        EndDateType.CALENDAR -> "2"
                                    },
                                    taskCategoryId = selectedCategory?.id?.toString(),
                                    checked = true,
                                    taskUsers = selectedEmployees
                                        .joinToString(",") { it.id }
                                        .ifBlank { null },
                                    endDate = endDate.ifBlank { null },
                                    startDate = startDate.ifBlank { null },
                                    period = if (endDateType != EndDateType.CALENDAR)
                                        periodCount.toString() else null,
                                )
                            )
                        }
                    },
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

// ── Small reusable components ─────────────────────────────────────────────────

@Composable
private fun ActionTaskDropdown(
    label: String,
    selected: String,
    options: List<String>,
    isError: Boolean,
    onExpand: () -> Unit,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    label,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Primary)
                        .clickable { onExpand(); expanded = !expanded },
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
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpand(); expanded = !expanded },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Divider,
                errorBorderColor = ColorError,
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

@Composable
private fun DatePickerButton(
    label: String,
    value: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isError) ColorError else Divider,
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp,
            )
            Text(
                text = value.ifBlank { "--/--/----" },
                style = MaterialTheme.typography.bodySmall,
                color = if (value.isBlank()) TextSecondary
                else MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

/** Stepper — mirrors iOS StepperView (minus / count / plus) */
@Composable
private fun PeriodStepper(
    label: String,
    value: Int,
    isError: Boolean,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (isError) ColorError else Divider,
                RoundedCornerShape(8.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Decrement
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(Primary)
                .clickable { onDecrement() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
        }

        // Value + label
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (value == 0) label else "$value",
                style = MaterialTheme.typography.bodyMedium,
                color = if (value == 0) TextSecondary else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (value > 0) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }

        // Increment
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .background(Primary)
                .clickable { onIncrement() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }
    }
}