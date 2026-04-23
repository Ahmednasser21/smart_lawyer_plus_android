package com.smartfingers.smartlawyerplus.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    onBack: () -> Unit,
    viewModel: AppointmentDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(appointmentId) { viewModel.loadDetails(appointmentId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "تفاصيل الموعد",
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
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            state.details != null -> {
                val d = state.details!!
                val detailItems = buildList {
                    add("المكلف" to (d.assignedUsers.firstOrNull() ?: "-"))
                    add("الحالة" to if (d.isFinished) "منتهي" else "مجدول")
                    add("طلب العميل" to (d.clientRequest ?: "-"))
                    add("المسؤولون" to d.assignedUsers.joinToString(", "))
                    add("الأطراف" to d.parties.joinToString(", "))
                    add("النوع" to (d.typeName ?: "-"))
                    add("تاريخ الموعد" to (d.startDate?.take(10) ?: "-"))
                    add("الوقت المتبقي" to "${d.remainingDays} يوم , ${d.remainingHours} ساعة")
                    add("تاريخ الإنشاء" to (d.createdOn?.take(10) ?: "-"))
                    add("تاريخ آخر تعديل" to (d.updatedOn?.take(10) ?: "-"))
                }
                val rows = detailItems.chunked(2)
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(rows) { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { (k, v) ->
                                ApptDetailCard(k, v, Modifier.weight(1f))
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            else -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(state.error.ifBlank { "حدث خطأ" }, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ApptDetailCard(key: String, value: String, modifier: Modifier) {
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