package com.smartfingers.smartlawyerplus.ui.screens.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsScreen(
    caseId: Int,
    onBack: () -> Unit,
    viewModel: CaseDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val tabs = listOf("بيانات القضية", "المستندات", "بيانات العملاء")

    LaunchedEffect(caseId) { viewModel.loadDetails(caseId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "تفاصيل القضية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CaseDetailsTab.entries.forEachIndexed { idx, tab ->
                    val label = tabs[idx]
                    val isSelected = state.selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.selectTab(tab) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }

            when (state.selectedTab) {
                CaseDetailsTab.DATA -> CaseDataContent(state, caseId)
                CaseDetailsTab.DOCS -> CaseDocsContent(state, caseId, viewModel)
                CaseDetailsTab.CLIENTS -> CaseClientsContent(state, caseId, viewModel)
            }
        }
    }
}

@Composable
private fun CaseDataContent(state: CaseDetailsUiState, caseId: Int) {
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }
    val d = state.details ?: return
    val detailItems = buildList {
        add("رقم القضية" to (d.caseNumberInSource ?: "-"))
        add("اسم القضية" to d.name)
        add("الحالة" to (d.statusName ?: "-"))
        add("درجة التقاضي" to (d.litigationTypeName ?: "-"))
        add("المُصلح" to (d.reformerName ?: "-"))
        add("الفرع" to (d.branch ?: "-"))
        add("الوضع القانوني" to (d.legalStatusName ?: "-"))
        add("مصدر القضية" to (d.caseSource ?: "-"))
        add("المحكمة" to (d.court ?: "-"))
        add("الدائرة" to (d.circleName ?: "-"))
        add("تاريخ البدء" to "${d.startDate?.take(10) ?: ""} / ${d.startDateHijri ?: ""}")
        add("عدد الجلسات" to "${d.hearingsCount}")
        add("الأيام المتبقية" to "${d.restDaysToCreateNextCase?.toInt() ?: 0} يوم")
        add("الفئة" to (d.caseCategory ?: "-"))
        add("النوع" to (d.caseKind ?: "-"))
        add("النوع الفرعي" to (d.caseSubKind ?: "-"))
    }
    val rows = detailItems.chunked(2)
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (k, v) ->
                    CaseDetailCard(k, v, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CaseDetailCard(key: String, value: String, modifier: Modifier) {
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
            Text(key, style = MaterialTheme.typography.labelSmall, color = Color.White, textAlign = TextAlign.Center)
        }
        Text(
            value, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CaseDocsContent(
    state: CaseDetailsUiState,
    caseId: Int,
    viewModel: CaseDetailsViewModel,
) {
    LaunchedEffect(caseId) { viewModel.loadAttachments(caseId, refresh = true) }

    if (state.isLoadingAttachments && state.attachments.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }
    if (state.attachments.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد مستندات", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(state.attachments) { att ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(att.name ?: "-", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text(att.createdOn?.take(10) ?: "", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Text(att.type ?: "", style = MaterialTheme.typography.labelSmall, color = Primary)
                }
            }
        }
    }
}

@Composable
private fun CaseClientsContent(
    state: CaseDetailsUiState,
    caseId: Int,
    viewModel: CaseDetailsViewModel,
) {
    LaunchedEffect(caseId) { viewModel.loadClients(caseId, refresh = true) }

    if (state.isLoadingClients && state.clients.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }
    if (state.clients.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا يوجد عملاء", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(state.clients) { client ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(client.legalStatusName ?: "", style = MaterialTheme.typography.labelSmall, color = Primary)
                        Text(client.name ?: "-", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(client.mobile ?: "", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text(client.identityValue ?: "", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}