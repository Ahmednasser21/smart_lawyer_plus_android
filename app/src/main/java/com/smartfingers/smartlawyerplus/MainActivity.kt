package com.smartfingers.smartlawyerplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.smartfingers.smartlawyerplus.ui.navigation.AppNavGraph
import com.smartfingers.smartlawyerplus.ui.navigation.NavRoutes
import com.smartfingers.smartlawyerplus.ui.theme.SmartLawyerPlusTheme
import com.smartfingers.smartlawyerplus.util.AppErrorState
import com.smartfingers.smartlawyerplus.util.SessionExpiredManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionExpiredManager: SessionExpiredManager

    @Inject
    lateinit var appErrorState: AppErrorState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartLawyerPlusTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    sessionExpiredManager.sessionExpired.collect {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState.showSnackbar(
                            message = "انتهت الجلسة، يرجى تسجيل الدخول مجدداً",
                            actionLabel = "تسجيل الدخول",
                        )
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    appErrorState.error.collect { message ->
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(message = message)
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavGraph(navController = navController)

                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .systemBarsPadding()
                                .padding(top = 8.dp),
                        ) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = Color(0xFFB71C1C),
                                contentColor = Color.White,
                                actionColor = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}