package com.example.ui.dialogs

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.AppointmentType

@Composable
fun AddAppointmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        clientName: String,
        dateTime: Long,
        durationMinutes: Int,
        locationOrLink: String,
        type: AppointmentType,
        dealValue: Double,
        notes: String,
        activity: Activity?
    ) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var durationMinutesStr by remember { mutableStateOf("45") }
    var locationOrLink by remember { mutableStateOf("") }
    var dealValueStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var appointmentType by remember { mutableStateOf(AppointmentType.VIDEO_CALL) }
    var isTitleError by remember { mutableStateOf(false) }
    var isClientError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "جدولة موعد / اجتماع عمل",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إلغاء")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isTitleError = false
                    },
                    label = { Text("موضوع الاجتماع *") },
                    placeholder = { Text("مثال: توقيع الصفقة، جلسة استشارية") },
                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                    isError = isTitleError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("appointment_title_input")
                )

                OutlinedTextField(
                    value = clientName,
                    onValueChange = {
                        clientName = it
                        if (it.isNotBlank()) isClientError = false
                    },
                    label = { Text("اسم العميل / الجهة *") },
                    placeholder = { Text("مثال: م. فهد السالم / شركة النماء") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    isError = isClientError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("appointment_client_input")
                )

                Text(
                    text = "طبيعة الاجتماع:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppointmentType.values().forEach { t ->
                        FilterChip(
                            selected = appointmentType == t,
                            onClick = { appointmentType = t },
                            label = { Text(t.titleAr) }
                        )
                    }
                }

                OutlinedTextField(
                    value = locationOrLink,
                    onValueChange = { locationOrLink = it },
                    label = { Text("الموقع أو رابط اللقاء") },
                    placeholder = { Text("رابط Google Meet أو العنوان") },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = durationMinutesStr,
                        onValueChange = { durationMinutesStr = it.filter { c -> c.isDigit() } },
                        label = { Text("المدة (دقيقة)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = dealValueStr,
                        onValueChange = { dealValueStr = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("قيمة الصفقة (ر.س)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.4f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("جدول الأعمال والملاحظات") },
                    placeholder = { Text("النقاط الرئيسية للمناقشة...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) isTitleError = true
                    if (clientName.isBlank()) isClientError = true

                    if (title.isNotBlank() && clientName.isNotBlank()) {
                        val duration = durationMinutesStr.toIntOrNull() ?: 45
                        val dealValue = dealValueStr.toDoubleOrNull() ?: 0.0
                        val activity = context as? Activity
                        // Schedule for upcoming time (2 hours from now as default quick schedule)
                        val appointmentTime = System.currentTimeMillis() + (3600000L * 2)
                        onConfirm(
                            title.trim(),
                            clientName.trim(),
                            appointmentTime,
                            duration,
                            locationOrLink.trim(),
                            appointmentType,
                            dealValue,
                            notes.trim(),
                            activity
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_add_appointment_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("تأكيد وحفظ الموعد")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
