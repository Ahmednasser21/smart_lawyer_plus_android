package com.smartfingers.smartlawyerplus.ui.screens.tasks

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.domain.model.Task
import com.smartfingers.smartlawyerplus.domain.model.TaskFilter
import com.smartfingers.smartlawyerplus.ui.theme.ColorSuccess
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.Secondary
import com.smartfingers.smartlawyerplus.ui.theme.TextOnPrimary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNotificationsClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onTaskClick: (Int) -> Unit = {},
    viewModel: TasksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Trigger load more when near the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= uiState.tasks.size - 2 && uiState.hasMore && !uiState.isLoadingMore
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────
        TasksTopBar(
            userName = uiState.userName,
            onNotificationsClick = onNotificationsClick,
            onCalendarClick = onCalendarClick,
        )

        // ── Filter Row ────────────────────────────────────────────────────
        FilterRow(
            filters = uiState.filters,
            selectedScope = uiState.selectedScope,
            onFilterSelected = viewModel::selectFilter,
            onScopeSelected = viewModel::selectScope,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── List ──────────────────────────────────────────────────────────
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.tasks.isEmpty() && !uiState.isLoading) {
                EmptyTasksState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskCard(task = task, onClick = { onTaskClick(task.id) })
                    }
                    if (uiState.isLoadingMore) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Primary, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun TasksTopBar(
    userName: String,
    onNotificationsClick: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Notification + Calendar buttons (left side for RTL)
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary),
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = TextOnPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary),
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextOnPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = if (userName.isNotBlank()) "المحامية: $userName" else "Smart Lawyer",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "M",
                color = TextOnPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ── Filter Row ────────────────────────────────────────────────────────────────

@Composable
private fun FilterRow(
    filters: List<TaskFilter>,
    selectedScope: TaskScope,
    onFilterSelected: (TaskFilter) -> Unit,
    onScopeSelected: (TaskScope) -> Unit,
) {
    var showScopeMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Scope dropdown chip (المهام + arrow)
        Box {
            FilterChipItem(
                label = when (selectedScope) {
                    TaskScope.RESPONSIBLE -> "مهام مسؤول عنها"
                    TaskScope.ALL -> "كل المهام"
                    TaskScope.CREATED -> "مهام أنشأتها"
                },
                isSelected = false,
                isDropdown = true,
                accentColor = Divider,
                textColor = MaterialTheme.colorScheme.onBackground,
                onClick = { showScopeMenu = true },
            )
            DropdownMenu(expanded = showScopeMenu, onDismissRequest = { showScopeMenu = false }) {
                TaskScope.entries.forEach { scope ->
                    DropdownMenuItem(
                        text = { Text(when (scope) {
                            TaskScope.RESPONSIBLE -> "مهام مسؤول عنها"
                            TaskScope.ALL -> "كل المهام"
                            TaskScope.CREATED -> "مهام أنشأتها"
                        }) },
                        onClick = { onScopeSelected(scope); showScopeMenu = false },
                    )
                }
            }
        }

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                FilterChipItem(
                    label = filter.name,
                    isSelected = filter.isSelected,
                    accentColor = when (filter.id) {
                        1 -> ColorSuccess
                        4 -> Primary
                        else -> Secondary
                    },
                    onClick = { onFilterSelected(filter) },
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    accentColor: Color = Primary,
    textColor: Color = if (isSelected) TextOnPrimary else MaterialTheme.colorScheme.onBackground,
    isDropdown: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextOnPrimary.copy(alpha = 0.6f)),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        )
        if (isDropdown) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
        }
    }
}

// ── Task Card ─────────────────────────────────────────────────────────────────

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    val pendingCount = task.taskReplyApproveRequestsCount + task.taskReplyReviewRequestsCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: status color bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(taskStatusColor(task.taskStatus)),
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Middle: task info
            Column(modifier = Modifier.weight(1f)) {
                // Top row: priority | pending badge | task name (-)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.priorityName ?: "عادي",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                    )
                    if (pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        PendingBadge(count = pendingCount)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = task.name.ifBlank { "-" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom row: status | remaining days
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.statusName ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = taskStatusColor(task.taskStatus),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${task.remainingDays}- يوم",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: employee avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons8_avatar_100),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun PendingBadge(count: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Primary)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Secondary)
                .padding(horizontal = 4.dp, vertical = 1.dp),
        ) {
            Text(text = "$count", style = MaterialTheme.typography.labelSmall, color = TextOnPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "طلبات للمراجعة", style = MaterialTheme.typography.labelSmall, color = TextOnPrimary)
    }
}

@Composable
private fun EmptyTasksState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "لا توجد مهام", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

private fun taskStatusColor(status: Int?): Color = when (status) {
    3 -> ColorSuccess          // active
    5 -> Color(0xFFF44336)     // finished/closed
    4 -> Color(0xFF9E9E9E)     // archived
    else -> Color(0xFF1A1A1A)  // default
}