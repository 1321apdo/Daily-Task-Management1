package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.store.model.CartItem
import com.example.store.model.StoreOrder
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold

@Composable
fun CheckoutDialog(
    cartItems: List<CartItem>,
    userPoints: Int,
    onDismiss: () -> Unit,
    onCompleteOrder: (paymentMethod: String, email: String, discount: Double, pointsSpent: Int) -> StoreOrder?
) {
    val context = LocalContext.current
    var customerEmail by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("بطاقة ائتمانية / دفع رقمي") }
    var usePointsDiscount by remember { mutableStateOf(userPoints >= 100) }
    var completedOrder by remember { mutableStateOf<StoreOrder?>(null) }

    val subtotal = cartItems.sumOf { it.subtotal }
    // 100 points = $5 discount, up to 50% of order value
    val maxPointsToUse = ((subtotal * 0.5) / 5.0 * 100).toInt().coerceAtMost(userPoints).coerceAtLeast(0)
    val pointsDiscountAmount = if (usePointsDiscount && maxPointsToUse >= 100) {
        (maxPointsToUse / 100) * 5.0
    } else 0.0

    val finalTotal = (subtotal - pointsDiscountAmount).coerceAtLeast(0.0)

    val paymentOptions = listOf(
        "بطاقة ائتمانية / مدى / فيزا",
        "رصيد أرباحي المتراكمة",
        "Google Pay / المحفظة الرقمية"
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
                if (completedOrder != null) {
                    // Success View
                    val order = completedOrder!!
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تمت عملية الشراء بنجاح!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "رقم الطلب: ${order.id}",
                            style = MaterialTheme.typography.bodySmall.copy(color = NeonEmerald)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "مفاتيح التراخيص البرمجية الفورية:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        order.licenseKeys.forEachIndexed { index, licKey ->
                            val itemTitle = order.items.getOrNull(index)?.product?.title ?: "مشروع برمجي"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color(0xFF0F1A15), RoundedCornerShape(10.dp))
                                    .border(1.dp, NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = itemTitle, style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Key, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = licKey,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("License Key", licKey))
                                            Toast.makeText(context, "تم نسخ مفتاح الترخيص!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonEmerald)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("العودة إلى المتجر", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                } else {
                    // Checkout Summary Form
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إتمام الدفع واستلام التراخيص",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Products list in order
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1A15), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "ملخص السلة (${cartItems.size} منتجات):",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFB0C4B8))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.product.title} (x${item.quantity})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$${String.format("%.2f", item.subtotal)}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = NeonEmerald, fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Divider(color = Color(0xFF2C3E34), modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المجموع الفرعي:", style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray))
                            Text("$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
                        }

                        if (pointsDiscountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("خصم نقاط المكافآت:", style = MaterialTheme.typography.bodySmall.copy(color = NeonGold))
                                Text("-$${String.format("%.2f", pointsDiscountAmount)}", style = MaterialTheme.typography.bodySmall.copy(color = NeonGold, fontWeight = FontWeight.Bold))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الإجمالي النهائي للدفع:", style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                            Text("$${String.format("%.2f", finalTotal)}", style = MaterialTheme.typography.titleMedium.copy(color = NeonEmerald, fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Use Points for Discount option
                    if (userPoints >= 100) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeonGold.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = usePointsDiscount,
                                    onCheckedChange = { usePointsDiscount = it },
                                    colors = CheckboxDefaults.colors(checkedColor = NeonGold, checkmarkColor = Color.Black)
                                )
                                Column {
                                    Text(
                                        text = "استبدال نقاط المكافآت بخصم نقدي ($5 لكل 100 نقطة)",
                                        style = MaterialTheme.typography.bodySmall.copy(color = NeonGold, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "رصيدك الحالي: $userPoints نقطة • وفر حتى $${String.format("%.2f", pointsDiscountAmount)}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email input
                    OutlinedTextField(
                        value = customerEmail,
                        onValueChange = { customerEmail = it },
                        label = { Text("البريد الإلكتروني لاستلام التراخيص والأكواد") },
                        placeholder = { Text("example@gmail.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_checkout_email"),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Payment Method selector
                    Text(
                        text = "اختر طريقة الدفع الآمنة:",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFB0C4B8))
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    paymentOptions.forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .background(
                                    if (isSelected) NeonEmerald.copy(alpha = 0.15f) else Color(0xFF0F1A15),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonEmerald else Color(0xFF2C3E34),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPaymentMethod = method }
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonEmerald else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) NeonEmerald else Color.White,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val order = onCompleteOrder(
                                selectedPaymentMethod,
                                customerEmail,
                                pointsDiscountAmount,
                                if (pointsDiscountAmount > 0) maxPointsToUse else 0
                            )
                            if (order != null) {
                                completedOrder = order
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_complete_checkout"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "دفع آمن الآن ($${String.format("%.2f", finalTotal)})",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}
