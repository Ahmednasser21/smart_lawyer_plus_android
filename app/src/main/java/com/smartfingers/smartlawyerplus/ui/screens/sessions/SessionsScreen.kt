package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

// Expose so MainScreen can wire the filter icon action
typealias OnOpenSessionFilter = () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    onSessionClick: (Int) -> Unit = {},
    onFilterIconReady: (OnOpenSessionFilter) -> Unit = {},
    viewModel: SessionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Expose the open-filter callback to the parent (MainScreen) for the AppBar icon
    LaunchedEffect(Unit) { onFilterIconReady(viewModel::openFilterSheet) }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= uiState.sessions.size - 3 && uiState.hasMore && !uiState.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadMore() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ── Period dropdown + status chips row ──
        SessionsFilterBar(
            statuses = uiState.statuses,
            periods = uiState.periods,
            selectedPeriod = uiState.selectedPeriod,
            onStatusSelected = viewModel::selectStatus,
            onPeriodSelected = viewModel::selectPeriod,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.sessions.isEmpty() && !uiState.isLoading) {
                EmptySessionsState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.sessions.distinctBy { it.id }, key = { it.id }) { session ->
                        SessionCard(session = session, onClick = { onSessionClick(session.id) })
                    }
                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Primary,
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Filter bottom sheet ──
    if (uiState.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissFilterSheet,
            sheetState = sheetState,
        ) {
            SessionsFilterSheet(
                periods = uiState.periods,
                currentFilter = uiState.filter,
                onApply = viewModel::applyFilter,
                onCancel = viewModel::dismissFilterSheet,
            )
        }
    }
}

// ─────────────────────────────────────────────
// Filter bar (period dropdown + status chips)
// ─────────────────────────────────────────────

@Composable
private fun SessionsFilterBar(
    statuses: List<HearingStatus>,
    periods: List<HearingPeriod>,
    selectedPeriod: HearingPeriod?,
    onStatusSelected: (HearingStatus) -> Unit,
    onPeriodSelected: (HearingPeriod?) -> Unit,
) {
    var showPeriodMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Scrollable status chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(statuses) { status ->
                SessionStatusChip(
                    label = status.name,
                    isSelected = status.isSelected,
                    accentColor = hearingStatusColor(status.id),
                    onClick = { onStatusSelected(status) },
                )
            }
        }

        // Period dropdown
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Divider, RoundedCornerShape(8.dp))
                    .clickable { showPeriodMenu = true }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = selectedPeriod?.name ?: "الفترة",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(16.dp),
                )
            }

            DropdownMenu(
                expanded = showPeriodMenu,
                onDismissRequest = { showPeriodMenu = false },
            ) {
                if (selectedPeriod != null) {
                    DropdownMenuItem(
                        text = { Text("الكل") },
                        onClick = { onPeriodSelected(null); showPeriodMenu = false },
                    )
                }
                periods.forEach { period ->
                    DropdownMenuItem(
                        text = { Text(period.name) },
                        onClick = { onPeriodSelected(period); showPeriodMenu = false },
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Filter bottom sheet (matches iOS filter sheet)
// ─────────────────────────────────────────────

@Composable
private fun SessionsFilterSheet(
    periods: List<HearingPeriod>,
    currentFilter: HearingFilter,
    onApply: (HearingFilter) -> Unit,
    onCancel: () -> Unit,
) {
    var courtId by remember { mutableStateOf(currentFilter.courtId ?: "") }
    var judgeName by remember { mutableStateOf(currentFilter.judgeName ?: "") }
    var caseId by remember { mutableStateOf(currentFilter.caseId ?: "") }
    var hearingTypeId by remember { mutableStateOf(currentFilter.hearingTypeId ?: "") }
    var subHearingTypeId by remember { mutableStateOf(currentFilter.subHearingTypeId ?: "") }
    var assignedUserId by remember { mutableStateOf(currentFilter.assignedUserId ?: "") }
    var branchId by remember { mutableStateOf(currentFilter.branchId ?: "") }
    var clientId by remember { mutableStateOf(currentFilter.clientId ?: "") }

    var showResultsCountMenu by remember { mutableStateOf(false) }
    var selectedResultsCount by remember { mutableStateOf<Int?>(null) }
    val resultCountOptions = listOf(10, 20, 50, 100)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "تصنيف الجلسات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        // Row 1: عدد النتائج + المحكمة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // عدد النتائج dropdown
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = selectedResultsCount?.toString() ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("عدد النتائج") },
                    trailingIcon = {
                        IconButton(onClick = { showResultsCountMenu = true }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                DropdownMenu(
                    expanded = showResultsCountMenu,
                    onDismissRequest = { showResultsCountMenu = false },
                ) {
                    resultCountOptions.forEach { count ->
                        DropdownMenuItem(
                            text = { Text(count.toString()) },
                            onClick = { selectedResultsCount = count; showResultsCountMenu = false },
                        )
                    }
                }
            }

            // المحكمة
            OutlinedTextField(
                value = courtId,
                onValueChange = { courtId = it },
                placeholder = { Text("المحكمة") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        // Row 2: القضية + نوع الجلسة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = caseId,
                onValueChange = { caseId = it },
                placeholder = { Text("القضية") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = hearingTypeId,
                onValueChange = { hearingTypeId = it },
                placeholder = { Text("نوع الجلسة") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        // Row 3: نوع الجلسة الفرعي + اسم القاضي
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = judgeName,
                onValueChange = { judgeName = it },
                placeholder = { Text("اسم القاضي") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = subHearingTypeId,
                onValueChange = { subHearingTypeId = it },
                placeholder = { Text("نوع الجلسة الفرعي") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        // Row 4: المكلف بالجلسة + الفترة (dropdown)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = assignedUserId,
                onValueChange = { assignedUserId = it },
                placeholder = { Text("المكلف بالجلسة") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = branchId,
                onValueChange = { branchId = it },
                placeholder = { Text("الفرع") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        // Row 5: عميل + رقم جوال العميل
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = clientId,
                onValueChange = { clientId = it },
                placeholder = { Text("عميل") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("رقم جوال العميل") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        // Row 6: رقم العميل (single field, half width)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("رقم العميل") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
            ) {
                Text("إلغاء")
            }
            Button(
                onClick = {
                    onApply(
                        currentFilter.copy(
                            courtId = courtId.ifBlank { null },
                            judgeName = judgeName.ifBlank { null },
                            caseId = caseId.ifBlank { null },
                            hearingTypeId = hearingTypeId.ifBlank { null },
                            subHearingTypeId = subHearingTypeId.ifBlank { null },
                            assignedUserId = assignedUserId.ifBlank { null },
                            branchId = branchId.ifBlank { null },
                            clientId = clientId.ifBlank { null },
                            pageSize = selectedResultsCount ?: currentFilter.pageSize,
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text("بحث")
            }
        }
    }
}

// ─────────────────────────────────────────────
// Status chip
// ─────────────────────────────────────────────

@Composable
private fun SessionStatusChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(36.dp)
                .background(
                    color = if (isSelected) accentColor else Divider,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else TextSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

// ─────────────────────────────────────────────
// Session card
// ─────────────────────────────────────────────

@Composable
fun SessionCard(session: Session, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f),
            ) {
                session.hearingTypeName?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.SemiBold)
                }
                session.courtName?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                session.remainingDays?.let { days ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Primary, modifier = Modifier.size(12.dp))
                        Text("$days يوم", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        val firstUser = session.assignedUsers.firstOrNull()
                        val userDisplay = if (session.assignedUsers.size > 1) "${firstUser ?: ""} ..." else firstUser ?: ""
                        if (userDisplay.isNotBlank()) {
                            Text(userDisplay, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 160.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        session.caseName?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = Primary, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 160.dp))
                        }
                    }
                    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Icon(painter = painterResource(R.drawable.icons8_avatar_100), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.fillMaxSize())
                    }
                }

                val dateDisplay = buildString {
                    session.startTime?.let { append(it.take(5)); append(" / ") }
                    session.startDate?.let { append(it.take(10)) }
                    session.startDateHijri?.let { append(" / "); append(it) }
                }
                if (dateDisplay.isNotBlank()) {
                    Text(dateDisplay, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.End)
                }
                session.hearingNumber?.let {
                    Text(it.toString(), style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.End)
                }
            }
        }
    }
}

@Composable
private fun EmptySessionsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("لا توجد جلسات", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

private fun hearingStatusColor(statusId: Int): Color = when (statusId) {
    1    -> Color(0xFF37B958)
    2    -> Color(0xFF307893)
    3    -> Color(0xFFA50900)
    else -> Primary
}