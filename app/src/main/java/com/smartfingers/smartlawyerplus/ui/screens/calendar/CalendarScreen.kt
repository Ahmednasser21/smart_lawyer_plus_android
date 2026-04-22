package com.smartfingers.smartlawyerplus.ui.screens.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.domain.model.CalendarDay
import com.smartfingers.smartlawyerplus.domain.model.CalendarEventType
import com.smartfingers.smartlawyerplus.domain.model.DayEvent
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.MonthHeader
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

private val DAY_LABELS = listOf("أحد", "اثن", "ثلا", "أرب", "خمي", "جمع", "سبت")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text = "الأجندة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        // ── Filter dropdowns (Employee + Item Type) ───────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CalendarDropdown(
                modifier = Modifier.weight(1f),
                label = "اسم الموظف",
                selectedName = uiState.selectedEmployee?.name,
                options = uiState.employees.map { it.name },
                isLoading = uiState.isLoadingEmployees,
                onSelect = { index ->
                    viewModel.onEmployeeSelected(
                        if (index == null) null else uiState.employees.getOrNull(index)
                    )
                },
            )
            CalendarDropdown(
                modifier = Modifier.weight(1f),
                label = "نوع العنصر",
                selectedName = uiState.selectedItemType?.name,
                options = uiState.itemTypes.map { it.name },
                isLoading = uiState.isLoadingItemTypes,
                onSelect = { index ->
                    viewModel.onItemTypeSelected(
                        if (index == null) null else uiState.itemTypes.getOrNull(index)
                    )
                },
            )
        }

        // ── Calendar card ─────────────────────────────────────────────────────
        if (uiState.months.isNotEmpty()) {
            val month = uiState.months.getOrNull(uiState.currentMonthIndex)
            if (month != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .pointerInput(uiState.currentMonthIndex) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -40f && uiState.currentMonthIndex < uiState.months.lastIndex) {
                                    viewModel.onMonthChanged(uiState.currentMonthIndex + 1)
                                } else if (dragAmount > 40f && uiState.currentMonthIndex > 0) {
                                    viewModel.onMonthChanged(uiState.currentMonthIndex - 1)
                                }
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {

                        // Month title + nav arrows
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(
                                onClick = {
                                    if (uiState.currentMonthIndex < uiState.months.lastIndex) {
                                        viewModel.onMonthChanged(uiState.currentMonthIndex + 1)
                                    }
                                },
                                enabled = uiState.currentMonthIndex < uiState.months.lastIndex,
                            ) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Next month",
                                    tint = if (uiState.currentMonthIndex < uiState.months.lastIndex) Primary else TextSecondary,
                                )
                            }

                            Text(
                                text = month.monthTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                            )

                            IconButton(
                                onClick = {
                                    if (uiState.currentMonthIndex > 0) {
                                        viewModel.onMonthChanged(uiState.currentMonthIndex - 1)
                                    }
                                },
                                enabled = uiState.currentMonthIndex > 0,
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Prev month",
                                    tint = if (uiState.currentMonthIndex > 0) Primary else TextSecondary,
                                )
                            }
                        }

                        // Day labels header — mirrors iOS monthHeaderCard
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MonthHeader),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                DAY_LABELS.forEach { label ->
                                    Text(
                                        text = label,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Days grid
                        if (uiState.isLoadingEvents) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = Primary, strokeWidth = 2.dp)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp)
                                    // height = rows * cell. Each cell is roughly square at width/7
                                    .height(((month.days.size / 7) * 52 + 8).dp),
                                contentPadding = PaddingValues(0.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                userScrollEnabled = false,
                            ) {
                                items(month.days) { day ->
                                    DayCell(
                                        day = day,
                                        onClick = { viewModel.onDaySelected(day) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Events list for selected day ──────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            if (uiState.selectedDayEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "لا توجد أحداث في هذا اليوم",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            } else {
                items(uiState.selectedDayEvents, key = { it.event.id }) { dayEvent ->
                    CalendarEventCard(dayEvent = dayEvent)
                }
            }
        }
    }
}

// ── Day Cell ──────────────────────────────────────────────────────────────────

@Composable
private fun DayCell(day: CalendarDay, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = when {
            day.isSelected -> MonthHeader
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200),
        label = "dayBg",
    )
    val textColor = when {
        day.isSelected -> Color.White
        !day.isWithinDisplayedMonth -> TextSecondary.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                width = if (!day.isSelected) 0.5.dp else 0.dp,
                color = if (!day.isSelected) Divider else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = day.number,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
            fontSize = 13.sp,
        )
        // Event dots — mirrors iOS CalendarEventCell dots at bottom
        if (day.events.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                day.events.take(3).forEach { dayEvent ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                color = try {
                                    Color(android.graphics.Color.parseColor(dayEvent.event.color ?: "#307891"))
                                } catch (_: Exception) {
                                    Primary
                                }
                            ),
                    )
                }
            }
        }
    }
}

// ── Event Card (below calendar) ───────────────────────────────────────────────

@Composable
fun CalendarEventCard(dayEvent: DayEvent) {
    val event = dayEvent.event
    val typeColor = calendarEventTypeColor(dayEvent.eventType)
    val height = if (dayEvent.eventType == CalendarEventType.APPOINTMENT ||
        dayEvent.eventType == CalendarEventType.HEARING
    ) 100.dp else 72.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Color stripe — mirrors iOS cell left-side color indicator
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(height)
                    .background(
                        color = try {
                            Color(android.graphics.Color.parseColor(event.color ?: "#307891"))
                        } catch (_: Exception) { typeColor },
                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                    ),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Title
                Text(
                    text = event.title ?: event.extendedProperties?.caseName ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                when (dayEvent.eventType) {
                    CalendarEventType.HEARING -> {
                        event.extendedProperties?.courtName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.End,
                            )
                        }
                        event.extendedProperties?.hearingNumber?.let {
                            Text(
                                text = "جلسة رقم $it",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                textAlign = TextAlign.End,
                            )
                        }
                        event.extendedProperties?.startTime?.let {
                            Text(
                                text = it.take(5),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = TextSecondary,
                            )
                        }
                    }

                    CalendarEventType.APPOINTMENT -> {
                        event.extendedProperties?.assignedUsers?.firstOrNull()?.let { user ->
                            Text(
                                text = user,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.End,
                            )
                        }
                        val dateStr = buildString {
                            event.extendedProperties?.startTime?.let { append(it.take(5)); append(" / ") }
                            event.extendedProperties?.startDate?.let { append(it.take(10)) }
                        }
                        if (dateStr.isNotBlank()) {
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = TextSecondary,
                            )
                        }
                    }

                    else -> {
                        // Task, CaseAppeal, Agency — show start date
                        event.extendedProperties?.startDate?.let {
                            Text(
                                text = it.take(10),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }

            // Event type badge
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(typeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = calendarEventTypeName(dayEvent.eventType),
                    style = MaterialTheme.typography.labelSmall,
                    color = typeColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

// ── Dropdown ──────────────────────────────────────────────────────────────────

@Composable
private fun CalendarDropdown(
    modifier: Modifier = Modifier,
    label: String,
    selectedName: String?,
    options: List<String>,
    isLoading: Boolean,
    onSelect: (Int?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, Divider, RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Primary)
            } else {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = selectedName ?: label,
                style = MaterialTheme.typography.bodySmall,
                color = if (selectedName != null) MaterialTheme.colorScheme.onSurface else TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            // "All" option
            DropdownMenuItem(
                text = { Text("الكل", style = MaterialTheme.typography.bodyMedium) },
                onClick = { onSelect(null); expanded = false },
            )
            HorizontalDivider(color = Divider)
            options.forEachIndexed { index, name ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    onClick = { onSelect(index); expanded = false },
                )
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

fun calendarEventTypeColor(type: CalendarEventType): Color = when (type) {
    CalendarEventType.APPOINTMENT -> Color(0xFF3AAD66)   // green
    CalendarEventType.HEARING -> Color(0xFF307893)        // Primary teal
    CalendarEventType.TASK -> Color(0xFF3AABD6)           // ColorTask
    CalendarEventType.CASE_APPEAL -> Color(0xFFE4B24F)   // ColorHearing amber
    CalendarEventType.AGENCY -> Color(0xFFC5C4E8)         // ColorClientRequest
    CalendarEventType.NA -> Color(0xFF9E9E9E)
}

fun calendarEventTypeName(type: CalendarEventType): String = when (type) {
    CalendarEventType.APPOINTMENT -> "موعد"
    CalendarEventType.HEARING -> "جلسة"
    CalendarEventType.TASK -> "مهمة"
    CalendarEventType.CASE_APPEAL -> "قضية"
    CalendarEventType.AGENCY -> "وكالة"
    CalendarEventType.NA -> "أخرى"
}
