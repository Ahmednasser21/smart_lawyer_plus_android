package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    onSessionClick: (Session) -> Unit = {},
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
                        SessionCard(
                            session = session,
                            onClick = {
                                onSessionClick(session)
                            })
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
                onLoadResultCounts = viewModel::loadResultCountsIfNeeded,
                onLoadDiscounts = viewModel::loadDiscountsIfNeeded,
                onApply = viewModel::applyFilter,
                onCancel = viewModel::dismissFilterSheet,
            )
        }
    }
}

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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Divider, RoundedCornerShape(8.dp))
                    .clickable { showPeriodMenu = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .size(width = 88.dp, height = 42.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = if (showPeriodMenu) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,

                    ) {
                    Text(
                        text = "الفترة",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp,
                    )
                    Text(
                        text = selectedPeriod?.name ?: "الكل",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                }
            }

            DropdownMenu(
                expanded = showPeriodMenu,
                onDismissRequest = { showPeriodMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            ) {
                periods.forEach { period ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (selectedPeriod?.id == period.id) {
                                    Text(
                                        "✓",
                                        color = Primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(end = 8.dp),
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(24.dp))
                                }
                                Text(
                                    text = period.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                )
                            }
                        },
                        onClick = { onPeriodSelected(period); showPeriodMenu = false },
                    )
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(start = 4.dp),
        ) {
            items(statuses) { status ->
                SessionStatusChip(
                    label = status.name,
                    isSelected = status.isSelected,
                    accentColor = hearingStatusColor(status.id),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { onStatusSelected(status) },
                )
            }
        }
    }
}

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
    onLoadResultCounts: () -> Unit,
    onLoadDiscounts: () -> Unit,
    onApply: (HearingFilter) -> Unit,
    onCancel: () -> Unit,
) {
    var selectedResultCount by remember { mutableStateOf<FilterOption?>(null) }
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
    var selectedDiscount by remember { mutableStateOf<FilterOption?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "تصنيف الجلسات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "المحكمة",
                selectedOption = selectedCourt,
                options = uiState.courts,
                isLoading = uiState.isLoadingCourts,
                onExpand = onLoadCourts,
                onSelect = { selectedCourt = if (it.id.isBlank()) null else it },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "عدد النتائج",
                selectedOption = selectedResultCount,
                options = uiState.resultCounts,
                isLoading = uiState.isLoadingResultCounts,
                onExpand = onLoadResultCounts,
                onSelect = { selectedResultCount = if (it.id.isBlank()) null else it },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "القضية",
                selectedOption = selectedCase,
                options = uiState.cases,
                isLoading = uiState.isLoadingCases,
                onExpand = onLoadCases,
                onSelect = { selectedCase = if (it.id.isBlank()) null else it },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "نوع الجلسة",
                selectedOption = selectedHearingType,
                options = uiState.hearingTypes,
                isLoading = uiState.isLoadingHearingTypes,
                onExpand = onLoadHearingTypes,
                onSelect = { selectedHearingType = if (it.id.isBlank()) null else it },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "نوع الجلسة الفرعي",
                selectedOption = selectedSubHearingType,
                options = uiState.subHearingTypes,
                isLoading = uiState.isLoadingSubHearingTypes,
                onExpand = onLoadSubHearingTypes,
                onSelect = { selectedSubHearingType = if (it.id.isBlank()) null else it },
            )
            OutlinedTextField(
                value = judgeName,
                onValueChange = { judgeName = it },
                placeholder = {
                    Text(
                        "اسم القاضي",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val periodOptions = uiState.periods.map { FilterOption(it.id.toString(), it.name) }
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "الفترة",
                selectedOption = selectedPeriod?.let { FilterOption(it.id.toString(), it.name) },
                options = periodOptions,
                isLoading = false,
                onExpand = {},
                onSelect = { opt ->
                    selectedPeriod = if (opt.id.isBlank()) null
                    else uiState.periods.firstOrNull { it.id.toString() == opt.id }
                },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "المكلف بالجلسة",
                selectedOption = selectedEmployee,
                options = uiState.employees,
                isLoading = uiState.isLoadingEmployees,
                onExpand = onLoadEmployees,
                onSelect = { selectedEmployee = if (it.id.isBlank()) null else it },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "الفرع",
                selectedOption = selectedBranch,
                options = uiState.branches,
                isLoading = uiState.isLoadingBranches,
                onExpand = onLoadBranches,
                onSelect = { selectedBranch = if (it.id.isBlank()) null else it },
            )
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "عميل",
                selectedOption = selectedClient,
                options = uiState.parties,
                isLoading = uiState.isLoadingParties,
                onExpand = onLoadParties,
                onSelect = { selectedClient = if (it.id.isBlank()) null else it },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = clientNumber,
                onValueChange = { clientNumber = it },
                placeholder = {
                    Text(
                        "رقم العميل",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )
            OutlinedTextField(
                value = clientPhone,
                onValueChange = { clientPhone = it },
                placeholder = {
                    Text(
                        "رقم جوال العميل",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Divider,
                ),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            FilterDropdownField(
                modifier = Modifier.weight(1f),
                label = "خصم",
                selectedOption = selectedDiscount,
                options = uiState.discounts,
                isLoading = uiState.isLoadingDiscounts,
                onExpand = onLoadDiscounts,
                onSelect = { selectedDiscount = if (it.id.isBlank()) null else it },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
            ) { Text("إلغاء", style = MaterialTheme.typography.labelLarge) }

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
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text(
                    "بحث",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

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
            placeholder = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextSecondary,
                )
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Primary)
                        .clickable {
                            if (!expanded) {
                                onExpand()
                            }
                            expanded = !expanded
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                    } else {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!expanded) onExpand()
                    expanded = !expanded
                },
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
                .background(MaterialTheme.colorScheme.surface)
                .heightIn(max = 220.dp),
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Primary,
                        strokeWidth = 2.dp,
                    )
                }
            } else if (options.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "لا توجد بيانات",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    },
                    onClick = { expanded = false },
                )
            } else {
                if (selectedOption != null && selectedOption.id.isNotBlank()) {
                    DropdownMenuItem(
                        text = { Text("الكل", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onSelect(FilterOption("", ""))
                            expanded = false
                        },
                    )
                    HorizontalDivider(color = Divider)
                }
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    option.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                if (selectedOption?.id == option.id) {
                                    Text(
                                        "✓",
                                        color = Primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionStatusChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(44.dp)
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
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        )
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                ),
        )
    }
}


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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f),
            ) {

                session.hearingTypeName?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                session.hearingNumber?.let {
                    Text(
                        it.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                session.courtName?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val firstUser = session.assignedUsers.firstOrNull()
                    val userDisplay =
                        if (session.assignedUsers.size > 1) "${firstUser ?: ""} ..." else firstUser
                            ?: ""
                    if (userDisplay.isNotBlank()) {
                        Text(
                            userDisplay,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End,
                            modifier = Modifier.widthIn(max = 160.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icons8_avatar_100),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                session.caseName?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 160.dp)
                    )
                }

                val dateDisplay = buildString {
                    session.startTime?.let { append(it.take(5)); append(" / ") }
                    session.startDate?.let { append(it.take(10)) }
                    session.startDateHijri?.let { append(" / "); append(it) }
                }
                if (dateDisplay.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            dateDisplay,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.End
                        )
                        Icon(
                            Icons.Default.AvTimer,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
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