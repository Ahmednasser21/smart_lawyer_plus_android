package com.smartfingers.smartlawyerplus.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smartfingers.smartlawyerplus.domain.model.AppointmentFormTemplate
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerButton
import com.smartfingers.smartlawyerplus.ui.components.SmartLawyerOutlinedButton
import com.smartfingers.smartlawyerplus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentFormTemplateScreen(
    existing: AppointmentFormTemplate? = null,   // non-null → edit mode
    onSave: (AppointmentFormTemplate) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var body by remember { mutableStateOf(existing?.body ?: "") }
    var nameError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = if (existing != null) "تعديل النموذج" else "إضافة نموذج",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        actions = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "رجوع",
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
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    // ── اسم النموذج ───────────────────────────────────────────
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = false },
                        label = {
                            Text(
                                "اسم النموذج",
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.End,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Divider,
                            errorBorderColor = ColorError,
                        ),
                    )
                    if (nameError) {
                        Text(
                            "اسم النموذج مطلوب",
                            color = ColorError,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }

                    // ── محتوى النموذج (body / HTML) ───────────────────────────
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = {
                            Text(
                                "محتوى النموذج",
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        maxLines = 12,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.End,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Divider,
                        ),
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmartLawyerButton(
                            text = "حفظ",
                            onClick = {
                                if (name.isBlank()) { nameError = true; return@SmartLawyerButton }
                                onSave(
                                    (existing ?: AppointmentFormTemplate()).copy(
                                        name = name.trim(),
                                        body = body.trim(),
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                        SmartLawyerOutlinedButton(
                            text = "إلغاء",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}