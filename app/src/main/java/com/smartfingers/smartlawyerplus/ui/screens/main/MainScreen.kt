package com.smartfingers.smartlawyerplus.ui.screens.main

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.ui.components.BottomNavCutoutShape
import com.smartfingers.smartlawyerplus.ui.navigation.NavRoutes
import com.smartfingers.smartlawyerplus.ui.screens.appointments.AppointmentsScreen
import com.smartfingers.smartlawyerplus.ui.screens.appointments.AppointmentsViewModel
import com.smartfingers.smartlawyerplus.ui.screens.cases.CasesScreen
import com.smartfingers.smartlawyerplus.ui.screens.cases.CasesViewModel
import com.smartfingers.smartlawyerplus.ui.screens.sessions.SessionsScreen
import com.smartfingers.smartlawyerplus.ui.screens.tasks.TasksScreen
import com.smartfingers.smartlawyerplus.ui.screens.tasks.TasksViewModel

enum class MainTab(val labelAr: String, val iconRes: Int) {
    TASKS("المهام", R.drawable.layer_3_2),
    SESSIONS("الجلسات", R.drawable.auction_2),
    APPOINTMENTS("المواعيد", R.drawable.icons8_consultation_100),
    CASES("القضايا", R.drawable.vector),
}

@Composable
fun MainScreen(
    navController: NavHostController,
    onNotificationsClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    viewModel: TasksViewModel = hiltViewModel(),
    casesViewModel: CasesViewModel = hiltViewModel(),
    appointmentsViewModel: AppointmentsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(MainTab.TASKS) }
    var showFabMenu by remember { mutableStateOf(false) }
    var sessionsFilterAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            SharedTopBar(
                userName = uiState.userName,
                userPicture = uiState.userPicture,
                onNotificationsClick = onNotificationsClick,
                onCalendarClick = onCalendarClick,
                selectedTab,
                sessionsFilterAction,
            )

            when (selectedTab) {
                MainTab.TASKS -> TasksScreen(
                    onTaskClick = { taskId ->
                        navController.navigate(
                            NavRoutes.TaskDetails.createRoute(
                                taskId
                            )
                        )
                    },
                    viewModel = viewModel,
                )

                MainTab.SESSIONS -> SessionsScreen(
                    onSessionClick = { session ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("session", session)
                        navController.navigate(NavRoutes.SessionDetails.createRoute(session.id))
                    },
                    onFilterIconReady = { action -> sessionsFilterAction = action },
                )

                MainTab.APPOINTMENTS -> AppointmentsScreen(
                    onAppointmentClick = { apptId ->
                        navController.navigate(
                            NavRoutes.AppointmentDetails.createRoute(
                                apptId
                            )
                        )
                    },
                    viewModel = appointmentsViewModel,
                )

                MainTab.CASES -> CasesScreen(
                    onCaseClick = { caseId ->
                        navController.navigate(
                            NavRoutes.CaseDetails.createRoute(
                                caseId
                            )
                        )
                    },
                    viewModel = casesViewModel,
                )
            }

        }

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
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                FabMenuItems(
                    onAddTask = {
                        showFabMenu = false; navController.navigate(NavRoutes.AddTask.route)
                    },
                    onAddSession = {
                        showFabMenu = false; navController.navigate(NavRoutes.AddSession.route)
                    },
                    onAddAppointment = {
                        showFabMenu =
                            false; navController.navigate(NavRoutes.AddAppointmentNav.route)
                    },
                )
            }
        }

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
private fun SharedTopBar(
    userName: String,
    userPicture: String,
    onNotificationsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    selectedTab: MainTab,
    sessionsFilterAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                painter = painterResource(R.drawable.icons8_notification_100),
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))


        if (selectedTab == MainTab.SESSIONS) {
            IconButton(
                onClick = { sessionsFilterAction?.invoke() },
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(
                    imageVector = Icons.Default.FormatAlignCenter,
                    contentDescription = "تصنيف الجلسات",
                    tint = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                painter = painterResource(R.drawable.gen014),
                contentDescription = "Calendar",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = userName.ifBlank { "Smart Lawyer" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.width(10.dp))

        UserAvatar(picture = userPicture, userName = userName, size = 38)
    }
}

@Composable
fun UserAvatar(
    picture: String,
    userName: String,
    size: Int,
) {
    val bitmap = remember(picture) {
        if (picture.isNotBlank()) {
            try {
                val bytes = android.util.Base64.decode(picture, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
        }
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
    val fabSize = 44.dp
    val barHeight = 64.dp
    val fabOverlap = 20.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(barHeight + fabOverlap),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter)
                .clip(BottomNavCutoutShape(fabRadius = 85f))
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf(MainTab.CASES, MainTab.APPOINTMENTS).forEach { tab ->
                    BottomNavItem(
                        tab = tab,
                        isSelected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.width(fabSize + 8.dp))

                listOf(MainTab.SESSIONS, MainTab.TASKS).forEach { tab ->
                    BottomNavItem(
                        tab = tab,
                        isSelected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .size(fabSize)
                .offset(y = (-6).dp)
                .align(Alignment.TopCenter)
        ) {
            Icon(
                painter = painterResource(
                    if (showFabMenu) R.drawable.ic_close else R.drawable.icon_ionic_ios_add
                ),
                contentDescription = "Add",
                modifier = Modifier.size(24.dp),
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
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.95f)
                    .fillMaxWidth(0.80f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight(),
        ) {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = tab.labelAr,
                modifier = Modifier.size(22.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.50f),
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = tab.labelAr,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.50f),
            )
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .width(140.dp)
                    .height(44.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}