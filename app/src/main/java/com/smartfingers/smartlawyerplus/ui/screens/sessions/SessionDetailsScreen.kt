package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.domain.model.HearingDetails
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    session: Session,
    onBack: () -> Unit,
    onNavigateToAddReport: (hearingId: Int, reportId: Int?) -> Unit = { _, _ -> },
    viewModel: SessionDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(session.id) { viewModel.init(session) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "تفاصيل الجلسة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    var showMenu by remember { mutableStateOf(false) }
                    val reportId = uiState.hearingDetails?.hearingReportId
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .padding(16.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (reportId != null) "تعديل التقرير" else "إضافة تقرير",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onNavigateToAddReport(session.id, reportId)
                                },
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Back",
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
                .background(MaterialTheme.colorScheme.background),
        ) {
            // ── Tab bar ───────────────────────────────────────────────────────
            val tabs = listOf("الإجراءات والمستندات", "بيانات الجلسة")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                tabs.forEachIndexed { idx, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (uiState.selectedTab == idx) Primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.onTabSelected(idx) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (uiState.selectedTab == idx) Color.White
                            else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (uiState.selectedTab == idx) FontWeight.SemiBold
                            else FontWeight.Normal,
                        )
                    }
                }
            }

            // ── Content ───────────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                uiState.error.isNotEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                uiState.error,
                                color = ColorError,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = viewModel::refresh) { Text("إعادة المحاولة") }
                        }
                    }
                }

                else -> {
                    when (uiState.selectedTab) {
                        0 -> SessionDocsTab(details = uiState.hearingDetails)
                        1 -> SessionDataTab(
                            details = uiState.hearingDetails,
                            session = session,
                        )
                    }
                }
            }
        }
    }
}

// ── Tab 0: Session Data ────────────────────────────────────────────────────────

@Composable
private fun SessionDataTab(details: HearingDetails?, session: Session) {
    val items = if (details != null) {
        buildList {
            add(Triple("القضية", details.caseName ?: "-", true)) // full width
            add(Triple("المكلف بالجلسة", details.assignedUsers.firstOrNull() ?: "-", false))
            add(Triple("الحالة", details.statusName ?: statusName(details.status), false))
            add(Triple("نوع الجلسة", details.hearingTypeName ?: "-", false))
            add(Triple("رقم الجلسة", "${details.hearingNumber ?: "-"}", false))
            add(Triple("الدائرة", details.courtCircle ?: "-", false))
            add(Triple("نوع الجلسة الفرعي", details.subHearingTypeName ?: "-", false))
            add(Triple("إلي تاريخ الجلسة", details.endDate?.take(10) ?: "-", false))
            add(
                Triple(
                    "تاريخ الجلسة",
                    buildString {
                        details.startDate?.let { append(it.take(10)) }
                        details.startDateHijri?.let { append("\n$it") }
                    }.ifBlank { "-" },
                    false,
                )
            )
            add(Triple("المحكمة", details.courtName ?: "-", false))
            add(Triple("اسم القاضي", details.judgeName ?: "-", false))
            add(Triple("تاريخ الإنشاء", details.createdOn?.take(10) ?: "-", false))
            add(Triple("بريد الدائرة", details.judgeOfficeNumber ?: "-", false))
            add(Triple("إضافة بواسطة", details.createdBy ?: "-", false))
            add(Triple("تاريخ آخر تعديل", details.updatedOn?.take(10) ?: "-", false))
        }
    } else {
        buildList {
            add(Triple("القضية", session.caseName ?: "-", true))
            add(Triple("الحالة", statusName(session.status), false))
            add(Triple("المكلف بالجلسة", session.assignedUsers.firstOrNull() ?: "-", false))
            add(Triple("رقم الجلسة", "${session.hearingNumber ?: "-"}", false))
            add(Triple("نوع الجلسة", session.hearingTypeName ?: "-", false))
            add(Triple("نوع الجلسة الفرعي", session.subHearingTypeName ?: "-", false))
            add(Triple("المحكمة", session.courtName ?: "-", false))
            add(
                Triple(
                    "تاريخ الجلسة",
                    buildString {
                        session.startDate?.let { append(it.take(10)) }
                        session.startDateHijri?.let { append("\n$it") }
                    }.ifBlank { "-" },
                    false,
                )
            )
            add(Triple("تاريخ الانتهاء", session.endDate?.take(10) ?: "-", false))
            add(Triple("الأيام المتبقية", "${session.remainingDays ?: 0} يوم", false))
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.filter { it.third }.forEach { (key, value, _) ->
            item {
                SessionDetailCard(
                    key = key,
                    value = value,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        // Two-column grid for rest
        val halfItems = items.filter { !it.third }
        val rows = halfItems.chunked(2)
        items(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (k, v, _) ->
                    SessionDetailCard(k, v, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SessionDetailCard(key: String, value: String, modifier: Modifier) {
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
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

// ── Tab 1: Docs & Procedures ───────────────────────────────────────────────────

@Composable
private fun SessionDocsTab(details: HearingDetails?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Actions required card
        SessionInfoCard(
            title = "الإجراءات المطلوبة في الجلسة",
            content = if (details?.hearingDescs?.isNotEmpty() == true)
                details.hearingDescs.joinToString("\n• ", prefix = "• ")
            else details?.hearingDesc ?: "-",
        )

        // Docs required card
        SessionInfoCard(
            title = "المستندات المطلوبة في الجلسة",
            content = details?.requiredDocs ?: "-",
        )

        // Attachments
        if (details?.attachments?.isNotEmpty() == true) {
            Text(
                text = "المرفقات",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            details.attachments.forEach { att ->
                HearingAttachmentCard(attachment = att)
            }
        } else {
            Text(
                text = "لا توجد مرفقات",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SessionInfoCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = MaterialTheme.colorScheme.onSurface
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = content,
                modifier = Modifier.padding(vertical =  16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun HearingAttachmentCard(attachment: com.smartfingers.smartlawyerplus.domain.model.HearingAttachment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.name ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (attachment.isApproved) ColorSuccess
                    else MaterialTheme.colorScheme.onSurface,
                )
                attachment.createdBy?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                }
                attachment.createdOn?.let {
                    Text(
                        text = it.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                }
            }
            if (attachment.isApproved) {
                Text(
                    text = "معتمد",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorSuccess,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ColorSuccess.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

private fun statusName(status: Int?) = when (status) {
    1 -> "في انتظار الجلسة"
    2 -> "قديمة"
    3 -> "مغلقة"
    else -> "-"
}