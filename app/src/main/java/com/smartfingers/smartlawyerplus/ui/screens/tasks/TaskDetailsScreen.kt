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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.domain.model.TaskDetails
import com.smartfingers.smartlawyerplus.domain.model.TaskProjectInfo
import com.smartfingers.smartlawyerplus.domain.model.TaskReply
import com.smartfingers.smartlawyerplus.domain.model.TaskReplyNote
import com.smartfingers.smartlawyerplus.ui.theme.ColorTask
import com.smartfingers.smartlawyerplus.ui.theme.Divider
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    taskId: Int,
    onBack: () -> Unit,
    onNavigateToAddTask: () -> Unit = {},
    viewModel: TaskDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val navigateBack by viewModel.navigateBack.collectAsState()

    LaunchedEffect(taskId) { viewModel.load(taskId) }

    // Navigate back after successful delete
    LaunchedEffect(navigateBack) {
        if (navigateBack) {
            viewModel.onNavigateBackHandled()
            onBack()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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

            if (state.showDeleteReplyConfirm) {
                ConfirmDialog(
                    message = "هل أنت متأكد من إتمام عملية الحذف؟",
                    confirmText = "حذف",
                    onConfirm = { viewModel.confirmDeleteTask() },
                    onDismiss = { viewModel.dismissConfirm() },
                )
            }
            if (state.showCloseTaskConfirm) {
                ConfirmDialog(
                    message = "هل أنت متأكد من إغلاق المهمة؟",
                    confirmText = "إغلاق",
                    onConfirm = { viewModel.confirmCloseTask() },
                    onDismiss = { viewModel.dismissConfirm() },
                )
            }

            if (state.isLoading) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val dataSelected = state.selectedTab == TaskDetailsTab.DATA
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (dataSelected) Primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.selectTab(TaskDetailsTab.DATA) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "بيانات المهمة",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (dataSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (dataSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                    val responsesSelected = state.selectedTab == TaskDetailsTab.RESPONSES
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (responsesSelected) Primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                viewModel.selectTab(TaskDetailsTab.RESPONSES)
                                if (state.replies.isEmpty()) viewModel.loadReplies(taskId)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "التكاليف والردود",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (responsesSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (responsesSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }

                when (state.selectedTab) {
                    TaskDetailsTab.DATA -> TaskDataTab(state)
                    TaskDetailsTab.RESPONSES -> TaskResponsesTab(
                        state = state,
                        taskId = taskId,
                        onDeleteTask = { viewModel.showDeleteTaskConfirm(it) },
                        onCloseTask = { viewModel.showCloseTaskConfirm(it) },
                        onAddTask = onNavigateToAddTask,
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmDialog(
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تأكيد",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(confirmText, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("إلغاء")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(16.dp),
    )
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
            item { ProjectInfoRow(projectInfo) }
        }
        item {
            val managerLabel = buildString {
                append("القائمون على القضية:")
                if (!details.createdBy?.name.isNullOrBlank()) {
                    append(" \n     ${details.createdBy.name}")
                }
            }
            Text(
                text = managerLabel,
                style = MaterialTheme.typography.labelMedium,
                color = Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Start,
            )
        }

        item { TaskDetailGrid(details) }
    }
}

@Composable
private fun ProjectInfoRow(info: TaskProjectInfo) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        reverseLayout = true,
    ) {
        val items = listOf(
            "بيانات القضية" to (info.executiveCaseClients ?: info.dataCase ?: "-"),
            "مرفقات القضية" to "${info.caseAttachmentCount}",
            "المواعيد" to "${info.caseAppointmentCount}",
            "الجلسات" to "${info.caseHearingCount}",
            "استعارة وثائق" to "${info.caseDocumentCount}",
            "الجلسة القادمة" to (info.nextHearingDate?.take(10) ?: "-"),
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
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Divider, RoundedCornerShape(10.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                maxLines = 1,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
private fun TaskDetailGrid(details: TaskDetails) {
    val items = buildList {
        add("رقم المهمة" to (details.taskNumber ?: "-"))
        add("نوع المهمة" to "رئيسية")
        add("عدد الردود" to "${details.replyApproveRequestsCount + details.replyReviewRequestsCount}")
        add("عدد الطلبات قيد المراجعة" to "${details.replyApproveRequestsCount}")
        add("تاريخ بداية المهمة" to (details.startDate?.take(10) ?: "-"))
        add("تاريخ إنتهاء المهمة" to (details.endDate?.take(10) ?: "-"))
    }
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // In RTL the first item goes on the right
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
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Divider, RoundedCornerShape(10.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontSize = 11.sp,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TaskResponsesTab(
    state: TaskDetailsUiState,
    taskId: Int,
    onDeleteTask: (Int) -> Unit,
    onCloseTask: (Int) -> Unit,
    onAddTask: () -> Unit,
) {
    if (state.isLoadingReplies) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    val details = state.details

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (details != null) {
            item {
                UserReplyCard(
                    userName = details.createdBy?.name ?: "",
                    message = details.requestMessage ?: "",
                    taskId = taskId,
                    onDeleteTask = { onDeleteTask(taskId) },
                    onCloseTask = { onCloseTask(taskId) },
                    onAddTask = onAddTask,
                )
            }
        }

        items(state.replies) { reply ->
            ReplyCard(
                reply = reply,
                onDeleteTask = { onDeleteTask(taskId) },
                onCloseTask = { onCloseTask(taskId) },
                onAddTask = onAddTask,
            )
        }
    }
}

@Composable
private fun UserReplyCard(
    userName: String,
    message: String,
    taskId: Int,
    onDeleteTask: () -> Unit,
    onCloseTask: () -> Unit,
    onAddTask: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Avatar — no background, just the drawable
                    Icon(
                        painter = painterResource(R.drawable.icons8_avatar_100),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary),
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    ) {
                        DropdownMenuItem(text = { Text("طلب تمديد") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("طلب اعتذار") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("تغيير المكلف بالمهمة") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("تكرار المهمة") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("إنشاء مهمة فرعية") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("اقتراح مهمة قادمة") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("تعديل المهمة") }, onClick = { showMenu = false; onAddTask() })
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("إغلاق", color = MaterialTheme.colorScheme.error) },
                            onClick = { showMenu = false; onCloseTask() },
                        )
                        DropdownMenuItem(
                            text = { Text("حذف", color = MaterialTheme.colorScheme.error) },
                            onClick = { showMenu = false; onDeleteTask() },
                        )
                    }
                }
            }

            if (message.isNotBlank()) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

@Composable
private fun ReplyCard(
    reply: TaskReply,
    onDeleteTask: () -> Unit,
    onCloseTask: () -> Unit,
    onAddTask: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ColorTask.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Avatar — no background box, just the drawable
                    Icon(
                        painter = painterResource(R.drawable.icons8_avatar_100),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                        Text(text = reply.elapsedTime ?: "", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary),
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    ) {
                        if (reply.taskReplyFormTemplatesCount > 0) {
                            DropdownMenuItem(
                                text = { Text("النماذج (${reply.taskReplyFormTemplatesCount})") },
                                onClick = { showMenu = false },
                            )
                        }
                        if (reply.attachmentCount > 0) {
                            DropdownMenuItem(
                                text = { Text("المرفقات (${reply.attachmentCount})") },
                                onClick = { showMenu = false },
                            )
                        }
                        DropdownMenuItem(text = { Text("طلب اعتماد") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("طلب رأي") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("طلب تمديد") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("طلب اعتذار") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("رد بملاحظة") }, onClick = { showMenu = false; onAddTask() })
                        DropdownMenuItem(text = { Text("إضافة رد") }, onClick = { showMenu = false; onAddTask() })
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("إغلاق", color = MaterialTheme.colorScheme.error) },
                            onClick = { showMenu = false; onCloseTask() },
                        )
                        DropdownMenuItem(
                            text = { Text("حذف", color = MaterialTheme.colorScheme.error) },
                            onClick = { showMenu = false; onDeleteTask() },
                        )
                    }
                }
            }

            if (!reply.replyMessage.isNullOrBlank()) {
                Text(
                    text = reply.replyMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
            }

            reply.taskReplyNotes.forEach { note -> ReplyNoteCard(note) }
        }
    }
}

@Composable
private fun ReplyNoteCard(note: TaskReplyNote) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = note.createdByUser?.name ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Text(
                text = note.message ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Text(
                text = note.createdOn?.take(10) ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                textAlign = TextAlign.Start, // date on left in RTL = visually right
            )
        }
    }
}