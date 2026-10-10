package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.store.model.StoreProduct
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailDialog(
    product: StoreProduct,
    onDismiss: () -> Unit,
    onAddToCart: (StoreProduct, String) -> Unit
) {
    val context = LocalContext.current
    var selectedLicense by remember { mutableStateOf(product.licenseType) }

    val licenseOptions = listOf(
        "رخصة تجارية كاملة" to "استخدام تجاري غير محدود في مشاريع العملاء والتطبيقات الخاصة",
        "كود مصدري مفتوح" to "إمكانية التعديل الكامل وإعادة البناء والمساهمة",
        "رخصة مطور سنوية" to "تحديثات دورية ودعم فني مخصص لمدة 12 شهراً"
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
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(NeonEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${product.category.iconEmoji} ${product.category.titleAr}",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonEmerald, fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title & Tagline
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = product.tagline,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0C4B8)),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Price and Rating Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1A15), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.formattedPrice,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            )
                            if (product.hasDiscount) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$${String.format("%.2f", product.originalPrice)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                )
                            }
                        }
                        Text(
                            text = "لغة البرمجة: ${product.language} • ${product.version}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = NeonGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.rating} (${product.downloadsCount} مبيعة)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Medium)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                Text(
                    text = "تفاصيل المنتج والكود المصدري",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD0DDD5)),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Features checklist
                if (product.features.isNotEmpty()) {
                    Text(
                        text = "المميزات المضمنة",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    product.features.forEach { feat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = feat, style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // License Selector
                Text(
                    text = "اختر نوع الترخيص:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Spacer(modifier = Modifier.height(6.dp))
                licenseOptions.forEach { (licTitle, licDesc) ->
                    val isSelected = selectedLicense == licTitle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(
                                if (isSelected) NeonEmerald.copy(alpha = 0.15f) else Color(0xFF0F1A15),
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) NeonEmerald else Color(0xFF2C3E34),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedLicense = licTitle }
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Verified else Icons.Default.Code,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonEmerald else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = licTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) NeonEmerald else Color.White
                                    )
                                )
                            }
                            Text(
                                text = licDesc,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                                modifier = Modifier.padding(start = 22.dp, top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: GitHub repo link & Add to cart
                if (product.githubRepoUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.githubRepoUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_github_repo"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("معاينة المستودع على GitHub (${product.githubStars} ⭐)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        onAddToCart(product, selectedLicense)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_add_to_cart_dialog"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إضافة إلى سلة المشتريات", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
