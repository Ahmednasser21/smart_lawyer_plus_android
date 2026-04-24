package com.smartfingers.smartlawyerplus.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    taskId: Int,
    onBack: () -> Unit,
    viewModel: TaskDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(taskId) { viewModel.load(taskId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "عرض مهمة",
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
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Tab row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TaskDetailsTab.entries.forEach { tab ->
                    val isSelected = state.selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                viewModel.selectTab(tab)
                                if (tab == TaskDetailsTab.RESPONSES && state.replies.isEmpty()) {
                                    viewModel.loadReplies(taskId)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (tab == TaskDetailsTab.DATA) "بيانات المهمة" else "التكاليف والردود",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }

            when (state.selectedTab) {
                TaskDetailsTab.DATA -> TaskDataTab(state)
                TaskDetailsTab.RESPONSES -> TaskResponsesTab(state)
            }
        }
    }
}

@Composable
private fun TaskDataTab(state: TaskDetailsUiState) {
    val details = state.details ?: return
    val projectInfo = state.projectInfo

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (projectInfo != null) {
            item {
                ProjectInfoRow(projectInfo)
            }
        }
        item {
            Text(
                text = "${details.createdBy?.name ?: ""} - المسؤولون",
                style = MaterialTheme.typography.labelMedium,
                color = Primary,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        item {
            TaskDetailGrid(details)
        }
    }
}

@Composable
private fun ProjectInfoRow(info: TaskProjectInfo) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        val items = listOf(
            "الجلسة القادمة" to (info.nextHearingDate?.take(10) ?: "-"),
            "المستندات" to "${info.caseDocumentCount}",
            "القضايا" to "${info.caseHearingCount}",
            "المواعيد" to "${info.caseAppointmentCount}",
            "المرفقات" to "${info.caseAttachmentCount}",
        )
        items(items) { (key, value) ->
            ProjectInfoCard(key, value)
        }
    }
}

@Composable
private fun ProjectInfoCard(key: String, value: String) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Divider, RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TaskDetailGrid(details: TaskDetails) {
    val items = buildList {
        add("رقم المهمة" to (details.taskNumber ?: "-"))
        add("نوع المهمة" to "رئيسية")
        add("عدد الردود" to "${details.replyApproveRequestsCount + details.replyReviewRequestsCount}")
        add("طلبات قيد المراجعة" to "${details.replyApproveRequestsCount}")
        add("تاريخ البدء" to (details.startDate?.take(10) ?: "-"))
        add("تاريخ الانتهاء" to (details.endDate?.take(10) ?: "-"))
    }
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (key, value) ->
                    DetailCard(key, value, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailCard(key: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Divider, RoundedCornerShape(8.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = key, style = MaterialTheme.typography.labelSmall,
                color = Color.White, textAlign = TextAlign.Center,
            )
        }
        Text(
            text = value, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TaskResponsesTab(state: TaskDetailsUiState) {
    if (state.isLoadingReplies) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }
    if (state.replies.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد ردود", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(state.replies) { reply ->
            ReplyCard(reply)
        }
    }
}

@Composable
private fun ReplyCard(reply: TaskReply) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ColorTask),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = reply.elapsedTime ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
            Text(
                text = reply.replyMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
            )
            // Notes
            reply.taskReplyNotes.forEach { note ->
                ReplyNoteCard(note)
            }
        }
    }
}

@Composable
private fun ReplyNoteCard(note: TaskReplyNote) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = note.createdByUser?.name ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = note.message ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = note.createdOn?.take(10) ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}