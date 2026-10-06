package com.example.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import com.example.ads.AdMobStats
import com.example.data.repository.BusinessRepository
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.AppointmentType
import com.example.model.FinanceTransaction
import com.example.model.Priority
import com.example.model.TaskCategory
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.model.TransactionType
import com.example.security.PrivacyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessUiState(
    val taskSearchQuery: String = "",
    val taskFilterStatus: TaskStatus? = null,
    val taskFilterCategory: TaskCategory? = null,
    val isAddTaskDialogOpen: Boolean = false,
    val editingTask: TaskItem? = null,
    val isAddAppointmentDialogOpen: Boolean = false,
    val isAddTransactionDialogOpen: Boolean = false,
    val isPrivacyDialogOpen: Boolean = false,
    val selectedTab: Int = 0 // 0: Dashboard, 1: Tasks, 2: Appointments, 3: Finance, 4: AdMob
)

class BusinessViewModel(
    private val repository: BusinessRepository,
    val privacyManager: PrivacyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessUiState())
    val uiState: StateFlow<BusinessUiState> = _uiState.asStateFlow()

    val adStats: StateFlow<AdMobStats> = AdMobManager.adStats

    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTasks: StateFlow<List<TaskItem>> = combine(
        repository.allTasks,
        _uiState
    ) { tasks, state ->
        tasks.filter { task ->
            val matchesQuery = state.taskSearchQuery.isBlank() ||
                    task.title.contains(state.taskSearchQuery, ignoreCase = true) ||
                    task.clientOrProject.contains(state.taskSearchQuery, ignoreCase = true)
            val matchesStatus = state.taskFilterStatus == null || task.status == state.taskFilterStatus
            val matchesCategory = state.taskFilterCategory == null || task.category == state.taskFilterCategory
            matchesQuery && matchesStatus && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingAppointments: StateFlow<List<Appointment>> = repository.allAppointments
        .combine(_uiState) { list, _ ->
            val now = System.currentTimeMillis()
            list.filter { it.dateTime >= now - 3600000L } // include recent within last hour
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<FinanceTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial KPI derivations
    val totalRevenue: StateFlow<Double> = allTransactions.combine(_uiState) { transactions, _ ->
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = allTransactions.combine(_uiState) { transactions, _ ->
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.checkAndSeedInitialData()
            } catch (e: Throwable) {
                // Graceful error handling
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun setTaskSearchQuery(query: String) {
        _uiState.update { it.copy(taskSearchQuery = query) }
    }

    fun setTaskFilterStatus(status: TaskStatus?) {
        _uiState.update { it.copy(taskFilterStatus = status) }
    }

    fun setTaskFilterCategory(category: TaskCategory?) {
        _uiState.update { it.copy(taskFilterCategory = category) }
    }

    fun setAddTaskDialog(open: Boolean) {
        _uiState.update { it.copy(isAddTaskDialogOpen = open) }
    }

    fun setEditingTask(task: TaskItem?) {
        _uiState.update { it.copy(editingTask = task) }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            repository.updateTask(task)
            setEditingTask(null)
        }
    }

    fun setAddAppointmentDialog(open: Boolean) {
        _uiState.update { it.copy(isAddAppointmentDialogOpen = open) }
    }

    fun setAddTransactionDialog(open: Boolean) {
        _uiState.update { it.copy(isAddTransactionDialogOpen = open) }
    }

    // Task Actions
    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        dueDate: Long,
        clientOrProject: String,
        category: TaskCategory = TaskCategory.WORK,
        activity: Activity? = null
    ) {
        viewModelScope.launch {
            repository.insertTask(
                TaskItem(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    clientOrProject = clientOrProject
                )
            )
            setAddTaskDialog(false)
            activity?.let { AdMobManager.showInterstitialAd(it) }
        }
    }

    fun toggleTaskCompleted(task: TaskItem, activity: Activity? = null) {
        viewModelScope.launch {
            val newStatus = if (task.status == TaskStatus.COMPLETED) {
                TaskStatus.PENDING
            } else {
                AdMobManager.addRewardPoints(25) // Earn real reward points for completing task!
                TaskStatus.COMPLETED
            }
            repository.updateTaskStatus(task.id, newStatus)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Appointment Actions
    fun addAppointment(
        title: String,
        clientName: String,
        dateTime: Long,
        durationMinutes: Int,
        locationOrLink: String,
        type: AppointmentType,
        dealValue: Double,
        notes: String,
        activity: Activity? = null
    ) {
        viewModelScope.launch {
            val newId = repository.insertAppointment(
                Appointment(
                    title = title,
                    clientName = clientName,
                    dateTime = dateTime,
                    durationMinutes = durationMinutes,
                    locationOrLink = locationOrLink,
                    type = type,
                    dealValue = dealValue,
                    notes = notes
                )
            )

            // Automatically schedule background alarm reminder for the appointment
            if (activity != null && dateTime > System.currentTimeMillis()) {
                com.example.notifications.BackgroundReminderManager.scheduleReminder(
                    context = activity,
                    reminderId = newId,
                    title = "تذكير بموعد عمل: $title",
                    message = "مع العميل: $clientName ($locationOrLink)",
                    triggerAtMillis = dateTime
                )
            }

            setAddAppointmentDialog(false)
            activity?.let { AdMobManager.showInterstitialAd(it) }
        }
    }

    fun updateAppointmentStatus(appointment: Appointment, newStatus: AppointmentStatus) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointment.id, newStatus)
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.deleteAppointment(appointment)
        }
    }

    // Finance Actions
    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                FinanceTransaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    notes = notes
                )
            )
            setAddTransactionDialog(false)
        }
    }

    fun deleteTransaction(transaction: FinanceTransaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun updateAdMobConfig(
        isTestMode: Boolean,
        customBannerId: String,
        customInterstitialId: String,
        customRewardedId: String,
        customAppId: String
    ) {
        AdMobManager.updateConfig(
            isTestMode = isTestMode,
            customBannerId = customBannerId,
            customInterstitialId = customInterstitialId,
            customRewardedId = customRewardedId,
            customAppId = customAppId
        )
    }

    fun triggerInterstitialTest(activity: Activity) {
        AdMobManager.showInterstitialAd(activity, force = true)
    }

    fun triggerRewardedTest(activity: Activity, onRewardEarned: () -> Unit = {}) {
        AdMobManager.showRewardedAd(activity) {
            onRewardEarned()
        }
    }

    fun spendRewardPoints(points: Int): Boolean = AdMobManager.spendPoints(points)
    fun addRewardPoints(points: Int) = AdMobManager.addRewardPoints(points)
    fun unlockVip(hours: Int = 24) = AdMobManager.unlockVip(hours)

    fun setPrivacyDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isPrivacyDialogOpen = isOpen) }
    }

    class Factory(
        private val repository: BusinessRepository,
        private val privacyManager: PrivacyManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
                return BusinessViewModel(repository, privacyManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
