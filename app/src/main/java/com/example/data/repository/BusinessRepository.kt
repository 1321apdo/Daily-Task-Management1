package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BusinessDao
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.FinanceTransaction
import com.example.model.TaskItem
import com.example.model.TaskStatus
import kotlinx.coroutines.flow.Flow

class BusinessRepository(private val businessDao: BusinessDao) {

    // Tasks
    val allTasks: Flow<List<TaskItem>> = businessDao.getAllTasks()
    val activeTasks: Flow<List<TaskItem>> = businessDao.getActiveTasks()

    fun getTasksByProject(projectName: String): Flow<List<TaskItem>> =
        businessDao.getTasksByProject(projectName)

    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskItem>> =
        businessDao.getTasksByStatus(status)

    fun getTasksDueBefore(deadline: Long): Flow<List<TaskItem>> =
        businessDao.getTasksDueBefore(deadline)

    suspend fun insertTask(task: TaskItem): Long = businessDao.insertTask(task)
    suspend fun updateTask(task: TaskItem) = businessDao.updateTask(task)
    suspend fun deleteTask(task: TaskItem) = businessDao.deleteTask(task)
    suspend fun updateTaskStatus(id: Long, status: TaskStatus) = businessDao.updateTaskStatus(id, status)
    suspend fun toggleTaskCompletion(id: Long) = businessDao.toggleTaskCompletion(id)

    suspend fun checkAndSeedInitialData() {
        if (businessDao.getAnyTask() == null) {
            AppDatabase.populateInitialBusinessData(businessDao)
        }
    }

    // Appointments
    val allAppointments: Flow<List<Appointment>> = businessDao.getAllAppointments()
    fun getUpcomingAppointments(currentTime: Long): Flow<List<Appointment>> =
        businessDao.getUpcomingAppointments(currentTime)

    suspend fun insertAppointment(appointment: Appointment): Long = businessDao.insertAppointment(appointment)
    suspend fun updateAppointment(appointment: Appointment) = businessDao.updateAppointment(appointment)
    suspend fun deleteAppointment(appointment: Appointment) = businessDao.deleteAppointment(appointment)
    suspend fun updateAppointmentStatus(id: Long, status: AppointmentStatus) =
        businessDao.updateAppointmentStatus(id, status)

    // Finance
    val allTransactions: Flow<List<FinanceTransaction>> = businessDao.getAllTransactions()
    suspend fun insertTransaction(transaction: FinanceTransaction): Long =
        businessDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: FinanceTransaction) =
        businessDao.deleteTransaction(transaction)
}
