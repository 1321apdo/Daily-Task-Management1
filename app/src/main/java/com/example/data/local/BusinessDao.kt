package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.FinanceTransaction
import com.example.model.TaskItem
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {

    // --- Tasks ---
    @Query("SELECT * FROM tasks LIMIT 1")
    suspend fun getAnyTask(): TaskItem?

    @Query("SELECT * FROM tasks ORDER BY status ASC, priority DESC, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE status != :completedStatus ORDER BY dueDate ASC")
    fun getActiveTasks(completedStatus: TaskStatus = TaskStatus.COMPLETED): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: TaskStatus)


    // --- Appointments ---
    @Query("SELECT * FROM appointments ORDER BY dateTime ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE dateTime >= :currentTime ORDER BY dateTime ASC")
    fun getUpcomingAppointments(currentTime: Long): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus)


    // --- Finance ---
    @Query("SELECT * FROM finance_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<FinanceTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: FinanceTransaction)
}
