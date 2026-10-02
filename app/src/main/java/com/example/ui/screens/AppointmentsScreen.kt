package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdBannerView
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.AppointmentType
import com.example.ui.BusinessViewModel
import com.example.ui.components.GlowFloatingCard
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentsScreen(viewModel: BusinessViewModel) {
    val context = LocalContext.current
    val allAppointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    var selectedFilterIndex by remember { mutableStateOf(0) }

    val now = System.currentTimeMillis()
    val filteredAppointments = when (selectedFilterIndex) {
        0 -> allAppointments.filter { it.dateTime >= now - 3600000L && it.status != AppointmentStatus.COMPLETED && it.status != AppointmentStatus.CANCELLED }
        1 -> allAppointments.filter { it.status == AppointmentStatus.COMPLETED }
        else -> allAppointments
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setAddAppointmentDialog(true) },
                containerColor = Color(0xFF00E5FF),
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_appointment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "جدولة موعد جديد")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("appointments_screen")
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "جدولة المواعيد واللقاءات",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "تنظيم لقاءات العملاء، جلسات التفاوض، وإغلاق الصفقات التجارية",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
            }

            // In-page AdMob Banner
            AdBannerView(modifier = Modifier.padding(vertical = 2.dp))

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("المواعيد القادمة", "المكتملة", "كافة المواعيد")
                filters.forEachIndexed { index, label ->
                    val isSelected = selectedFilterIndex == index
                    item {
                        SuggestionChip(
                            onClick = { selectedFilterIndex = index },
                            label = { Text(label, color = if (isSelected) Color.Black else Color.White) },
                            colors = if (isSelected) {
                                SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFF00E5FF)
                                )
                            } else SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1B2E24))
                        )
                    }
                }
            }

            // Appointments List
            if (filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF557766),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد مواعيد في هذا القسم",
                            color = Color(0xFFA5B8AD),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على زر الإضافة (+) لجدولة أول اجتماع لعملك",
                            color = Color(0xFF7E9488),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAppointments, key = { it.id }) { appointment ->
                        GlowingAppointmentCard(
                            appointment = appointment,
                            onStatusChange = { newStatus ->
                                viewModel.updateAppointmentStatus(appointment, newStatus)
                            },
                            onDelete = {
                                viewModel.deleteAppointment(appointment)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlowingAppointmentCard(
    appointment: Appointment,
    onStatusChange: (AppointmentStatus) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val dateTimeFormat = SimpleDateFormat("EEEE، d MMMM - hh:mm a", Locale("ar"))
    val formattedDateTime = dateTimeFormat.format(Date(appointment.dateTime))
    val currencyFormat = NumberFormat.getNumberInstance(Locale("ar", "SA"))

    val typeIcon: ImageVector = when (appointment.type) {
        AppointmentType.VIDEO_CALL -> Icons.Default.Videocam
        AppointmentType.PHONE_CALL -> Icons.Default.Call
        AppointmentType.IN_PERSON -> Icons.Default.LocationOn
    }

    GlowFloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("appointment_card_${appointment.id}"),
        glowColor = when (appointment.type) {
            AppointmentType.VIDEO_CALL -> Color(0xFF00E5FF)
            AppointmentType.PHONE_CALL -> NeonGold
            AppointmentType.IN_PERSON -> NeonEmerald
        },
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF142B22)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = appointment.type.titleAr,
                        tint = NeonEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = appointment.type.titleAr,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonEmerald
                    )
                }

                StatusBadge(status = appointment.status)

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "حذف الموعد",
                        tint = Color(0xFFFF5252).copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Client and Time info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF889C91),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "العميل: ${appointment.clientName}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF889C91),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$formattedDateTime (${appointment.durationMinutes} دقيقة)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
            }

            if (appointment.locationOrLink.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appointment.locationOrLink))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignore if not a valid URL
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Link,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = appointment.locationOrLink,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00E5FF)
                    )
                }
            }

            if (appointment.dealValue > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "القيمة المتوقعة للصفقة: ${currencyFormat.format(appointment.dealValue)} ر.س",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonGold
                        )
                    )
                }
            }

            if (appointment.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظات: ${appointment.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF90A397)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Status Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appointment.status != AppointmentStatus.COMPLETED) {
                    AssistChip(
                        onClick = { onStatusChange(AppointmentStatus.COMPLETED) },
                        label = { Text("تم الاجتماع", color = NeonEmerald) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF142B20)),
                        leadingIcon = {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(16.dp))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (appointment.status != AppointmentStatus.CONFIRMED && appointment.status != AppointmentStatus.COMPLETED) {
                    AssistChip(
                        onClick = { onStatusChange(AppointmentStatus.CONFIRMED) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF0D2533)),
                        label = { Text("تأكيد الموعد", color = Color(0xFF00E5FF)) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: AppointmentStatus) {
    val (color, bgColor) = when (status) {
        AppointmentStatus.CONFIRMED -> Color(0xFF00E5FF) to Color(0x3300E5FF)
        AppointmentStatus.COMPLETED -> NeonEmerald to Color(0x3300FF87)
        AppointmentStatus.SCHEDULED -> NeonGold to Color(0x33FFD700)
        AppointmentStatus.CANCELLED -> Color(0xFFFF5252) to Color(0x33FF5252)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.titleAr,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
