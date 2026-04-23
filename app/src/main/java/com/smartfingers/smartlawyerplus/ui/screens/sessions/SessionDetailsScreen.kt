package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    session: Session,
    onBack: () -> Unit,
) {
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
        val tabs = listOf("بيانات الجلسة", "الإجراءات والمستندات")
        var selectedTab by remember { mutableIntStateOf(0) }

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
                tabs.forEachIndexed { idx, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == idx) Primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickableNoRipple { selectedTab = idx },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedTab == idx) Color.White else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (selectedTab == idx) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> SessionDataContent(session)
                1 -> SessionDocsContent(session)
            }
        }
    }
}

@Composable
private fun SessionDataContent(session: Session) {
    val items = buildList {
        add("القضية" to (session.caseName ?: "-"))
        add("الحالة" to statusName(session.status))
        add("المكلف بالجلسة" to (session.assignedUsers.firstOrNull() ?: "-"))
        add("رقم الجلسة" to "${session.hearingNumber ?: "-"}")
        add("نوع الجلسة" to (session.hearingTypeName ?: "-"))
        add("نوع الجلسة الفرعي" to (session.subHearingTypeName ?: "-"))
        add("المحكمة" to (session.courtName ?: "-"))
        add("تاريخ الجلسة" to buildString {
            session.startDate?.let { append(it.take(10)) }
            session.startDateHijri?.let { append(" / $it") }
        })
        add("تاريخ الانتهاء" to (session.endDate?.take(10) ?: "-"))
        add("الأيام المتبقية" to "${session.remainingDays ?: 0} يوم")
    }
    val rows = items.chunked(2)
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (k, v) ->
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
private fun SessionDocsContent(session: Session) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("لا توجد مستندات", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

private fun statusName(status: Int?) = when (status) {
    1 -> "في انتظار الجلسة"
    2 -> "قديمة"
    3 -> "مغلقة"
    else -> "-"
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit) =
    this.then(Modifier.clickable(indication = null, interactionSource = null, onClick = onClick))