package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.store.model.StoreCategory
import com.example.store.model.StoreProduct
import com.example.ui.components.NeonEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onProductAdded: (StoreProduct) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var tagline by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("29.00") }
    var category by remember { mutableStateOf(StoreCategory.GITHUB_REPO) }
    var githubUrl by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("Kotlin") }

    val categories = listOf(
        StoreCategory.GITHUB_REPO,
        StoreCategory.MOBILE_APP,
        StoreCategory.SCRIPT_TOOL,
        StoreCategory.DEV_SERVICE,
        StoreCategory.UI_TEMPLATE
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = Color(0xFF14221B),
        shape = RoundedCornerShape(24.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إضافة منتج أو مشروع جديد لمتجرك",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category chips
                Text(
                    text = "نوع المنتج والتصنيف:",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFB0C4B8))
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NeonEmerald.copy(alpha = 0.2f) else Color(0xFF0F1A15),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonEmerald else Color(0xFF2C3E34),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${cat.iconEmoji} ${cat.titleAr}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) NeonEmerald else Color.LightGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم المشروع أو المنتج البرمجي") },
                    placeholder = { Text("مثال: تطبيق توصيل ذكي بكود مصدري كامل") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_title"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = Color(0xFF2C3E34),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonEmerald,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tagline
                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("نبذة مختصرة أو ميزة بارزة") },
                    placeholder = { Text("مثال: تصميم Clean Architecture ودعم الوضع الليلي") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = Color(0xFF2C3E34),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonEmerald,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Language Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("السعر ($)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_price"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonEmerald,
                            unfocusedBorderColor = Color(0xFF2C3E34),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = NeonEmerald,
                            unfocusedLabelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("اللغة / التقنية") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonEmerald,
                            unfocusedBorderColor = Color(0xFF2C3E34),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = NeonEmerald,
                            unfocusedLabelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // GitHub URL
                OutlinedTextField(
                    value = githubUrl,
                    onValueChange = { githubUrl = it },
                    label = { Text("رابط مستودع GitHub (اختياري)") },
                    placeholder = { Text("https://github.com/username/project") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = Color(0xFF2C3E34),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonEmerald,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف التفصيلي والملفات المضمنة") },
                    placeholder = { Text("اشرح ما يقدمه مشروعك وما يحتويه الكود والتراخيص...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = Color(0xFF2C3E34),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonEmerald,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val parsedPrice = priceText.toDoubleOrNull() ?: 19.99
                        val newProduct = StoreProduct(
                            id = "custom_${System.currentTimeMillis()}",
                            title = title,
                            tagline = tagline.ifBlank { "مشروع برمجي جاهز للبيع من متجر المطور" },
                            description = description.ifBlank { "كود مصدري كامل ومرخص تجارياً." },
                            category = category,
                            price = parsedPrice,
                            originalPrice = parsedPrice + 10.0,
                            githubRepoUrl = githubUrl,
                            githubStars = 12,
                            language = language.ifBlank { "Kotlin" },
                            version = "v1.0",
                            downloadsCount = 0,
                            rating = 5.0f,
                            features = listOf("كود مصدري كامل", "دعم فني وتحديثات", "رخصة تجارية كاملة"),
                            licenseType = "رخصة تجارية كاملة",
                            isFeatured = true
                        )
                        onProductAdded(newProduct)
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_submit_add_product"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("نشر المنتج في المتجر الآن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
