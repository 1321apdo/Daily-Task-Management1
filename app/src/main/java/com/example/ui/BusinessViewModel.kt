package com.example.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import com.example.ads.AdMobStats
import com.example.data.repository.AuthRepository
import com.example.data.repository.BusinessRepository
import com.example.data.repository.FirestoreRepository
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.AppointmentType
import com.example.model.FinanceTransaction
import com.example.model.Priority
import com.example.model.TaskCategory
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.model.TransactionType
import com.example.model.UserProfile
import com.example.security.PrivacyManager
import com.google.firebase.auth.FirebaseUser
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
    val isAuthDialogOpen: Boolean = false,
    val selectedTab: Int = 0, // 0: Dashboard, 1: Tasks, 2: Appointments, 3: Store, 4: Rewards
    // GitHub Developer Store UI States
    val storeSearchQuery: String = "",
    val storeSelectedCategory: com.example.store.model.StoreCategory = com.example.store.model.StoreCategory.ALL,
    val storeSubTab: Int = 0, // 0: Storefront, 1: Cart, 2: Merchant Studio, 3: Orders & Licenses
    val isAddProductDialogOpen: Boolean = false,
    val selectedProductForDetail: com.example.store.model.StoreProduct? = null,
    val isCheckoutDialogOpen: Boolean = false,
    val isGitHubConnectDialogOpen: Boolean = false,
    val isConnectingGitHub: Boolean = false,
    val storeUserFeedbackMessage: String? = null
)

class BusinessViewModel(
    private val repository: BusinessRepository,
    val privacyManager: PrivacyManager,
    val authRepository: AuthRepository? = null,
    val firestoreRepository: FirestoreRepository? = null,
    val storeRepository: com.example.store.StoreRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessUiState())
    val uiState: StateFlow<BusinessUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<FirebaseUser?> = (authRepository?.authStateFlow()
        ?: MutableStateFlow(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authRepository?.currentUser)

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

    fun setAuthDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isAuthDialogOpen = isOpen) }
    }

    fun syncPointsWithCloud() {
        val user = currentUser.value ?: return
        val currentStats = adStats.value
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository?.syncPointsToCloud(
                userId = user.uid,
                points = currentStats.rewardPoints.toLong(),
                vipUntilMs = currentStats.vipUnlockedUntilMs
            )
        }
    }

    fun signOut() {
        authRepository?.signOut()
    }

    // ==========================================
    // GITHUB DEVELOPER STORE STATE & ACTIONS
    // ==========================================

    val storeProfile: StateFlow<com.example.store.model.GitHubStoreProfile> = (storeRepository?.storeProfile
        ?: MutableStateFlow(com.example.store.model.GitHubStoreProfile()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.store.model.GitHubStoreProfile())

    val storeProducts: StateFlow<List<com.example.store.model.StoreProduct>> = (storeRepository?.products
        ?: MutableStateFlow(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<com.example.store.model.CartItem>> = (storeRepository?.cartItems
        ?: MutableStateFlow(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storeOrders: StateFlow<List<com.example.store.model.StoreOrder>> = (storeRepository?.orders
        ?: MutableStateFlow(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredStoreProducts: StateFlow<List<com.example.store.model.StoreProduct>> = combine(
        storeProducts,
        _uiState
    ) { products, state ->
        products.filter { product ->
            val matchesCategory = state.storeSelectedCategory == com.example.store.model.StoreCategory.ALL ||
                    product.category == state.storeSelectedCategory
            val matchesQuery = state.storeSearchQuery.isBlank() ||
                    product.title.contains(state.storeSearchQuery, ignoreCase = true) ||
                    product.tagline.contains(state.storeSearchQuery, ignoreCase = true) ||
                    product.language.contains(state.storeSearchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotalCount: StateFlow<Int> = cartItems.combine(_uiState) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.combine(_uiState) { items, _ ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setStoreSubTab(subTab: Int) {
        _uiState.update { it.copy(storeSubTab = subTab) }
    }

    fun setStoreSearchQuery(query: String) {
        _uiState.update { it.copy(storeSearchQuery = query) }
    }

    fun setStoreCategory(category: com.example.store.model.StoreCategory) {
        _uiState.update { it.copy(storeSelectedCategory = category) }
    }

    fun setAddProductDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isAddProductDialogOpen = isOpen) }
    }

    fun setSelectedProductForDetail(product: com.example.store.model.StoreProduct?) {
        _uiState.update { it.copy(selectedProductForDetail = product) }
    }

    fun setCheckoutDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isCheckoutDialogOpen = isOpen) }
    }

    fun setGitHubConnectDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isGitHubConnectDialogOpen = isOpen) }
    }

    fun setStoreFeedback(message: String?) {
        _uiState.update { it.copy(storeUserFeedbackMessage = message) }
    }

    fun connectGitHubAccount(username: String, onFinished: (Boolean, String) -> Unit = { _, _ -> }) {
        _uiState.update { it.copy(isConnectingGitHub = true) }
        viewModelScope.launch {
            val result = storeRepository?.connectGitHubUser(username)
            _uiState.update { it.copy(isConnectingGitHub = false, isGitHubConnectDialogOpen = false) }
            if (result != null && result.isSuccess) {
                val profile = result.getOrNull()
                setStoreFeedback("تم ربط متجرك بنجاح مع حساب GitHub: ${profile?.username}")
                onFinished(true, "تم الربط بنجاح!")
            } else {
                setStoreFeedback("تعذر الاتصال بـ GitHub أو الحساب غير متاح")
                onFinished(false, "تعذر الاتصال بحساب GitHub")
            }
        }
    }

    fun importGitHubRepositories(onFinished: (Int) -> Unit = {}) {
        val currentUsername = storeProfile.value.username
        if (currentUsername.isBlank()) {
            setStoreFeedback("يرجى ربط حسابك على GitHub أولاً")
            setGitHubConnectDialog(true)
            return
        }
        viewModelScope.launch {
            val imported = storeRepository?.importRepositoriesFromGitHub(currentUsername) ?: 0
            setStoreFeedback("تم استيراد $imported مستودع من GitHub وإضافتها لمتجرك بنجاح!")
            onFinished(imported)
        }
    }

    fun addNewProductToStore(product: com.example.store.model.StoreProduct) {
        storeRepository?.addProduct(product)
        setAddProductDialog(false)
        setStoreFeedback("تم نشر المنتج '${product.title}' في متجرك بنجاح!")
    }

    fun deleteProductFromStore(productId: String) {
        storeRepository?.deleteProduct(productId)
        setStoreFeedback("تم حذف المنتج من المتجر")
    }

    fun addProductToCart(product: com.example.store.model.StoreProduct, license: String = "رخصة تجارية كاملة") {
        storeRepository?.addToCart(product, license)
        setStoreFeedback("تمت إضافة '${product.title}' إلى سلة المشتريات")
    }

    fun updateCartItemQuantity(productId: String, quantity: Int) {
        storeRepository?.updateCartItemQuantity(productId, quantity)
    }

    fun removeProductFromCart(productId: String) {
        storeRepository?.removeFromCart(productId)
    }

    fun clearCart() {
        storeRepository?.clearCart()
    }

    fun completeCheckout(
        paymentMethod: String,
        customerEmail: String,
        coinsDiscountApplied: Double = 0.0,
        coinsSpent: Int = 0
    ): com.example.store.model.StoreOrder? {
        if (coinsSpent > 0) {
            spendRewardPoints(coinsSpent)
        }
        val order = storeRepository?.checkout(
            paymentMethod = paymentMethod,
            customerEmail = customerEmail,
            discountApplied = coinsDiscountApplied
        )
        if (order != null) {
            setCheckoutDialog(false)
            setStoreFeedback("تمت عملية الشراء بنجاح! تم إصدار ترخيصك الفوري")
        }
        return order
    }

    fun disconnectGitHubStore() {
        storeRepository?.disconnectGitHub()
        setStoreFeedback("تم فصل حساب GitHub")
    }

    class Factory(
        private val repository: BusinessRepository,
        private val privacyManager: PrivacyManager,
        private val authRepository: AuthRepository? = null,
        private val firestoreRepository: FirestoreRepository? = null,
        private val storeRepository: com.example.store.StoreRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
                return BusinessViewModel(
                    repository = repository,
                    privacyManager = privacyManager,
                    authRepository = authRepository,
                    firestoreRepository = firestoreRepository,
                    storeRepository = storeRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
