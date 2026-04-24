package com.smartfingers.smartlawyerplus.ui.screens.calendar

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.domain.model.CalendarDay
import com.smartfingers.smartlawyerplus.domain.model.CalendarEventType
import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.DayEvent
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import androidx.core.graphics.toColorInt

private val DAY_LABELS = listOf("أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {

        // ── Top Bar: back button right, title center ───────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "الأجندة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }

        // ── User info row: notification left, name+avatar right ───────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Notification bell (left)
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons8_notification_100),
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (uiState.userName.isNotBlank()) {
                    Text(
                        text = "المحامية: ${uiState.userName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                UserAvatarCalendar(
                    picture = uiState.userPicture,
                    name = uiState.userName,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Filter dropdowns row ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            IosItemTypeDropdown(
                modifier = Modifier.weight(1f),
                label = "نوع المعلومات",
                selectedType = uiState.selectedItemType,
                options = uiState.itemTypes,
                isLoading = uiState.isLoadingItemTypes,
                onSelect = { viewModel.onItemTypeSelected(it) },
            )

            IosEmployeeDropdown(
                modifier = Modifier.weight(1f),
                label = "إسم الموظف",
                selectedName = uiState.selectedEmployee?.name,
                options = uiState.employees,
                isLoading = uiState.isLoadingEmployees,
                onSelect = { index ->
                    viewModel.onEmployeeSelected(
                        if (index == null) null else uiState.employees.getOrNull(index)
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Calendar Card ─────────────────────────────────────────────────────
        if (uiState.months.isNotEmpty()) {
            val month = uiState.months.getOrNull(uiState.currentMonthIndex)
            if (month != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = MaterialTheme.colorScheme.onSurface,
                        )
                        .pointerInput(uiState.currentMonthIndex) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -40f && uiState.currentMonthIndex < uiState.months.lastIndex) {
                                    viewModel.onMonthChanged(uiState.currentMonthIndex + 1)
                                } else if (dragAmount > 40f && uiState.currentMonthIndex > 0) {
                                    viewModel.onMonthChanged(uiState.currentMonthIndex - 1)
                                }
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                ) {
                    Column {

                        // ── Toggle (م / ) LEFT  +  Month title CENTERED ────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            // Month title — absolutely centered in the row
                            Text(
                                text = month.monthTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center),
                            )

                            // Toggle buttons pinned to the LEFT
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Transparent)
                                    .padding(3.dp), // padding between buttons
                            ) {
                                // هـ Hijri — now on the LEFT
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (uiState.isHijri) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { viewModel.onCalendarTypeChanged(true) }
                                        .padding(horizontal = 18.dp, vertical = 7.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "هـ",
                                        color = if (uiState.isHijri) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (uiState.isHijri) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                    )
                                }

                                Spacer(modifier = Modifier.width(3.dp)) // gap between buttons

                                // م Gregorian — now on the RIGHT
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!uiState.isHijri) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { viewModel.onCalendarTypeChanged(false) }
                                        .padding(horizontal = 18.dp, vertical = 7.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "م",
                                        color = if (!uiState.isHijri) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (!uiState.isHijri) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }

                        // ── Day header: سبت | جمعة | ... | أحد ───────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                            ) {
                                DAY_LABELS.forEach { label ->
                                    Text(
                                        text = label,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // ── Days Grid ─────────────────────────────────────────
                        if (uiState.isLoadingEvents) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .height(((month.days.size / 7) * 55).dp),
                                contentPadding = PaddingValues(0.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                userScrollEnabled = false,
                            ) {
                                items(month.days) { day ->
                                    IosDayCell(
                                        day = day,
                                        onClick = { viewModel.onDaySelected(day) })
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Events list ───────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            if (uiState.selectedDayEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "لا توجد أحداث في هذا اليوم",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            } else {
                items(uiState.selectedDayEvents, key = { it.event.id }) { dayEvent ->
                    IosCalendarEventCard(dayEvent = dayEvent)
                }
            }
        }
    }
}

// ─────────────────────────────── User Avatar ──────────────────────────────────

@Composable
private fun UserAvatarCalendar(picture: String, name: String) {
    val bitmap = remember(picture) {
        if (picture.isNotBlank()) {
            runCatching {
                val bytes = android.util.Base64.decode(picture, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }.getOrNull()
        } else null
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "User",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "م",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}

// ─────────────────────────────── Day Cell ─────────────────────────────────────

@Composable
private fun IosDayCell(day: CalendarDay, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = when {
            day.isSelected -> MaterialTheme.colorScheme.primary
            !day.isWithinDisplayedMonth -> Color.Transparent
            else -> Color.Transparent // Let the shadow/surface handle non-selected days
        },
        animationSpec = tween(200),
        label = "dayBg",
    )
    val textColor = when {
        day.isSelected -> Color.White
        !day.isWithinDisplayedMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val shape = RoundedCornerShape(12.dp)
    val isVisible = day.isWithinDisplayedMonth

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                if (isVisible) Modifier.shadow(
                    elevation = 8.dp,
                    shape = shape,
                    spotColor = MaterialTheme.colorScheme.onSurface,
                ) else Modifier
            )
            .clip(shape)
            .background(
                if (day.isSelected) bgColor
                else if (isVisible) MaterialTheme.colorScheme.background
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = day.number.padStart(2, '0'),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            fontSize = 15.sp,
        )
        if (day.events.isNotEmpty() && day.isWithinDisplayedMonth) {
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                day.events.take(3).forEach { dayEvent ->
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (day.isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                else runCatching {
                                    Color((dayEvent.event.color ?: "#3AABD6").toColorInt())
                                }.getOrDefault(MaterialTheme.colorScheme.primary)
                            ),
                    )
                }
            }
        }
    }
}

// ─────────────────── Employee Dropdown (text only) ────────────────────────────

@Composable
private fun IosEmployeeDropdown(
    modifier: Modifier,
    label: String,
    selectedName: String?,
    options: List<FilterOption>,
    isLoading: Boolean,
    onSelect: (Int?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp,Color.Transparent , RoundedCornerShape(10.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = selectedName ?: label,
                style = MaterialTheme.typography.bodySmall,
                color = if (selectedName != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 13.sp,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        "الكل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                onClick = { onSelect(null); expanded = false },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            options.forEachIndexed { index, opt ->
                DropdownMenuItem(
                    text = {
                        Text(
                            opt.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    onClick = { onSelect(index); expanded = false },
                )
            }
        }
    }
}

// ──────────── Item Type Dropdown (with icons per type) ────────────────────────

@Composable
private fun IosItemTypeDropdown(
    modifier: Modifier,
    label: String,
    selectedType: CalendarItemType?,
    options: List<CalendarItemType>,
    isLoading: Boolean,
    onSelect: (CalendarItemType?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, Color.Transparent, RoundedCornerShape(10.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            if (selectedType != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = selectedType.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp,
                    )
                    CalendarTypeIcon(typeId = selectedType.id, size = 18)
                }
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 13.sp,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "الكل",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.tasks_app_svgrepo_com_4),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                onClick = { onSelect(null); expanded = false },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            options.forEach { type ->
                val isSelected = selectedType?.id == type.id
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (isSelected) {
                                Text(
                                    "✓",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Text(
                                text = type.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CalendarTypeIcon(typeId = type.id, size = 20)
                        }
                    },
                    onClick = { onSelect(type); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun CalendarTypeIcon(typeId: Int, size: Int) {
    val (resId, color) = when (typeId) {
        1 -> Pair(R.drawable.stockholm_icons___home___timer, Color(0xFF3AAD66))
        2 -> Pair(R.drawable.auction, Color(0xFF3AABD6))
        3 -> Pair(R.drawable.layer_3, Color(0xFFE4B24F))
        4 -> Pair(R.drawable.exclamation_circle_fill, Color(0xFFC5C4E8))
        5 -> Pair(R.drawable.person_fill_gear, Color(0xFF9C88D4))
        else -> Pair(R.drawable.tasks_app_svgrepo_com_4, MaterialTheme.colorScheme.onSurface)
    }
    Icon(
        painter = painterResource(resId),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(size.dp),
    )
}

// ──────────────────────────── Event Card ──────────────────────────────────────

@Composable
fun IosCalendarEventCard(dayEvent: DayEvent) {
    val event = dayEvent.event
    val typeColor = calendarEventTypeColor(dayEvent.eventType)
    val height = if (dayEvent.eventType == CalendarEventType.APPOINTMENT ||
        dayEvent.eventType == CalendarEventType.HEARING
    ) 100.dp else 72.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = MaterialTheme.colorScheme.onSurface,
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(height)
                    .background(
                        color = runCatching {
                            Color((event.color ?: "#3AABD6").toColorInt())
                        }.getOrDefault(typeColor),
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                    ),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = event.title ?: event.extendedProperties?.caseName ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                when (dayEvent.eventType) {
                    CalendarEventType.HEARING -> {
                        event.extendedProperties?.courtName?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.End
                            )
                        }
                        event.extendedProperties?.startTime?.let {
                            Text(
                                it.take(5),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    CalendarEventType.APPOINTMENT -> {
                        event.extendedProperties?.assignedUsers?.firstOrNull()?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.End
                            )
                        }
                        val dateStr = buildString {
                            event.extendedProperties?.startTime?.let { append(it.take(5)); append(" / ") }
                            event.extendedProperties?.startDate?.let { append(it.take(10)) }
                        }
                        if (dateStr.isNotBlank()) {
                            Text(
                                dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    else -> {
                        event.extendedProperties?.startDate?.let {
                            Text(
                                it.take(10),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(typeColor.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = calendarEventTypeName(dayEvent.eventType),
                    style = MaterialTheme.typography.labelSmall,
                    color = typeColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

// ──────────────────────────── Helpers ─────────────────────────────────────────

fun calendarEventTypeColor(type: CalendarEventType): Color = when (type) {
    CalendarEventType.APPOINTMENT -> Color(0xFF3AAD66)
    CalendarEventType.HEARING -> Color(0xFF3AABD6)
    CalendarEventType.TASK -> Color(0xFFE4B24F)
    CalendarEventType.CASE_APPEAL -> Color(0xFFC5C4E8)
    CalendarEventType.AGENCY -> Color(0xFF9C88D4)
    CalendarEventType.NA -> Color(0xFF636366)
}

fun calendarEventTypeName(type: CalendarEventType): String = when (type) {
    CalendarEventType.APPOINTMENT -> "موعد"
    CalendarEventType.HEARING -> "جلسة"
    CalendarEventType.TASK -> "مهمة"
    CalendarEventType.CASE_APPEAL -> "قضية"
    CalendarEventType.AGENCY -> "وكالة"
    CalendarEventType.NA -> "أخرى"
}