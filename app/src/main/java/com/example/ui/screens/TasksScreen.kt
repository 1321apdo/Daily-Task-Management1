package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdBannerView
import com.example.ads.AdMobManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.model.TaskCategory
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.ui.BusinessViewModel
import com.example.ui.components.GlowFloatingCard
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(viewModel: BusinessViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val totalCount = allTasks.size
    val completedCount = allTasks.count { it.status == TaskStatus.COMPLETED }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setAddTaskDialog(true) },
                containerColor = NeonEmerald,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة مهمة جديدة")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("tasks_screen")
        ) {
            // Screen Title & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تنظيم وإدارة المهام",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "تتبع إنجاز المشاريع والمهام بأعلى كفاءة وإنتاجية",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5B8AD)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val reportBuilder = StringBuilder()
                            reportBuilder.append("📋 تقرير المهام اليومية\n")
                            reportBuilder.append("التاريخ: ${java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale("ar")).format(java.util.Date())}\n")
                            reportBuilder.append("إجمالي المهام: ${allTasks.size} | المكتملة: ${allTasks.count { it.status == TaskStatus.COMPLETED }}\n")
                            reportBuilder.append("============================\n\n")
                            allTasks.forEach { task ->
                                val statusMark = if (task.status == TaskStatus.COMPLETED) "✅ [مكتملة]" else "⏳ [قيد التنفيذ]"
                                reportBuilder.append("$statusMark ${task.title}\n")
                                reportBuilder.append("   • التصنيف: ${task.category.titleAr} | الأولوية: ${task.priority.titleAr}\n")
                                if (task.description.isNotBlank()) {
                                    reportBuilder.append("   • الملاحظات: ${task.description}\n")
                                }
                                reportBuilder.append("\n")
                            }
                            reportBuilder.append("تم التصدير بنجاح عبر تطبيق إدارة المهام اليومية ✨")

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "تقرير المهام اليومية")
                                putExtra(Intent.EXTRA_TEXT, reportBuilder.toString())
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "تصدير ومشاركة تقرير المهام"))
                            AdMobManager.addRewardPoints(3)
                            Toast.makeText(context, "تم تصدير التقرير بنجاح وإضافة +3 نقاط لرصيدك! 🎉", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonEmerald),
                        border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تصدير التقرير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.taskSearchQuery,
                    onValueChange = { viewModel.setTaskSearchQuery(it) },
                    placeholder = { Text("بحث في المهام أو المشاريع...", color = Color(0xFF7E9488)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonEmerald) },
                    trailingIcon = {
                        if (uiState.taskSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setTaskSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Color.White)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = Color(0xFF274235),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_search_bar")
                )
            }

            // In-page AdMob Banner on Tasks Page
            AdBannerView(modifier = Modifier.padding(vertical = 2.dp))

            // Completion Progress Bar in Glowing Card
            GlowFloatingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                glowColor = NeonEmerald
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "معدل الإنجاز الكلي",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% ($completedCount من $totalCount)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        color = NeonEmerald,
                        trackColor = Color(0xFF162920),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    val isAllSelected = uiState.taskFilterCategory == null
                    SuggestionChip(
                        onClick = { viewModel.setTaskFilterCategory(null) },
                        label = {
                            Text(
                                text = "جميع التصنيفات",
                                color = if (isAllSelected) Color.Black else Color.White,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = if (isAllSelected) {
                            SuggestionChipDefaults.suggestionChipColors(containerColor = NeonEmerald)
                        } else {
                            SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1B2E24))
                        }
                    )
                }

                items(TaskCategory.values()) { cat ->
                    val isSelected = uiState.taskFilterCategory == cat
                    val count = allTasks.count { it.category == cat }
                    val activeColor = when (cat) {
                        TaskCategory.URGENT -> Color(0xFFFF453A)
                        TaskCategory.WORK -> NeonEmerald
                        TaskCategory.PERSONAL -> Color(0xFFBF5AF2)
                        TaskCategory.FINANCE -> NeonGold
                        TaskCategory.GENERAL -> Color(0xFF64D2FF)
                    }
                    SuggestionChip(
                        onClick = {
                            viewModel.setTaskFilterCategory(if (isSelected) null else cat)
                        },
                        label = {
                            Text(
                                text = "${cat.iconEmoji} ${cat.titleAr} ($count)",
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = if (isSelected) {
                            SuggestionChipDefaults.suggestionChipColors(containerColor = activeColor)
                        } else {
                            SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1B2E24))
                        }
                    )
                }
            }

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = uiState.taskFilterStatus == null
                    SuggestionChip(
                        onClick = { viewModel.setTaskFilterStatus(null) },
                        label = { Text("الكل ($totalCount)", color = if (isSelected) Color.Black else Color.White) },
                        colors = if (isSelected) {
                            SuggestionChipDefaults.suggestionChipColors(
                                containerColor = NeonEmerald
                            )
                        } else {
                            SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1B2E24))
                        }
                    )
                }

                TaskStatus.values().forEach { status ->
                    val isSelected = uiState.taskFilterStatus == status
                    val count = allTasks.count { it.status == status }
                    item {
                        SuggestionChip(
                            onClick = { viewModel.setTaskFilterStatus(status) },
                            label = { Text("${status.titleAr} ($count)", color = if (isSelected) Color.Black else Color.White) },
                            colors = if (isSelected) {
                                SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = NeonEmerald
                                )
                            } else {
                                SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1B2E24))
                            }
                        )
                    }
                }
            }

            // Tasks List
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF557766),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد مهام تطابق هذا الاختيار",
                            color = Color(0xFFA5B8AD),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        GlowingTaskListItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompleted(task, activity) },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { viewModel.setEditingTask(task) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlowingTaskListItem(
    task: TaskItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
    val dueDateStr = dateFormat.format(Date(task.dueDate))
    var isDeleting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scaleCheck by animateFloatAsState(
        targetValue = if (isCompleted) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "task_check_scale"
    )

    AnimatedVisibility(
        visible = !isDeleting,
        enter = fadeIn(animationSpec = tween(350)) + expandVertically(
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        ),
        exit = fadeOut(animationSpec = tween(250)) + shrinkVertically(
            animationSpec = tween(300)
        ) + slideOutHorizontally(animationSpec = tween(300)) { -it },
        modifier = modifier
    ) {
        GlowFloatingCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("task_card_${task.id}"),
            glowColor = if (isCompleted) Color(0xFF274235) else NeonEmerald,
            shape = RoundedCornerShape(16.dp),
            onClick = onEdit
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "إكمال المهمة",
                            tint = if (isCompleted) NeonEmerald else Color(0xFF7E9488),
                            modifier = Modifier
                                .size(24.dp)
                                .scale(scaleCheck)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )

                        if (task.clientOrProject.isNotBlank()) {
                            Text(
                                text = "المشروع: ${task.clientOrProject}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeonEmerald
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadge(category = task.category)
                        PriorityBadge(priority = task.priority)
                    }

                    IconButton(
                        onClick = {
                            isDeleting = true
                            scope.launch {
                                delay(280)
                                onDelete()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "حذف المهمة",
                            tint = Color(0xFFFF5252).copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD),
                    modifier = Modifier.padding(start = 48.dp, end = 8.dp, top = 2.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الاستحقاق: $dueDateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF889C91)
                )

                Text(
                    text = task.status.titleAr,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) NeonEmerald else NeonGold
                    )
                )
            }
        }
    }
}
}
