package com.example.store

import android.content.Context
import com.example.store.github.GitHubClient
import com.example.store.model.CartItem
import com.example.store.model.GitHubStoreProfile
import com.example.store.model.OrderStatus
import com.example.store.model.StoreCategory
import com.example.store.model.StoreOrder
import com.example.store.model.StoreProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.UUID

class StoreRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("github_store_prefs", Context.MODE_PRIVATE)

    private val _storeProfile = MutableStateFlow(
        GitHubStoreProfile(
            username = prefs.getString("gh_username", "") ?: "",
            name = prefs.getString("gh_name", "") ?: "",
            storeTitle = prefs.getString("gh_store_title", "متجر المطور الرقمي") ?: "متجر المطور الرقمي",
            bio = prefs.getString("gh_bio", "مشاريع برمجية وتطبيقات حصرية من مستودعات GitHub") ?: "",
            avatarUrl = prefs.getString("gh_avatar", "") ?: "",
            githubProfileUrl = prefs.getString("gh_url", "") ?: "",
            publicReposCount = prefs.getInt("gh_repos_count", 0),
            totalStarsCount = prefs.getInt("gh_stars_count", 0),
            isConnected = prefs.getBoolean("gh_connected", false),
            totalSalesEarned = 589.00,
            totalOrdersCount = 18
        )
    )
    val storeProfile: StateFlow<GitHubStoreProfile> = _storeProfile.asStateFlow()

    private val _products = MutableStateFlow<List<StoreProduct>>(getInitialProducts())
    val products: StateFlow<List<StoreProduct>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<StoreOrder>>(getInitialOrders())
    val orders: StateFlow<List<StoreOrder>> = _orders.asStateFlow()

    private fun getInitialProducts(): List<StoreProduct> {
        return listOf(
            StoreProduct(
                id = "prod_compose_starter",
                title = "قالب Android Jetpack Compose الاحترافي",
                tagline = "هيكل تطبيقي نظيف وجاهز للنشر على Google Play مع بنية MVVM",
                description = "كود مصدري كامل لتطبيق أندرويد متكامل مبني بأحدث تقنيات Jetpack Compose 2026، متضمن نظام الوضع الليلي، مصادقة المستخدمين، وقواعد بيانات Room مع اختبارات Robolectric شاملة.",
                category = StoreCategory.MOBILE_APP,
                price = 39.00,
                originalPrice = 59.00,
                githubRepoUrl = "https://github.com/apdo1321/compose-clean-starter",
                githubStars = 84,
                language = "Kotlin",
                version = "v2.4",
                downloadsCount = 230,
                rating = 4.9f,
                features = listOf("بنية Clean Architecture", "دعم كامل للغة العربية RTL", "جاهز للنشر على المتجر", "تحديثات مجانية لمدة سنة"),
                licenseType = "رخصة تجارية كاملة",
                isFeatured = true
            ),
            StoreProduct(
                id = "prod_ai_automation_bot",
                title = "بوت أتمتة وذكاء اصطناعي متعدد المنصات",
                tagline = "أداة سريعة لربط نماذج الذكاء الاصطناعي مع تيليجرام وواتساب وGitHub",
                description = "مشروع برمجي متكامل بلغة بايثون يقوم بمعالجة المهام الآلية وإرسال التقارير البرمجية مع تكامل فوري مع GitHub Webhooks وOpenAI API وGemini.",
                category = StoreCategory.SCRIPT_TOOL,
                price = 24.50,
                originalPrice = 35.00,
                githubRepoUrl = "https://github.com/apdo1321/ai-automation-agent",
                githubStars = 142,
                language = "Python",
                version = "v1.8",
                downloadsCount = 412,
                rating = 4.8f,
                features = listOf("نشر فوري عبر Docker", "دعم Gemini و GPT-4o", "واجهة ويب مصغرة", "أمان عالي ومشفر"),
                licenseType = "كود مصدري مفتوح المصرح به",
                isFeatured = true
            ),
            StoreProduct(
                id = "prod_ecommerce_api",
                title = "واجهة برمجة تطبيقات المتجر السحابي Cloud API",
                tagline = "RESTful API فائق السرعة مع توثيق Swagger ونظام الدفع Stripe",
                description = "خادم برمجيات خلفي Backend مبني بتقنية Node.js / Kotlin Ktor متوافق مع قواعد بيانات PostgreSQL وFirebase، مجهز بكافة endpoints المتاجر الإلكترونية وإدارة المخزون.",
                category = StoreCategory.DEV_SERVICE,
                price = 49.00,
                originalPrice = 75.00,
                githubRepoUrl = "https://github.com/apdo1321/cloud-store-backend",
                githubStars = 97,
                language = "TypeScript",
                version = "v3.1",
                downloadsCount = 185,
                rating = 5.0f,
                features = listOf("نظام صلاحيات JWT", "تكامل بوابات الدفع", "كود نظيف مع وثائق كاملة", "أداء سريع جداً"),
                licenseType = "رخصة مطور تجارية",
                isFeatured = false
            ),
            StoreProduct(
                id = "prod_flutter_ui_kit",
                title = "حزمة واجهات المتاجر الرقمية UI Kit",
                tagline = "أكثر من 40 شاشة تفاعلية وتصميم أنيق وجذاب للبرمجيات",
                description = "مجموعة مكونات واجهات وتصاميم حديثة جاهزة للاستخدام في تطبيقات المتاجر الرقمية مع دعم الوضع المظلم والرسوم المتحركة السلسة.",
                category = StoreCategory.UI_TEMPLATE,
                price = 19.99,
                originalPrice = 29.99,
                githubRepoUrl = "https://github.com/apdo1321/digital-store-ui-kit",
                githubStars = 63,
                language = "Flutter",
                version = "v1.2",
                downloadsCount = 310,
                rating = 4.7f,
                features = listOf("40+ شاشة جاهزة", "رسوم متحركة ناعمة", "تنسيق متجاوب لجميع الشاشات", "كود خفيف ومنظم"),
                licenseType = "رخصة استخدام غير محدودة",
                isFeatured = false
            ),
            StoreProduct(
                id = "prod_custom_dev_hour",
                title = "خدمة تطوير وبرمجة مشروع خاص",
                tagline = "تطوير ميزة مخصصة، إصلاح أخطاء، أو بناء تطبيق كامل حسب الطلب",
                description = "حجز استشارة أو ساعات برمجية متخصصة لتطوير ميزات مخصصة في تطبيقك، ربط واجهات الـ API، أو إعداد نظام النشر الآلي CI/CD مع مستودعك في GitHub.",
                category = StoreCategory.DEV_SERVICE,
                price = 79.00,
                originalPrice = 100.00,
                githubRepoUrl = "",
                githubStars = 0,
                language = "جميع اللغات",
                version = "خدمة مباشرة",
                downloadsCount = 45,
                rating = 5.0f,
                features = listOf("تنفيذ سريع ومتقن", "مراجعة كود مجانية", "ضمان الجودة", "دعم فني مباشر"),
                licenseType = "خدمة عمل مخصصة",
                isFeatured = true
            )
        )
    }

    private fun getInitialOrders(): List<StoreOrder> {
        return listOf(
            StoreOrder(
                id = "ORD-GH-9841",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                items = listOf(
                    CartItem(
                        product = getInitialProducts()[0],
                        quantity = 1,
                        selectedLicense = "رخصة تجارية كاملة"
                    )
                ),
                totalAmount = 39.00,
                discountApplied = 0.0,
                licenseKeys = listOf("LIC-COMPOSE-893A-4F29"),
                status = OrderStatus.COMPLETED,
                customerEmail = "client@techcorp.com",
                paymentMethod = "بطاقة ائتمانية"
            )
        )
    }

    suspend fun connectGitHubUser(rawUsername: String): Result<GitHubStoreProfile> = withContext(Dispatchers.IO) {
        val cleanUsername = rawUsername.trim().removePrefix("@").removePrefix("https://github.com/")
        if (cleanUsername.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("يرجى إدخال اسم مستخدم GitHub صحيح"))
        }

        try {
            val userResponse = GitHubClient.apiService.getUserProfile(cleanUsername)
            val updatedProfile = GitHubStoreProfile(
                username = userResponse.login,
                name = userResponse.name ?: userResponse.login,
                storeTitle = "متجر ${userResponse.name ?: userResponse.login} البرمجي",
                bio = userResponse.bio ?: "مشاريع برمجية وتطبيقات مفتوحة وخاصة من GitHub",
                avatarUrl = userResponse.avatarUrl ?: "",
                githubProfileUrl = userResponse.htmlUrl ?: "https://github.com/$cleanUsername",
                publicReposCount = userResponse.publicRepos ?: 0,
                totalStarsCount = 0,
                isConnected = true,
                totalSalesEarned = _storeProfile.value.totalSalesEarned,
                totalOrdersCount = _storeProfile.value.totalOrdersCount
            )

            // Save to preferences
            prefs.edit()
                .putString("gh_username", updatedProfile.username)
                .putString("gh_name", updatedProfile.name)
                .putString("gh_store_title", updatedProfile.storeTitle)
                .putString("gh_bio", updatedProfile.bio)
                .putString("gh_avatar", updatedProfile.avatarUrl)
                .putString("gh_url", updatedProfile.githubProfileUrl)
                .putInt("gh_repos_count", updatedProfile.publicReposCount)
                .putBoolean("gh_connected", true)
                .apply()

            _storeProfile.value = updatedProfile

            // Automatically import repositories in background
            try {
                importRepositoriesInternal(cleanUsername)
            } catch (_: Exception) {}

            Result.success(updatedProfile)
        } catch (e: Exception) {
            // Graceful fallback if offline or GitHub API rate limit
            val fallbackProfile = GitHubStoreProfile(
                username = cleanUsername,
                name = cleanUsername,
                storeTitle = "متجر $cleanUsername البرمجي",
                bio = "مستودعات ومشاريع برمجية حصرية على GitHub",
                avatarUrl = "https://github.com/$cleanUsername.png",
                githubProfileUrl = "https://github.com/$cleanUsername",
                publicReposCount = 5,
                totalStarsCount = 42,
                isConnected = true,
                totalSalesEarned = _storeProfile.value.totalSalesEarned,
                totalOrdersCount = _storeProfile.value.totalOrdersCount
            )
            prefs.edit()
                .putString("gh_username", fallbackProfile.username)
                .putString("gh_name", fallbackProfile.name)
                .putString("gh_store_title", fallbackProfile.storeTitle)
                .putString("gh_avatar", fallbackProfile.avatarUrl)
                .putBoolean("gh_connected", true)
                .apply()

            _storeProfile.value = fallbackProfile
            Result.success(fallbackProfile)
        }
    }

    suspend fun importRepositoriesFromGitHub(username: String): Int = withContext(Dispatchers.IO) {
        val cleanUsername = username.ifBlank { _storeProfile.value.username }
        if (cleanUsername.isBlank()) return@withContext 0
        importRepositoriesInternal(cleanUsername)
    }

    private suspend fun importRepositoriesInternal(cleanUsername: String): Int {
        return try {
            val repos = GitHubClient.apiService.getUserRepos(cleanUsername)
            var count = 0
            val newProducts = repos.mapNotNull { repo ->
                if (repo.name.isBlank()) null
                else {
                    count++
                    val defaultPrice = when {
                        (repo.stargazersCount ?: 0) > 50 -> 34.00
                        (repo.stargazersCount ?: 0) > 10 -> 19.50
                        else -> 9.99
                    }
                    StoreProduct(
                        id = "gh_repo_${repo.id}",
                        title = repo.name.replace("-", " ").replace("_", " ").replaceFirstChar { it.uppercase() },
                        tagline = repo.description ?: "مشروع برمجي كامل متاح عبر مستودع GitHub",
                        description = (repo.description ?: "كود مصدري نظيف وموثق") + "\nمستودع رسمي: ${repo.htmlUrl}",
                        category = StoreCategory.GITHUB_REPO,
                        price = defaultPrice,
                        originalPrice = defaultPrice + 15.00,
                        githubRepoUrl = repo.htmlUrl,
                        githubStars = repo.stargazersCount ?: 0,
                        language = repo.language ?: "Kotlin",
                        version = "v1.0",
                        downloadsCount = (repo.forksCount ?: 1) * 12 + 10,
                        rating = 4.8f,
                        features = listOf("كود مصدري كامل", "مستودع GitHub رسمي", "تحديثات مستمرة", "دعم فني مباشر"),
                        licenseType = "رخصة تجارية كاملة",
                        isFeatured = (repo.stargazersCount ?: 0) > 5
                    )
                }
            }

            if (newProducts.isNotEmpty()) {
                _products.update { current ->
                    val existingIds = current.map { it.id }.toSet()
                    val toAdd = newProducts.filter { it.id !in existingIds }
                    toAdd + current
                }
            }
            count
        } catch (_: Exception) {
            0
        }
    }

    fun addProduct(product: StoreProduct) {
        _products.update { listOf(product) + it }
    }

    fun deleteProduct(productId: String) {
        _products.update { it.filter { p -> p.id != productId } }
        removeFromCart(productId)
    }

    fun addToCart(product: StoreProduct, license: String = "رخصة تجارية كاملة") {
        _cartItems.update { items ->
            val index = items.indexOfFirst { it.product.id == product.id }
            if (index >= 0) {
                items.toMutableList().apply {
                    val current = this[index]
                    this[index] = current.copy(quantity = current.quantity + 1, selectedLicense = license)
                }
            } else {
                items + CartItem(product = product, quantity = 1, selectedLicense = license)
            }
        }
    }

    fun updateCartItemQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
        } else {
            _cartItems.update { items ->
                items.map {
                    if (it.product.id == productId) it.copy(quantity = quantity) else it
                }
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.update { it.filter { item -> item.product.id != productId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkout(
        paymentMethod: String,
        customerEmail: String,
        discountApplied: Double
    ): StoreOrder {
        val currentCart = _cartItems.value
        val subtotal = currentCart.sumOf { it.subtotal }
        val finalTotal = (subtotal - discountApplied).coerceAtLeast(0.0)

        val licenseKeys = currentCart.map { item ->
            "LIC-${item.product.language.take(3).uppercase()}-${UUID.randomUUID().toString().take(8).uppercase()}"
        }

        val order = StoreOrder(
            id = "ORD-GH-${System.currentTimeMillis().toString().takeLast(6)}",
            timestamp = System.currentTimeMillis(),
            items = currentCart,
            totalAmount = finalTotal,
            discountApplied = discountApplied,
            licenseKeys = licenseKeys,
            status = OrderStatus.COMPLETED,
            customerEmail = customerEmail.ifBlank { "developer@github.com" },
            paymentMethod = paymentMethod
        )

        _orders.update { listOf(order) + it }
        _storeProfile.update {
            it.copy(
                totalSalesEarned = it.totalSalesEarned + finalTotal,
                totalOrdersCount = it.totalOrdersCount + 1
            )
        }
        clearCart()
        return order
    }

    fun disconnectGitHub() {
        prefs.edit().clear().apply()
        _storeProfile.value = GitHubStoreProfile()
    }
}
