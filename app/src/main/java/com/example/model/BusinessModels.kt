package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority(val titleAr: String, val level: Int) {
    LOW("منخفض", 1),
    MEDIUM("متوسط", 2),
    HIGH("مهم", 3),
    URGENT("عاجل جداً", 4)
}

enum class TaskStatus(val titleAr: String) {
    PENDING("قيد الانتظار"),
    IN_PROGRESS("جاري العمل"),
    COMPLETED("مكتمل")
}

enum class TaskCategory(val titleAr: String, val iconEmoji: String) {
    WORK("عمل", "💼"),
    PERSONAL("شخصي", "👤"),
    URGENT("طارئ", "🚨"),
    FINANCE("مالي", "💰"),
    GENERAL("عام", "📁")
}

/**
 * Room Database Schema Entity for Business Tasks
 * Stores business tasks, category, associated project names, deadlines, priority, and completion status.
 */
@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: TaskCategory = TaskCategory.WORK,
    val projectName: String = "",
    val deadline: Long = System.currentTimeMillis() + 86400000L,
    val status: TaskStatus = TaskStatus.PENDING,
    val priority: Priority = Priority.MEDIUM,
    val clientOrProject: String = "",
    val dueDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isCompleted: Boolean
        get() = status == TaskStatus.COMPLETED

    val effectiveProjectName: String
        get() = projectName.ifBlank { clientOrProject }

    val effectiveDeadline: Long
        get() = if (deadline > 0) deadline else (if (dueDate > 0) dueDate else createdAt + 86400000L)
}

enum class AppointmentType(val titleAr: String) {
    IN_PERSON("اجتماع حضوري"),
    VIDEO_CALL("مكالمة فيديو"),
    PHONE_CALL("مكالمة هاتفية")
}

enum class AppointmentStatus(val titleAr: String) {
    SCHEDULED("مجدول"),
    CONFIRMED("مؤكد"),
    COMPLETED("تم بنجاح"),
    CANCELLED("ملغي")
}

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val clientName: String,
    val dateTime: Long = System.currentTimeMillis() + 86400000L,
    val durationMinutes: Int = 45,
    val locationOrLink: String = "",
    val type: AppointmentType = AppointmentType.VIDEO_CALL,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val notes: String = "",
    val dealValue: Double = 0.0
)

enum class TransactionType(val titleAr: String) {
    INCOME("إيراد / ربح"),
    EXPENSE("مصروف / تكلفة")
}

@Entity(tableName = "finance_transactions")
data class FinanceTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)
