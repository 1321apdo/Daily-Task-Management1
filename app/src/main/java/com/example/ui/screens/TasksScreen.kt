package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdBannerView
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
                Text(
                    text = "تنظيم وإدارة المهام",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "تتبع إنجاز المشاريع والمهام لتحقيق أقصى إنتاجية وأرباح",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )

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

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
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
                            onDelete = { viewModel.deleteTask(task) }
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
    onDelete: () -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
    val dueDateStr = dateFormat.format(Date(task.dueDate))

    GlowFloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        glowColor = if (isCompleted) Color(0xFF274235) else NeonEmerald,
        shape = RoundedCornerShape(16.dp)
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
                        modifier = Modifier.size(24.dp)
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

                PriorityBadge(priority = task.priority)

                IconButton(onClick = onDelete) {
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
