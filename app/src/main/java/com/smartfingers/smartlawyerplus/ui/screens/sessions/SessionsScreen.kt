package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
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
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

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
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
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

    if (uiState.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissFilterSheet,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            SessionsFilterSheet(
                uiState = uiState,
                onLoadCourts = viewModel::loadCourtsIfNeeded,
                onLoadCases = viewModel::loadCasesIfNeeded,
                onLoadHearingTypes = viewModel::loadHearingTypesIfNeeded,
                onLoadSubHearingTypes = viewModel::loadSubHearingTypesIfNeeded,
                onLoadEmployees = viewModel::loadEmployeesIfNeeded,
                onLoadBranches = viewModel::loadBranchesIfNeeded,
                onLoadParties = viewModel::loadPartiesIfNeeded,
                onApply = viewModel::applyFilter,
                onCancel = viewModel::dismissFilterSheet,
            )
        }
    }
}

// ─── Period dropdown + Status chips bar ───────────────────────────────────────

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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Period dropdown (left side, matching iOS top-left position)
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
                Icon(
                    if (showPeriodMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(16.dp),
                )
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "الفترة",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp,
                    )
                    Text(
                        text = selectedPeriod?.name ?: "اليوم",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                    )
                }
            }

            DropdownMenu(
                expanded = showPeriodMenu,
                onDismissRequest = { showPeriodMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            ) {
                if (selectedPeriod != null) {
                    DropdownMenuItem(
                        text = { Text("الكل", style = MaterialTheme.typography.bodyMedium) },
                        onClick = { onPeriodSelected(null); showPeriodMenu = false },
                        leadingIcon = {
                            if (selectedPeriod == null)
                                Icon(painterResource(R.drawable.ic_close), null, modifier = Modifier.size(16.dp))
                        },
                    )
                    HorizontalDivider(color = Divider)
                }
                periods.forEach { period ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(period.name, style = MaterialTheme.typography.bodyMedium)
                                if (selectedPeriod?.id == period.id) {
                                    Text("✓", color = Primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        onClick = { onPeriodSelected(period); showPeriodMenu = false },
                    )
                }
            }
        }

        // Scrollable status chips (right side)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
            reverseLayout = true, // RTL — rightmost chip first
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
    }
}

// ─── Filter bottom sheet with lazy dropdowns ──────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionsFilterSheet(
    uiState: SessionsUiState,
    onLoadCourts: () -> Unit,
    onLoadCases: () -> Unit,
    onLoadHearingTypes: () -> Unit,
    onLoadSubHearingTypes: () -> Unit,
    onLoadEmployees: () -> Unit,
    onLoadBranches: () -> Unit,
    onLoadParties: () -> Unit,
    onApply: (HearingFilter) -> Unit,
    onCancel: () -> Unit,
) {
    var selectedCourt by remember { mutableStateOf<FilterOption?>(null) }
    var selectedCase by remember { mutableStateOf<FilterOption?>(null) }
    var selectedHearingType by remember { mutableStateOf<FilterOption?>(null) }
    var selectedSubHearingType by remember { mutableStateOf<FilterOption?>(null) }
    var judgeName by remember { mutableStateOf(uiState.pendingFilter.judgeName ?: "") }
    var selectedEmployee by remember { mutableStateOf<FilterOption?>(null) }
    var selectedPeriod by remember { mutableStateOf<HearingPeriod?>(null) }
    var selectedBranch by remember { mutableStateOf<FilterOption?>(null) }
    var selectedClient by remember { mutableStateOf<FilterOption?>(null) }
    var clientPhone by remember { mutableStateOf("") }
    var clientNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Title
        Text(
            text = "تصنيف الجلسات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
        )

        // Row 1: Court + Number of results (placeholder)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "المحكمة",
                selectedOption = selectedCourt,
                options = uiState.courts,
                isLoading = uiState.isLoadingCourts,
                onExpand = onLoadCourts,
                onSelect = { selectedCourt = it },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "القضية",
                selectedOption = selectedCase,
                options = uiState.cases,
                isLoading = uiState.isLoadingCases,
                onExpand = onLoadCases,
                onSelect = { selectedCase = it },
            )
        }

        // Row 2: Hearing type + Sub hearing type
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "نوع الجلسة",
                selectedOption = selectedHearingType,
                options = uiState.hearingTypes,
                isLoading = uiState.isLoadingHearingTypes,
                onExpand = onLoadHearingTypes,
                onSelect = { selectedHearingType = it },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "نوع الجلسة الفرعي",
                selectedOption = selectedSubHearingType,
                options = uiState.subHearingTypes,
                isLoading = uiState.isLoadingSubHearingTypes,
                onExpand = onLoadSubHearingTypes,
                onSelect = { selectedSubHearingType = it },
            )
        }

        // Row 3: Judge name (text) + Person in charge (dropdown)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = judgeName,
                onValueChange = { judgeName = it },
                placeholder = { Text("اسم القاضي", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "المكلف بالجلسة",
                selectedOption = selectedEmployee,
                options = uiState.employees,
                isLoading = uiState.isLoadingEmployees,
                onExpand = onLoadEmployees,
                onSelect = { selectedEmployee = it },
            )
        }

        // Row 4: Period (from uiState.periods) + Branch
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "الفترة",
                selectedOption = selectedPeriod?.let { FilterOption(it.id.toString(), it.name) },
                options = uiState.periods.map { FilterOption(it.id.toString(), it.name) },
                isLoading = false,
                onExpand = {},
                onSelect = { opt ->
                    selectedPeriod = uiState.periods.firstOrNull { it.id.toString() == opt.id }
                },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "الفرع",
                selectedOption = selectedBranch,
                options = uiState.branches,
                isLoading = uiState.isLoadingBranches,
                onExpand = onLoadBranches,
                onSelect = { selectedBranch = it },
            )
        }

        // Row 5: Client + Client phone
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "عميل",
                selectedOption = selectedClient,
                options = uiState.parties,
                isLoading = uiState.isLoadingParties,
                onExpand = onLoadParties,
                onSelect = { selectedClient = it },
            )
            OutlinedTextField(
                value = clientPhone,
                onValueChange = { clientPhone = it },
                placeholder = { Text("رقم جوال العميل", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )
        }

        // Row 6: Client number (half width)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = clientNumber,
                onValueChange = { clientNumber = it },
                placeholder = { Text("رقم العميل", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(8.dp),
            ) { Text("إلغاء") }

            Button(
                onClick = {
                    onApply(
                        uiState.pendingFilter.copy(
                            courtId = selectedCourt?.id,
                            caseId = selectedCase?.id,
                            hearingTypeId = selectedHearingType?.id,
                            subHearingTypeId = selectedSubHearingType?.id,
                            judgeName = judgeName.ifBlank { null },
                            assignedUserId = selectedEmployee?.id,
                            dashboardPeriodType = selectedPeriod?.id?.toString(),
                            branchId = selectedBranch?.id,
                            clientId = selectedClient?.id,
                            clientMobile = clientPhone.ifBlank { null },
                            clientNumber = clientNumber.ifBlank { null },
                        )
                    )
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) { Text("بحث", fontWeight = FontWeight.Bold) }
        }
    }
}

// ─── Reusable lazy dropdown field ─────────────────────────────────────────────

@Composable
private fun FilterDropdownField(
    modifier: Modifier = Modifier,
    label: String,
    selectedOption: FilterOption?,
    options: List<FilterOption>,
    isLoading: Boolean,
    onExpand: () -> Unit,
    onSelect: (FilterOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption?.name ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(label, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Primary)
                } else {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            expanded = true
                            onExpand()
                        },
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
        )

        DropdownMenu(
            expanded = expanded && options.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .heightIn(max = 200.dp),
        ) {
            if (selectedOption != null) {
                DropdownMenuItem(
                    text = { Text("الكل", style = MaterialTheme.typography.bodyMedium) },
                    onClick = { onSelect(FilterOption("", "")); expanded = false },
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
                            Text(option.name, style = MaterialTheme.typography.bodyMedium)
                            if (selectedOption?.id == option.id)
                                Text("✓", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}

// ─── Status chip (matching iOS design) ───────────────────────────────────────

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
            .background(
                if (isSelected) Primary else MaterialTheme.colorScheme.surface
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Colored left bar
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(36.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

// ─── Session card ─────────────────────────────────────────────────────────────

@Composable
fun SessionCard(session: Session, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
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
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
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
    1 -> Color(0xFF37B958)
    2 -> Color(0xFF307893)
    3 -> Color(0xFFA50900)
    else -> Primary
}