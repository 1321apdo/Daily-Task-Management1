package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ads.AdBannerView
import com.example.model.Appointment
import com.example.model.Priority
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.ui.BusinessViewModel
import com.example.ui.components.GlowButton
import com.example.ui.components.GlowFloatingCard
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: BusinessViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToMonetization: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val adStats by viewModel.adStats.collectAsStateWithLifecycle()
    val privacySettings by viewModel.privacyManager.settings.collectAsStateWithLifecycle()

    val netProfit = totalRevenue - totalExpenses
    val pendingTasksCount = allTasks.count { it.status != TaskStatus.COMPLETED }
    val completedTasksCount = allTasks.count { it.status == TaskStatus.COMPLETED }

    val currencyFormatter = NumberFormat.getNumberInstance(Locale("ar", "SA")).apply {
        maximumFractionDigits = 0
    }

    val displayNetProfit = if (privacySettings.isDiscreetMode) "•••• ر.س" else "${currencyFormatter.format(netProfit)} ر.س"
    val displayTotalRevenue = if (privacySettings.isDiscreetMode) "••••" else "${currencyFormatter.format(totalRevenue)} ر.س"
    val displayAdMobEarnings = if (privacySettings.isDiscreetMode) "•••• ر.س" else "${String.format(Locale.US, "%.2f", adStats.estimatedEarningsSar)} ر.س"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Welcome and Executive Header
        item {
            ExecutiveHeader()
        }

        // In-page AdMob Banner
        item {
            AdBannerView(modifier = Modifier.padding(vertical = 4.dp))
        }

        // Floating Luminous KPI Cards Row
        item {
            Text(
                text = "مؤشرات الأداء المالي والتشغيلي",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    GlowingKpiMetricCard(
                        title = "صافي الأرباح",
                        value = displayNetProfit,
                        subtitle = "إيرادات: $displayTotalRevenue",
                        icon = Icons.Default.TrendingUp,
                        glowColor = NeonEmerald,
                        secondaryColor = Color(0xFF00BFA5),
                        onClick = onNavigateToFinance
                    )
                }

                item {
                    GlowingKpiMetricCard(
                        title = "رصيد مكافآت VIP",
                        value = "${adStats.rewardPoints} نقطة",
                        subtitle = "عضوية VIP نشطة ✨",
                        icon = Icons.Default.CardGiftcard,
                        glowColor = NeonGold,
                        secondaryColor = Color(0xFFFFA000),
                        onClick = onNavigateToMonetization
                    )
                }

                item {
                    GlowingKpiMetricCard(
                        title = "المهام المعلقة",
                        value = "$pendingTasksCount مهمة",
                        subtitle = "$completedTasksCount مهمة منجزة",
                        icon = Icons.Default.Assignment,
                        glowColor = Color(0xFF00E5FF),
                        secondaryColor = Color(0xFF2979FF),
                        onClick = onNavigateToTasks
                    )
                }

                item {
                    GlowingKpiMetricCard(
                        title = "المواعيد القادمة",
                        value = "${upcomingAppointments.size} موعد",
                        subtitle = "جدول اليوم والغد",
                        icon = Icons.Default.Event,
                        glowColor = Color(0xFFE040FB),
                        secondaryColor = Color(0xFF7C4DFF),
                        onClick = onNavigateToAppointments
                    )
                }
            }
        }

        // Quick Actions with Glowing Illumination
        item {
            Spacer(modifier = Modifier.height(18.dp))
            QuickActionsBar(
                onAddTask = { viewModel.setAddTaskDialog(true) },
                onAddAppointment = { viewModel.setAddAppointmentDialog(true) },
                onAddTransaction = { viewModel.setAddTransactionDialog(true) },
                onMonetization = onNavigateToMonetization
            )
        }

        // Next Appointment Highlight in a Floating Luminous Card
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الموعد القادم",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                TextButton(onClick = onNavigateToAppointments) {
                    Text("عرض الكل", color = NeonEmerald)
                }
            }

            val nextAppointment = upcomingAppointments.firstOrNull()
            if (nextAppointment != null) {
                GlowingNextAppointmentBanner(nextAppointment, onClick = onNavigateToAppointments)
            } else {
                EmptyScheduleBanner(onSchedule = { viewModel.setAddAppointmentDialog(true) })
            }
        }

        // High Priority Tasks
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المهام العاجلة والمهمة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                TextButton(onClick = onNavigateToTasks) {
                    Text("إدارة المهام", color = NeonEmerald)
                }
            }
        }

        val topTasks = allTasks.filter { it.status != TaskStatus.COMPLETED }.take(3)
        if (topTasks.isEmpty()) {
            item {
                GlowFloatingCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    glowColor = NeonEmerald.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "أحسنت! جميع المهام العاجلة مكتملة حالياً.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(topTasks, key = { it.id }) { task ->
                GlowingTaskItem(
                    task = task,
                    onToggle = { viewModel.toggleTaskCompleted(task, activity) }
                )
            }
        }

        // Blogger Website & Direct Share Card
        item {
            GlowFloatingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                glowColor = NeonGold,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "موقعنا الرسمي والتنزيل المباشر",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "apdo1321.blogspot.com",
                                color = NeonEmerald,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://apdo1321.blogspot.com"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // ignore
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("زيارة الموقع", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "حمّل تطبيق إدارة الأعمال والمهام مع عوائد أرباح ونقاط تفاعلية! قم بزيارة موقعنا: https://apdo1321.blogspot.com أو جرّب المعاينة المباشرة: https://ais-pre-ual4e72q7qffkf5toikjss-148594802387.europe-west2.run.app"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق والموقع"))
                                } catch (e: Exception) {
                                    // ignore
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, NeonEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مشاركة التطبيق", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecutiveHeader() {
    val dateStr = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(Date())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NeonEmerald.copy(alpha = 0.12f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مرحباً بك في إدارة الأعمال",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA5B4AC)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0A1C14),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonEmerald.copy(alpha = 0.7f)),
                modifier = Modifier.size(46.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.app_launcher_foreground_1790996683198),
                    contentDescription = "شعار التطبيق",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

@Composable
private fun GlowingKpiMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    glowColor: Color,
    secondaryColor: Color,
    onClick: () -> Unit
) {
    GlowFloatingCard(
        modifier = Modifier
            .width(190.dp)
            .height(130.dp),
        glowColor = glowColor,
        secondaryGlowColor = secondaryColor,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            glowColor.copy(alpha = 0.22f),
                            Color(0xFF0F1E17).copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = glowColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = value,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFFB0C4B8),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsBar(
    onAddTask: () -> Unit,
    onAddAppointment: () -> Unit,
    onAddTransaction: () -> Unit,
    onMonetization: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "إجراءات سريعة بإضاءة تفاعلية",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlowActionButton(
                title = "مهمة",
                icon = Icons.Default.Add,
                modifier = Modifier.weight(1f),
                glowColor = NeonEmerald,
                onClick = onAddTask
            )
            GlowActionButton(
                title = "موعد",
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f),
                glowColor = Color(0xFF00E5FF),
                onClick = onAddAppointment
            )
            GlowActionButton(
                title = "مالية",
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f),
                glowColor = NeonGold,
                onClick = onAddTransaction
            )
            GlowActionButton(
                title = "AdMob",
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.weight(1f),
                glowColor = Color(0xFFFF5252),
                onClick = onMonetization
            )
        }
    }
}

@Composable
private fun GlowActionButton(
    title: String,
    icon: ImageVector,
    glowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlowFloatingCard(
        modifier = modifier.height(74.dp),
        glowColor = glowColor,
        secondaryGlowColor = glowColor.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = glowColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun GlowingNextAppointmentBanner(
    appointment: Appointment,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("EEEE - hh:mm a", Locale("ar"))
    val formattedTime = timeFormat.format(Date(appointment.dateTime))

    GlowFloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        glowColor = Color(0xFF00E5FF),
        secondaryGlowColor = NeonEmerald,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D3328)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = NeonEmerald
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
                    text = "${appointment.clientName} • $formattedTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
                Text(
                    text = appointment.type.titleAr,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonEmerald
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "عرض التفاصيل",
                tint = NeonEmerald
            )
        }
    }
}

@Composable
private fun EmptyScheduleBanner(onSchedule: () -> Unit) {
    GlowFloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        glowColor = Color(0xFF385547)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "لا توجد مواعيد مجدولة لليوم",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "نظم جدول لقاءاتك مع العملاء لزيادة إغلاق الصفقات",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF90A397)
                )
            }
            TextButton(onClick = onSchedule) {
                Text("جدولة موعد", color = NeonEmerald)
            }
        }
    }
}

@Composable
private fun GlowingTaskItem(
    task: TaskItem,
    onToggle: () -> Unit
) {
    GlowFloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        glowColor = when (task.priority) {
            Priority.URGENT -> Color(0xFFFF3366)
            Priority.HIGH -> Color(0xFFFF9900)
            Priority.MEDIUM -> Color(0xFF00E5FF)
            Priority.LOW -> NeonEmerald
        },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (task.status == TaskStatus.COMPLETED) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = "إكمال المهمة",
                    tint = if (task.status == TaskStatus.COMPLETED) {
                        NeonEmerald
                    } else {
                        Color(0xFF88998F)
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                if (task.clientOrProject.isNotBlank()) {
                    Text(
                        text = task.clientOrProject,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA5B8AD)
                    )
                }
            }

            PriorityBadge(priority = task.priority)
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val (color, bgColor) = when (priority) {
        Priority.URGENT -> Color(0xFFFF3366) to Color(0x33FF3366)
        Priority.HIGH -> Color(0xFFFF9900) to Color(0x33FF9900)
        Priority.MEDIUM -> Color(0xFF00E5FF) to Color(0x3300E5FF)
        Priority.LOW -> NeonEmerald to Color(0x3300FF87)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = priority.titleAr,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
