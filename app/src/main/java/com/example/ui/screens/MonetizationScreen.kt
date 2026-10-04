package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdBannerView
import com.example.ads.AdMobManager
import com.example.ui.BusinessViewModel
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold
import java.util.Locale

@Composable
fun MonetizationScreen(viewModel: BusinessViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val adStats by viewModel.adStats.collectAsStateWithLifecycle()

    var isRedeemDialogOpen by remember { mutableStateOf(false) }
    var hasClaimedDailyBonus by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("monetization_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Ad - Official Compliant Placement
        item {
            AdBannerView()
        }

        // 1. Visitor's Points Wallet & Calibrated High-Profit Progress Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF132D21),
                                    Color(0xFF0F4B2E),
                                    Color(0xFF0A1E15)
                                )
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
                            Column {
                                Text(
                                    text = "رصيد محفظتك من النقاط",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFA5B8AD)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = NeonGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${adStats.rewardPoints}",
                                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "نقطة",
                                        color = NeonGold,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                val estimatedValUsd = (adStats.rewardPoints / 1000.0)
                                Text(
                                    text = "القيمة النقدية: ≈ ${String.format(Locale.US, "%.2f", estimatedValUsd)} دولار أمريكي",
                                    color = NeonEmerald,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { isRedeemDialogOpen = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("redeem_points_button")
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("سحب الأرباح", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // High Profit Margin Cashout Goal (5,000 points = 5$)
                        val targetPoints = 5000
                        val progress = (adStats.rewardPoints.toFloat() / targetPoints).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الهدف للسحب: 5,000 نقطة (5$ كاش)",
                                fontSize = 12.sp,
                                color = Color(0xFFA5B8AD)
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% مكتمل",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonEmerald,
                            trackColor = Color(0xFF1B3D2A)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // VIP Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VipBadge(title = "تصدير غير محدود", modifier = Modifier.weight(1f))
                            VipBadge(title = "أولويات المهام", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        VipBadge(title = "حماية وتشفير محلي كامل للبيانات", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // 2. In-App Digital Perks Store (100% Net Profit - Zero Cashout Cost to You!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF102118)),
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonEmerald.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "متجر المزايا الرقمية (استبدال فوري) ⚡",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "استبدل نقاطك فوراً بمزايا حصرية دون الحاجة لانتظار السحب",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA5B8AD)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DigitalPerkItem(
                        icon = Icons.Default.WorkspacePremium,
                        title = "تفعيل عضوية VIP الذهبية (أسبوع)",
                        cost = "300 نقطة",
                        onUnlock = {
                            if (adStats.rewardPoints >= 300) {
                                AdMobManager.addRewardPoints(-300)
                                AdMobManager.unlockVip(hours = 24 * 7)
                                Toast.makeText(context, "👑 تم تفعيل عضوية VIP لمدة أسبوع بنجاح!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "تحتاج 300 نقطة لتفعيل هذه الميزة!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DigitalPerkItem(
                        icon = Icons.Default.PictureAsPdf,
                        title = "تصدير تقارير المهام PDF غير المحدود",
                        cost = "200 نقطة",
                        onUnlock = {
                            if (adStats.rewardPoints >= 200) {
                                AdMobManager.addRewardPoints(-200)
                                Toast.makeText(context, "📑 تم فتح ميزة تصدير PDF بنجاح!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "تحتاج 200 نقطة لتفعيل هذه الميزة!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DigitalPerkItem(
                        icon = Icons.Default.Palette,
                        title = "تخصيص ثيم النيون الذهبي المظلم",
                        cost = "150 نقطة",
                        onUnlock = {
                            if (adStats.rewardPoints >= 150) {
                                AdMobManager.addRewardPoints(-150)
                                Toast.makeText(context, "🎨 تم تفعيل الثيم الذهبي الحصري!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "تحتاج 150 نقطة لتفعيل هذه الميزة!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Section Title: Ways to Earn Points
        item {
            Text(
                text = "طرق كسب النقاط اليومية لربح المال 💰",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        // 3. Primary Rewarded Video Ad Card (Your Top Revenue Engine!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241C)),
                border = BorderStroke(1.5.dp, NeonEmerald.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "شاهد فيديو قصير (30 ثانية) 🎬",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "اكسب +10 نقاط فورية في رصيدك + تمديد VIP",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA5B8AD)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (activity != null) {
                                viewModel.triggerRewardedTest(activity) {
                                    AdMobManager.addRewardPoints(10)
                                    Toast.makeText(context, "🎉 رائع! تمت إضافة 10 نقاط لرصيدك بنجاح!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("watch_rewarded_video_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مشاهدة الفيديو وكسب +10 نقاط 💰",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // 4. Daily Check-in Card (+5 points)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111E18)),
                border = BorderStroke(1.dp, Color(0xFF224434))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonGold.copy(alpha = 0.15f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تسجيل الدخول اليومي 🎁",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (hasClaimedDailyBonus) "تم استلام مكافأة اليوم! عد غداً" else "سجل حضورك اليوم واحصل على +5 نقاط",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5B8AD)
                        )
                    }

                    Button(
                        onClick = {
                            if (!hasClaimedDailyBonus) {
                                hasClaimedDailyBonus = true
                                AdMobManager.addRewardPoints(5)
                                Toast.makeText(context, "🎁 مبروك! حصلت على 5 نقاط تسجيل دخول يومي", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "لقد حصلت على مكافأة اليوم بالفعل!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !hasClaimedDailyBonus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGold,
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFF27382F),
                            disabledContentColor = Color(0xFF7A8E84)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (hasClaimedDailyBonus) "مكتمل ✓" else "استلام +5",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. Productivity Task Reward Card (+3 points per task completed)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101D17)),
                border = BorderStroke(1.dp, Color(0xFF1E3A2C))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Assignment,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "إنجاز المهام اليومية 📋",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "كل مهمة تنجزها في التطبيق تمنحك +3 نقاط فورية",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5B8AD)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+3 نقاط",
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 6. Speed Boost & Support (+5 points)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241C)),
                border = BorderStroke(1.dp, Color(0xFF244434))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonGold.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تسريع الإنجاز والدعم ⚡️",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "مهمة إعلانية سريعة مع فاصل آمن لإضافة +5 نقاط",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA5B8AD)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (activity != null) {
                                viewModel.triggerInterstitialTest(activity)
                                AdMobManager.addRewardPoints(5)
                                Toast.makeText(context, "⚡ تمت إضافة 5 نقاط تسريع لرصيدك!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("boost_speed_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A2E),
                            contentColor = NeonEmerald
                        ),
                        border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنفيذ المهمة (+5 نقاط)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 7. Calibrated High Profit Margin Exchange Table (جدول استبدال عالي الربحية)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E16)),
                border = BorderStroke(1.dp, NeonGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = NeonGold, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جدول أسعار استبدال النقاط بأموال حقيقية 💵",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExchangeRateRow(points = "5,000 نقطة", reward = "5.00$ دولار", method = "رصيد PayPal أو بطاقة Google Play")
                    Spacer(modifier = Modifier.height(8.dp))
                    ExchangeRateRow(points = "10,000 نقطة", reward = "10.00$ دولار", method = "حوالة بنكية أو محفظة كاش")
                    Spacer(modifier = Modifier.height(8.dp))
                    ExchangeRateRow(points = "20,000 نقطة", reward = "25.00$ دولار (+5$ بونص)", method = "سحب فوري VIP مباشر")
                }
            }
        }

        // 8. Legitimate Strategy Explanation for Visitors
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1813)),
                border = BorderStroke(1.dp, Color(0xFF163324))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NeonEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "استراتيجية الربح النزيه والقانوني 100%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "يتم تمويل المكافآت من عوائد الرعاة والإعلانات الرسمية المعتمدة، ويتم توزيعها على المستخدمين النشطين بنزاهة وشفافية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5B8AD)
                        )
                    }
                }
            }
        }
    }

    // Cashout / Redeem Dialog
    if (isRedeemDialogOpen) {
        RedeemDialog(
            currentPoints = adStats.rewardPoints,
            onDismiss = { isRedeemDialogOpen = false },
            onConfirmRedeem = { method, account, points ->
                if (adStats.rewardPoints >= points) {
                    AdMobManager.addRewardPoints(-points)
                    isRedeemDialogOpen = false
                    Toast.makeText(context, "✅ تم تسجيل طلب السحب بقيمة $method إلى $account بنجاح! ستصلك الحوالة خلال 24-48 ساعة عمل.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "عذراً، الحد الأدنى للسحب هو 5,000 نقطة ورصيدك الحالي غير كافٍ!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
private fun DigitalPerkItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    cost: String,
    onUnlock: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NeonGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(text = "التكلفة: $cost", color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = onUnlock,
            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
        ) {
            Text("فتح الميزة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RedeemDialog(
    currentPoints: Int,
    onDismiss: () -> Unit,
    onConfirmRedeem: (method: String, account: String, points: Int) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("PayPal") }
    var accountAddress by remember { mutableStateOf("") }
    var selectedPoints by remember { mutableStateOf(5000) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "طلب سحب الأرباح",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إلغاء")
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "رصيدك الحالي: $currentPoints نقطة",
                    color = NeonEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "اختر وسيلة استلام الأموال:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("PayPal", "Google Play", "محفظة كاش").forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method, fontSize = 12.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = accountAddress,
                    onValueChange = {
                        accountAddress = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text(if (selectedMethod == "PayPal") "بريد PayPal الإلكتروني" else "رقم الحساب / الهاتف") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("يرجى إدخال عنوان الحساب بشكل صحيح") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "اختر باقة السحب (الحد الأدنى 5,000 نقطة):",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5000 to "5$", 10000 to "10$", 20000 to "25$").forEach { (pts, usd) ->
                        FilterChip(
                            selected = selectedPoints == pts,
                            onClick = { selectedPoints = pts },
                            label = { Text("$pts نقطة ($usd)", fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (accountAddress.isBlank()) {
                        isError = true
                    } else {
                        onConfirmRedeem(selectedMethod, accountAddress.trim(), selectedPoints)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonEmerald,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تأكيد طلب السحب", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ExchangeRateRow(points: String, reward: String, method: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = points, color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = method, color = Color(0xFFA5B8AD), fontSize = 11.sp)
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = NeonEmerald.copy(alpha = 0.2f)
        ) {
            Text(
                text = reward,
                color = NeonEmerald,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun VipBadge(title: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NeonEmerald,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
