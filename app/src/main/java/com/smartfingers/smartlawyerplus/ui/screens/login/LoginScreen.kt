package com.smartfingers.smartlawyerplus.ui.screens.login

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerTextField
import com.smartfingers.smartlawyerplus.ui.screens.linkentry.AppLogo
import com.smartfingers.smartlawyerplus.ui.theme.Primary

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val logoBase64 = uiState.logo
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (!logoBase64.isNullOrBlank()) {
                val bitmap = remember(logoBase64) {
                    try {
                        val bytes =
                            android.util.Base64.decode(logoBase64, android.util.Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .padding(24.dp)
                            .size(width = 240.dp, height = 140.dp),
                        contentScale = ContentScale.FillBounds,
                    )
                } else {
                    AppLogo(modifier = Modifier.size(width = 200.dp, height = 120.dp))
                }
            } else {
                AppLogo(modifier = Modifier.size(width = 200.dp, height = 120.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            SmartLawyerTextField(
                value = uiState.userName,
                onValueChange = viewModel::onUserNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.user_name),
                leadingIcon = Icons.Default.Person,
                errorText = uiState.userNameError,
                imeAction = ImeAction.Next,
            )

            Spacer(modifier = Modifier.height(30.dp))

            SmartLawyerTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.password),
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                errorText = uiState.passwordError,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = viewModel::login,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                TextButton(onClick = onNavigateToForgotPassword) {
                    Text(
                        text = stringResource(R.string.forget_password),
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            if (uiState.generalError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.generalError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SmartLawyerButton(
                    text = stringResource(R.string.login),
                    onClick = viewModel::login,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.weight(1f),
                )

                SmartLawyerOutlinedButton(
                    text = stringResource(R.string.register),
                    onClick = onNavigateToRegister,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        AppLogo(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 25.dp, bottom = 25.dp)
                .size(width = 150.dp, height = 90.dp),
        )
    }
}