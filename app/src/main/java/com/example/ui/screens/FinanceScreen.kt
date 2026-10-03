package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdBannerView
import com.example.model.FinanceTransaction
import com.example.model.TransactionType
import com.example.ui.BusinessViewModel
import com.example.ui.components.GlowFloatingCard
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceScreen(viewModel: BusinessViewModel) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val privacySettings by viewModel.privacyManager.settings.collectAsStateWithLifecycle()
    val netProfit = totalRevenue - totalExpenses

    val currencyFormat = NumberFormat.getNumberInstance(Locale("ar", "SA")).apply {
        maximumFractionDigits = 2
    }

    fun formatAmount(amount: Double): String {
        return if (privacySettings.isDiscreetMode) "•••• ر.س" else "${currencyFormat.format(amount)} ر.س"
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setAddTransactionDialog(true) },
                containerColor = NeonGold,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_transaction_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "تسجيل حركة مالية")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("finance_screen")
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "الأرباح والمالية التشغيلية",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "متابعة التدفق النقدي، إيرادات الصفقات، وعوائد إعلانات AdMob",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
            }

            // In-page AdMob Banner
            AdBannerView(modifier = Modifier.padding(vertical = 2.dp))

            // Net Profit Hero Glowing Card
            GlowFloatingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                glowColor = if (netProfit >= 0) NeonEmerald else Color(0xFFFF3366),
                secondaryGlowColor = NeonGold,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = if (netProfit >= 0) {
                                    listOf(Color(0xFF0C2B1F), Color(0xFF071F16))
                                } else {
                                    listOf(Color(0xFF330C15), Color(0xFF1F070C))
                                }
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "صافي أرباح الأعمال",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (netProfit >= 0) NeonEmerald else Color(0xFFFF5252)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = formatAmount(netProfit),
                            color = if (netProfit >= 0) NeonEmerald else Color(0xFFFF5252),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "إيرادات: ${formatAmount(totalRevenue)}",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "مصروفات: ${formatAmount(totalExpenses)}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Transactions Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل العمليات المالية",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "${allTransactions.size} حركة",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
            }

            // Transactions List
            if (allTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFF557766),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد حركات مالية مسجلة بعد",
                            color = Color(0xFFA5B8AD),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allTransactions, key = { it.id }) { item ->
                        GlowingTransactionListItem(
                            transaction = item,
                            isDiscreetMode = privacySettings.isDiscreetMode,
                            onDelete = { viewModel.deleteTransaction(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlowingTransactionListItem(
    transaction: FinanceTransaction,
    isDiscreetMode: Boolean,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
    val dateStr = dateFormat.format(Date(transaction.date))
    val currencyFormat = NumberFormat.getNumberInstance(Locale("ar", "SA"))

    GlowFloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}"),
        glowColor = if (isIncome) NeonEmerald else Color(0xFFFF5252),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isIncome) Color(0x3300FF87) else Color(0x33FF5252)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = transaction.type.titleAr,
                    tint = if (isIncome) NeonEmerald else Color(0xFFFF5252),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGold
                    )
                    Text(
                        text = " • $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA5B8AD)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isDiscreetMode) "•••• ر.س" else "${if (isIncome) "+" else "-"}${currencyFormat.format(transaction.amount)} ر.س",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isIncome) NeonEmerald else Color(0xFFFF5252)
                    )
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "حذف الحركة",
                        tint = Color(0xFFFF5252).copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
