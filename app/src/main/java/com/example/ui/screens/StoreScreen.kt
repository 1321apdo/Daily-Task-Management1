package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.store.model.CartItem
import com.example.store.model.StoreCategory
import com.example.store.model.StoreOrder
import com.example.store.model.StoreProduct
import com.example.ui.BusinessViewModel
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import com.example.ui.dialogs.AddProductDialog
import com.example.ui.dialogs.CheckoutDialog
import com.example.ui.dialogs.GitHubConnectDialog
import com.example.ui.dialogs.ProductDetailDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoreScreen(viewModel: BusinessViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val storeProfile by viewModel.storeProfile.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredStoreProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.storeProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.storeOrders.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartTotalCount.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val adStats by viewModel.adStats.collectAsStateWithLifecycle()

    val subTabs = listOf(
        "تصفح المعروضات",
        "سلة الشراء",
        "استوديو التاجر",
        "طلباتي والتراخيص"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Developer & GitHub Store Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFF0F1A15), RoundedCornerShape(16.dp))
                .border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (storeProfile.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = storeProfile.avatarUrl,
                            contentDescription = "صورة المطور",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonEmerald, CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(NeonEmerald.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = NeonEmerald)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = storeProfile.storeTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "موثق",
                                tint = NeonEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (storeProfile.isConnected) "@${storeProfile.username} على GitHub" else "اضغط لربط حساب GitHub",
                            style = MaterialTheme.typography.bodySmall.copy(color = NeonEmerald)
                        )
                    }
                }

                // Connect / Manage GitHub button
                OutlinedButton(
                    onClick = { viewModel.setGitHubConnectDialog(true) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (storeProfile.isConnected) NeonEmerald else Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_manage_github_store")
                ) {
                    Icon(
                        imageVector = if (storeProfile.isConnected) Icons.Default.CheckCircle else Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (storeProfile.isConnected) "حسابي المربوط" else "ربط GitHub",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // SubTabs Navigation Row
        ScrollableTabRow(
            selectedTabIndex = uiState.storeSubTab,
            containerColor = Color.Transparent,
            contentColor = NeonEmerald,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.storeSubTab]),
                    color = NeonEmerald,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.storeSubTab == index,
                    onClick = { viewModel.setStoreSubTab(index) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (uiState.storeSubTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.storeSubTab == index) NeonEmerald else Color.Gray
                                )
                            )
                            if (index == 1 && cartCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(NeonGold, CircleShape)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$cartCount",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        // Main Content based on SubTab
        when (uiState.storeSubTab) {
            0 -> StorefrontTab(
                viewModel = viewModel,
                products = filteredProducts,
                searchQuery = uiState.storeSearchQuery,
                selectedCategory = uiState.storeSelectedCategory
            )
            1 -> CartTab(
                cartItems = cartItems,
                subtotal = cartSubtotal,
                onUpdateQuantity = { id, qty -> viewModel.updateCartItemQuantity(id, qty) },
                onRemoveItem = { id -> viewModel.removeProductFromCart(id) },
                onCheckout = { viewModel.setCheckoutDialog(true) },
                onBrowseMore = { viewModel.setStoreSubTab(0) }
            )
            2 -> MerchantStudioTab(
                viewModel = viewModel,
                allProducts = allProducts,
                storeProfile = storeProfile,
                onConnectGitHub = { viewModel.setGitHubConnectDialog(true) },
                onImportRepos = { viewModel.importGitHubRepositories() },
                onAddProduct = { viewModel.setAddProductDialog(true) }
            )
            3 -> OrdersTab(
                orders = orders,
                context = context,
                onShopNow = { viewModel.setStoreSubTab(0) }
            )
        }
    }

    // Dialogs
    if (uiState.isGitHubConnectDialogOpen) {
        GitHubConnectDialog(
            currentProfile = storeProfile,
            isLoading = uiState.isConnectingGitHub,
            onDismiss = { viewModel.setGitHubConnectDialog(false) },
            onConnect = { username -> viewModel.connectGitHubAccount(username) },
            onDisconnect = { viewModel.disconnectGitHubStore() },
            onImportRepos = { viewModel.importGitHubRepositories() }
        )
    }

    if (uiState.selectedProductForDetail != null) {
        ProductDetailDialog(
            product = uiState.selectedProductForDetail!!,
            onDismiss = { viewModel.setSelectedProductForDetail(null) },
            onAddToCart = { prod, lic -> viewModel.addProductToCart(prod, lic) }
        )
    }

    if (uiState.isAddProductDialogOpen) {
        AddProductDialog(
            onDismiss = { viewModel.setAddProductDialog(false) },
            onProductAdded = { newProd -> viewModel.addNewProductToStore(newProd) }
        )
    }

    if (uiState.isCheckoutDialogOpen) {
        CheckoutDialog(
            cartItems = cartItems,
            userPoints = adStats.rewardPoints,
            onDismiss = { viewModel.setCheckoutDialog(false) },
            onCompleteOrder = { method, email, discount, coinsSpent ->
                viewModel.completeCheckout(method, email, discount, coinsSpent)
            }
        )
    }
}

@Composable
private fun StorefrontTab(
    viewModel: BusinessViewModel,
    products: List<StoreProduct>,
    searchQuery: String,
    selectedCategory: StoreCategory
) {
    val categories = listOf(
        StoreCategory.ALL,
        StoreCategory.GITHUB_REPO,
        StoreCategory.MOBILE_APP,
        StoreCategory.SCRIPT_TOOL,
        StoreCategory.DEV_SERVICE,
        StoreCategory.UI_TEMPLATE
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setStoreSearchQuery(it) },
                placeholder = { Text("ابحث عن مشروع أو سكربت أو لغة برمجة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonEmerald) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setStoreSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_store_search"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonEmerald,
                    unfocusedBorderColor = Color(0xFF2C3E34),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) NeonEmerald else Color(0xFF0F1A15),
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) NeonEmerald else Color(0xFF2C3E34),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setStoreCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${cat.iconEmoji} ${cat.titleAr}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        // Results count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المشاريع والمنتجات المتوفرة (${products.size}):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = "تراخيص فورية مع الأكواد",
                    style = MaterialTheme.typography.labelSmall.copy(color = NeonEmerald)
                )
            }
        }

        // Product Cards
        if (products.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا توجد منتجات مطابقة للبحث", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                    }
                }
            }
        } else {
            items(products, key = { it.id }) { product ->
                ProductStoreCard(
                    product = product,
                    onViewDetails = { viewModel.setSelectedProductForDetail(product) },
                    onAddToCart = { viewModel.addProductToCart(product) }
                )
            }
        }
    }
}

@Composable
private fun ProductStoreCard(
    product: StoreProduct,
    onViewDetails: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("card_product_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF203328))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(NeonEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${product.category.iconEmoji} ${product.category.titleAr}",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonEmerald, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E2D24), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = product.language,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
                        )
                    }
                }

                if (product.githubStars > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = NeonGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.githubStars}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Tagline
            Text(
                text = product.tagline,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0C4B8)),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.formattedPrice,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        )
                        if (product.hasDiscount) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$${String.format("%.2f", product.originalPrice)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            )
                        }
                    }
                    Text(
                        text = "تقييم: ${product.rating} ⭐",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("التفاصيل", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        modifier = Modifier.testTag("btn_quick_add_${product.id}")
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("أضف للسلة", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartTab(
    cartItems: List<CartItem>,
    subtotal: Double,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    onBrowseMore: () -> Unit
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "سلة المشتريات فارغة حالياً",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = "تصفح مشاريع متجرك الرقمي وأضف قوالب أو أكواد مصدرية لسلتك",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBrowseMore,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تصفح المعروضات الآن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems, key = { it.product.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                                Text(
                                    text = "${item.selectedLicense} • $${String.format("%.2f", item.product.price)}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeonEmerald)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$${String.format("%.2f", item.subtotal)}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                IconButton(onClick = { onRemoveItem(item.product.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFFF6B6B))
                                }
                            }
                        }
                    }
                }
            }

            // Checkout Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1A15), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("إجمالي المشتريات:", style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray))
                        Text(
                            text = "$${String.format("%.2f", subtotal)}",
                            style = MaterialTheme.typography.titleLarge.copy(color = NeonEmerald, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_cart_checkout"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("متابعة الدفع واستلام التراخيص", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MerchantStudioTab(
    viewModel: BusinessViewModel,
    allProducts: List<StoreProduct>,
    storeProfile: com.example.store.model.GitHubStoreProfile,
    onConnectGitHub: () -> Unit,
    onImportRepos: () -> Unit,
    onAddProduct: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Merchant KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("أرباح المتجر", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Text(
                            text = "$${String.format("%.2f", storeProfile.totalSalesEarned)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NeonEmerald)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("المشاريع المعروضة", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Text(
                            text = "${allProducts.size} مشروع",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("المبيعات المكتملة", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Text(
                            text = "${storeProfile.totalOrdersCount} طلب",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NeonGold)
                        )
                    }
                }
            }
        }

        // Quick Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "إدارة محتوى المتجر ومستودعات GitHub",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onImportRepos,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_studio_import_repos"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استيراد من GitHub", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = onAddProduct,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_studio_add_product"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A2B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة منتج يدوي", color = NeonEmerald, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // List of Published Products
        item {
            Text(
                text = "قائمة المنتجات المعروضة في متجرك (${allProducts.size}):",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }

        items(allProducts, key = { it.id }) { prod ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = prod.title, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        Text(text = "${prod.formattedPrice} • ${prod.language}", style = MaterialTheme.typography.labelSmall.copy(color = NeonEmerald))
                    }

                    IconButton(onClick = { viewModel.deleteProductFromStore(prod.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFFF6B6B))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTab(
    orders: List<StoreOrder>,
    context: Context,
    onShopNow: () -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Key, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("لا توجد طلبات سابقة حتى الآن", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                Text("عند شرائك لأي مشروع برمجي، ستظهر تراخيصك الفورية هنا.", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onShopNow,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تصفح المتجر", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "طلباتي والتراخيص الرقمية (${orders.size}):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
            }

            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A15)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "طلب رقم: ${order.id}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            Text(text = "$${String.format("%.2f", order.totalAmount)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NeonEmerald))
                        }

                        Text(text = "طريقة الدفع: ${order.paymentMethod}", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))

                        Spacer(modifier = Modifier.height(8.dp))

                        // Licenses list
                        order.licenseKeys.forEachIndexed { idx, key ->
                            val itemName = order.items.getOrNull(idx)?.product?.title ?: "مشروع برمجي"
                            val repoUrl = order.items.getOrNull(idx)?.product?.githubRepoUrl ?: ""
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .background(Color(0xFF14221B), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = itemName, style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray))
                                        Text(text = key, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NeonEmerald))
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("License", key))
                                                Toast.makeText(context, "تم نسخ مفتاح الترخيص!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonEmerald, modifier = Modifier.size(18.dp))
                                        }

                                        if (repoUrl.isNotBlank()) {
                                            IconButton(
                                                onClick = {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
                                                        context.startActivity(intent)
                                                    } catch (_: Exception) {}
                                                }
                                            ) {
                                                Icon(Icons.Default.OpenInBrowser, contentDescription = "فتح", tint = Color.White, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
