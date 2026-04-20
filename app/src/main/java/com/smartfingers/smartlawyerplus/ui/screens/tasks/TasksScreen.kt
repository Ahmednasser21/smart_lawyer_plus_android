package com.smartfingers.smartlawyerplus.ui.screens.tasks

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= uiState.tasks.size - 3 && uiState.hasMore && !uiState.isLoadingMore
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TasksTopBar(
            userName = uiState.userName,
            userPicture = uiState.userPicture,
            onNotificationsClick = onNotificationsClick,
            onCalendarClick = onCalendarClick,
        )

        FilterRow(
            filters = uiState.filters,
            selectedScope = uiState.selectedScope,
            onFilterSelected = viewModel::selectFilter,
            onScopeSelected = viewModel::selectScope,
        )

        Spacer(modifier = Modifier.height(8.dp))

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
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun TasksTopBar(
    userName: String,
    userPicture: String,
    onNotificationsClick: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        // Left: teal action buttons (notification + calendar)
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Primary),
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = TextOnPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Primary),
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "Calendar",
                tint = TextOnPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right: username + avatar
        Text(
            text = if (userName.isNotBlank()) "المحامية: $userName" else "Smart Lawyer",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.width(10.dp))

        UserAvatar(
            picture = userPicture,
            userName = userName,
            size = 38,
        )
    }
}

// ── User Avatar ───────────────────────────────────────────────────────────────

@Composable
private fun UserAvatar(
    picture: String,
    userName: String,
    size: Int,
) {
    val bitmap = remember(picture) {
        if (picture.isNotBlank()) {
            try {
                val bytes = android.util.Base64.decode(picture, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (_: Exception) { null }
        } else null
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Primary)
            .border(2.dp, Primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "User avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "U",
                color = TextOnPrimary,
                style = MaterialTheme.typography.titleSmall,
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
        // Scope dropdown chip
        Box {
            ScopeChip(
                label = when (selectedScope) {
                    TaskScope.RESPONSIBLE -> "مهام مسؤول عنها"
                    TaskScope.ALL        -> "كل المهام"
                    TaskScope.CREATED    -> "مهام أنشأتها"
                },
                onClick = { showScopeMenu = true },
            )
            DropdownMenu(
                expanded = showScopeMenu,
                onDismissRequest = { showScopeMenu = false },
            ) {
                TaskScope.entries.forEach { scope ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (scope) {
                                    TaskScope.RESPONSIBLE -> "مهام مسؤول عنها"
                                    TaskScope.ALL        -> "كل المهام"
                                    TaskScope.CREATED    -> "مهام أنشأتها"
                                }
                            )
                        },
                        onClick = { onScopeSelected(scope); showScopeMenu = false },
                    )
                }
            }
        }

        // Status filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                StatusFilterChip(
                    label = filter.name,
                    isSelected = filter.isSelected,
                    accentColor = when (filter.id) {
                        1    -> ColorSuccess   // مهام منجزة  → green
                        4    -> Primary        // بانتظار الاعتماد → teal
                        else -> Secondary      // fallback
                    },
                    onClick = { onFilterSelected(filter) },
                )
            }
        }
    }
}

// Scope dropdown chip — outlined, white bg, arrow icon
@Composable
private fun ScopeChip(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Divider, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// Status filter chip — when selected shows colored left bar inside chip
@Composable
private fun StatusFilterChip(
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
        // Colored left bar (visible always, accent when selected, gray when not)
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
            color = if (isSelected) MaterialTheme.colorScheme.onBackground
            else TextSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        )
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Employee avatar (right side in RTL = leading in LTR layout) ──
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF4FB)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons8_avatar_100),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))


            Text(
                text = task.name.ifBlank { "-" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.Start) {
                // Priority label
                Text(
                    text = task.priorityName ?: "عادي",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                )

                // Pending badge (if any)
                if (pendingCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    PendingBadge(count = pendingCount)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Status + remaining days
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.statusName ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = taskStatusColor(task.taskStatus),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${task.remainingDays}- يوم",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                }
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
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelSmall,
                color = TextOnPrimary,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "طلبات للمراجعة",
            style = MaterialTheme.typography.labelSmall,
            color = TextOnPrimary,
        )
    }
}

@Composable
private fun EmptyTasksState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "لا توجد مهام",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

private fun taskStatusColor(status: Int?): Color = when (status) {
    3    -> ColorSuccess
    5    -> Color(0xFFF44336)
    4    -> Color(0xFF9E9E9E)
    else -> Color(0xFF1A1A1A)
}