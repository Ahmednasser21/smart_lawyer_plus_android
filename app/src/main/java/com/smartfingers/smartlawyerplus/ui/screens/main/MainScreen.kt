package com.smartfingers.smartlawyerplus.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.ui.screens.tasks.TasksScreen
import com.smartfingers.smartlawyerplus.ui.theme.Primary
import com.smartfingers.smartlawyerplus.ui.theme.TextOnPrimary
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

enum class MainTab(val labelAr: String, val iconRes: Int) {
    TASKS("المهام", R.drawable.layer_3_2),
    SESSIONS("الجلسات", R.drawable.auction_2),
    APPOINTMENTS("المواعيد", R.drawable.icons8_consultation_100),
    CASES("القضايا", R.drawable.vector),
}

@Composable
fun MainScreen(
    onNotificationsClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(MainTab.TASKS) }
    var showFabMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
        ) {
            when (selectedTab) {
                MainTab.TASKS -> TasksScreen(
                    onNotificationsClick = onNotificationsClick,
                    onCalendarClick = onCalendarClick,
                    onTaskClick = {},
                )
                MainTab.SESSIONS -> PlaceholderTabScreen("الجلسات")
                MainTab.APPOINTMENTS -> PlaceholderTabScreen("المواعيد")
                MainTab.CASES -> PlaceholderTabScreen("القضايا")
            }
        }

        // FAB overlay dimmer
        AnimatedVisibility(
            visible = showFabMenu,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(bottom = 140.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                FabMenuItems(
                    onAddTask = { showFabMenu = false },
                    onAddSession = { showFabMenu = false },
                    onAddAppointment = { showFabMenu = false },
                )
            }
        }

        // Bottom Navigation Bar
        SmartLawyerBottomBar(
            selectedTab = selectedTab,
            showFabMenu = showFabMenu,
            onTabSelected = { tab ->
                selectedTab = tab
                showFabMenu = false
            },
            onFabClick = { showFabMenu = !showFabMenu },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun SmartLawyerBottomBar(
    selectedTab: MainTab,
    showFabMenu: Boolean,
    onTabSelected: (MainTab) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
    ) {
        // Bar background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            color = Primary,
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Left tabs: Tasks, Sessions
                listOf(MainTab.TASKS, MainTab.SESSIONS).forEach { tab ->
                    BottomNavItem(
                        tab = tab,
                        isSelected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }

                // Center spacer for FAB
                Spacer(modifier = Modifier.weight(1f))

                // Right tabs: Appointments, Cases
                listOf(MainTab.APPOINTMENTS, MainTab.CASES).forEach { tab ->
                    BottomNavItem(
                        tab = tab,
                        isSelected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Center FAB
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = Primary,
            contentColor = TextOnPrimary,
            shape = CircleShape,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
                .shadow(8.dp, CircleShape),
        ) {
            Icon(
                painter = painterResource(
                    if (showFabMenu) R.drawable.ic_close else R.drawable.icon_ionic_ios_add
                ),
                contentDescription = "Add",
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha = if (isSelected) 1f else 0.6f

    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(contentColor = TextOnPrimary),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = tab.labelAr,
                modifier = Modifier.size(22.dp),
                tint = TextOnPrimary.copy(alpha = alpha),
            )
            Text(
                text = tab.labelAr,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = TextOnPrimary.copy(alpha = alpha),
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(TextOnPrimary),
                )
            }
        }
    }
}

@Composable
private fun FabMenuItems(
    onAddTask: () -> Unit,
    onAddSession: () -> Unit,
    onAddAppointment: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        listOf(
            "جلسة" to onAddSession,
            "إضافة مهمة" to onAddTask,
            "موعد" to onAddAppointment,
        ).forEach { (label, action) ->
            Button(
                onClick = action,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier
                    .width(140.dp)
                    .height(44.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            ) {
                Text(
                    text = label,
                    color = TextOnPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun PlaceholderTabScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextSecondary,
        )
    }
}