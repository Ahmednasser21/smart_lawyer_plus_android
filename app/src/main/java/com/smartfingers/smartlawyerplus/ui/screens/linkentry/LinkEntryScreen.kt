package com.smartfingers.smartlawyerplus.ui.screens.linkentry

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerTextField
import com.smartfingers.smartlawyerplus.ui.theme.TextSecondary

@Composable
fun LinkEntryScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: LinkEntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateToLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppLogo(modifier = Modifier.size(width = 200.dp, height = 120.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineSmall,
            color = TextSecondary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.enter_link_message),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        SmartLawyerTextField(
            value = uiState.link,
            onValueChange = viewModel::onLinkChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.link),
            leadingIcon = Icons.Default.Link,
            errorText = uiState.linkError,
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SmartLawyerTextField(
            value = uiState.code,
            onValueChange = viewModel::onCodeChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.code),
            leadingIcon = Icons.Default.Code,
            errorText = uiState.codeError,
            imeAction = ImeAction.Done,
            onImeAction = viewModel::submit,
        )

        if (uiState.generalError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        SmartLawyerButton(
            text = stringResource(R.string.add),
            onClick = viewModel::submit,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth(0.6f),
        )
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}