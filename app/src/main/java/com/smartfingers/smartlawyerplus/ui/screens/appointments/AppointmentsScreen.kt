package com.smartfingers.smartlawyerplus.ui.screens.appointments

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.smartfingers.smartlawyerplus.domain.model.AppointmentListItem
import com.smartfingers.smartlawyerplus.ui.theme.ColorSuccess
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onAppointmentClick: (Int) -> Unit = {},
    viewModel: AppointmentsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= uiState.appointments.size - 3 && uiState.hasMore && !uiState.isLoadingMore
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
        AppointmentsFilterTabRow(
            tabs = uiState.filterTabs,
            onTabSelected = viewModel::selectTab,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.appointments.isEmpty() && !uiState.isLoading) {
                EmptyAppointmentsState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.appointments.distinctBy { it.id }, key = { it.id }) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onClick = { onAppointmentClick(appointment.id) },
                        )
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

@Composable
private fun AppointmentsFilterTabRow(
    tabs: List<AppointmentFilterTab>,
    onTabSelected: (AppointmentFilterTab) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
        contentPadding = PaddingValues(end = 4.dp),
    ) {
        items(tabs) { tab ->
            AppointmentTabChip(
                label = tab.name,
                isSelected = tab.isSelected,
                isFinished = tab.isFinished,
                modifier = Modifier.padding(start = 16.dp),
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun AppointmentTabChip(
    label: String,
    isSelected: Boolean,
    isFinished: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val accentColor = if (isFinished) ColorSuccess else Primary

    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Primary else MaterialTheme.colorScheme.surface,
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
                .height(44.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                ),
        )
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentListItem, onClick: () -> Unit) {
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
            // Left column: remaining time + finished badge
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.width(80.dp),
            ) {
                if (appointment.isFinished) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ColorSuccess,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "منتهي",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorSuccess,
                            fontSize = 10.sp,
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AvTimer,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "${appointment.remainingDays} يوم",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp,
                        )
                    }
                    if (appointment.remainingHours > 0) {
                        Text(
                            text = "${appointment.remainingHours} ساعة",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 10.sp,
                        )
                    }
                }

                // Type name badge
                appointment.typeName?.let { type ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Primary.copy(alpha = 0.12f))
                            .padding(horizontal = 5.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right column: subject + users + date
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        // Subject
                        appointment.subject?.let { subject ->
                            Text(
                                text = subject,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.End,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 170.dp),
                            )
                        }

                        // Assigned users
                        val firstUser = appointment.assignedUsers.firstOrNull()
                        val userDisplay = if (appointment.assignedUsers.size > 1)
                            "${firstUser ?: ""} ..."
                        else firstUser ?: ""

                        if (userDisplay.isNotBlank()) {
                            Text(
                                text = userDisplay,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 170.dp),
                            )
                        }

                        // Case name
                        appointment.caseName?.let { caseName ->
                            Text(
                                text = caseName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 170.dp),
                            )
                        }
                    }

                    // Avatar
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
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                // Date + time row
                val dateDisplay = buildString {
                    appointment.startTime?.let { append(it.take(5)); append(" / ") }
                    appointment.startDate?.let { append(it.take(10)) }
                    appointment.startDateHijri?.let { append(" / "); append(it) }
                }
                if (dateDisplay.isNotBlank()) {
                    Text(
                        text = dateDisplay,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

        // Parties strip (clients) at the bottom if present
        if (appointment.parties.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Divider),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val displayParties = appointment.parties.take(2).joinToString(" , ")
                val suffix = if (appointment.parties.size > 2) " ..." else ""
                Text(
                    text = displayParties + suffix,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyAppointmentsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "لا توجد مواعيد",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}