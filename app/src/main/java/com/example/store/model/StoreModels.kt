package com.example.store.model

enum class StoreCategory(val titleAr: String, val iconEmoji: String) {
    ALL("الكل", "🌐"),
    GITHUB_REPO("مشاريع GitHub", "🐙"),
    MOBILE_APP("تطبيقات جاهزة", "📱"),
    SCRIPT_TOOL("سكربتات وأدوات", "⚡"),
    DEV_SERVICE("خدمات برمجية", "🛠️"),
    UI_TEMPLATE("تصاميم وقوالب", "🎨")
}

data class StoreProduct(
    val id: String,
    val title: String,
    val tagline: String,
    val description: String,
    val category: StoreCategory,
    val price: Double,
    val originalPrice: Double = 0.0,
    val githubRepoUrl: String = "",
    val githubStars: Int = 0,
    val language: String = "Kotlin",
    val version: String = "v1.0",
    val downloadsCount: Int = 120,
    val rating: Float = 4.9f,
    val features: List<String> = emptyList(),
    val licenseType: String = "رخصة تجارية كاملة",
    val isFeatured: Boolean = false,
    val imageUrl: String = ""
) {
    val hasDiscount: Boolean get() = originalPrice > price
    val formattedPrice: String get() = if (price <= 0.0) "مجاني (Open Source)" else "$${String.format("%.2f", price)}"
}

data class CartItem(
    val product: StoreProduct,
    val quantity: Int = 1,
    val selectedLicense: String = "رخصة تجارية كاملة"
) {
    val subtotal: Double get() = product.price * quantity
}

enum class OrderStatus(val titleAr: String) {
    COMPLETED("مكتمل وتم التسليم"),
    PROCESSING("جاري التجهيز")
}

data class StoreOrder(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<CartItem>,
    val totalAmount: Double,
    val discountApplied: Double = 0.0,
    val licenseKeys: List<String> = emptyList(),
    val status: OrderStatus = OrderStatus.COMPLETED,
    val customerEmail: String = "",
    val paymentMethod: String = "بطاقة ائتمانية"
)

data class GitHubStoreProfile(
    val username: String = "",
    val name: String = "",
    val storeTitle: String = "متجر المطور الرقمي",
    val bio: String = "",
    val avatarUrl: String = "",
    val githubProfileUrl: String = "",
    val publicReposCount: Int = 0,
    val totalStarsCount: Int = 0,
    val isConnected: Boolean = false,
    val totalSalesEarned: Double = 349.50,
    val totalOrdersCount: Int = 14
)
