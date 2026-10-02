package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.AppointmentStatus
import com.example.model.AppointmentType
import com.example.model.Priority
import com.example.model.TaskStatus
import com.example.model.TransactionType

class Converters {
    @TypeConverter
    fun fromPriority(value: Priority?): String = value?.name ?: Priority.MEDIUM.name

    @TypeConverter
    fun toPriority(value: String?): Priority = value?.let {
        try { enumValueOf<Priority>(it) } catch (e: Exception) { Priority.MEDIUM }
    } ?: Priority.MEDIUM

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus?): String = value?.name ?: TaskStatus.PENDING.name

    @TypeConverter
    fun toTaskStatus(value: String?): TaskStatus = value?.let {
        try { enumValueOf<TaskStatus>(it) } catch (e: Exception) { TaskStatus.PENDING }
    } ?: TaskStatus.PENDING

    @TypeConverter
    fun fromAppointmentType(value: AppointmentType?): String = value?.name ?: AppointmentType.VIDEO_CALL.name

    @TypeConverter
    fun toAppointmentType(value: String?): AppointmentType = value?.let {
        try { enumValueOf<AppointmentType>(it) } catch (e: Exception) { AppointmentType.VIDEO_CALL }
    } ?: AppointmentType.VIDEO_CALL

    @TypeConverter
    fun fromAppointmentStatus(value: AppointmentStatus?): String = value?.name ?: AppointmentStatus.SCHEDULED.name

    @TypeConverter
    fun toAppointmentStatus(value: String?): AppointmentStatus = value?.let {
        try { enumValueOf<AppointmentStatus>(it) } catch (e: Exception) { AppointmentStatus.SCHEDULED }
    } ?: AppointmentStatus.SCHEDULED

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String = value?.name ?: TransactionType.INCOME.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType = value?.let {
        try { enumValueOf<TransactionType>(it) } catch (e: Exception) { TransactionType.INCOME }
    } ?: TransactionType.INCOME
}
