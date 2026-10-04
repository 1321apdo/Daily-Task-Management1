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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdBannerView
import com.example.ads.AdMobManager
import com.example.ui.BusinessViewModel
import java.util.Locale

@Composable
fun MonetizationScreen(viewModel: BusinessViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val clipboardManager = LocalClipboardManager.current
    val adStats by viewModel.adStats.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("monetization_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & Brief
        item {
            Column {
                Text(
                    text = "مركز أرباح Google AdMob",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "معرفاتك الرسمية مشفرة ومدمجة بشكل دائم لحفظ أرباح الزوار لحسابك حصراً",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
            }
        }

        // Active Banner on Monetization Page
        item {
            AdBannerView()
        }

        // Verified Account Banner with Tamper-Proof Lock
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10281D)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF16A34A),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الأرباح مدمجة ومحمية ضد العبث (Tamper-Proof)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF86EFAC)
                            )
                        )
                        Text(
                            text = "كافة زيارات وإعلانات التطبيق مربوطة تلقائياً بحساب الناشر الخاص بك",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD1FAE5)
                        )
                    }
                }
            }
        }

        // Earnings Analytics Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF006D44), Color(0xFF0F766E))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تقدير عوائد الإعلانات المباشرة",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "${String.format(Locale.US, "%.2f", adStats.estimatedEarningsSar)} ر.س",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${String.format(Locale.US, "%.2f", adStats.estimatedEarningsUsd)} USD)",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricSubItem("مشاهدات البانر", "${adStats.bannerImpressions}")
                            MetricSubItem("إعلانات بينية", "${adStats.interstitialImpressions}")
                            MetricSubItem("فيديو بمكافأة", "${adStats.rewardedImpressions}")
                            MetricSubItem("النقرات", "${adStats.clicks}")
                        }
                    }
                }
            }
        }

        // Interactive Ad Testing Action
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تجربة الإعلانات المدمجة في التطبيق",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "يمكنك اختبار الإعلان البيني (Interstitial) وفيديو المكافأة (Rewarded) للتأكد من سلاسة العرض",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (activity != null) {
                                    viewModel.triggerInterstitialTest(activity)
                                    Toast.makeText(context, "جاري فتح الإعلان البيني...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_interstitial_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعلان بيني", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (activity != null) {
                                    viewModel.triggerRewardedTest(activity) {
                                        Toast.makeText(context, "🎉 مبروك! حصلت على 50 نقطة واشتراك VIP", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_rewarded_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            )
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فيديو بمكافأة 💰", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Locked & Verified Developer AdMob IDs (Tamper-Proof)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E17)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "معرفات الأرباح مدمجة ومحمية بصفة دائمة",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "مشفرة ضد التعديل لحفظ جميع أرباح الزوار لحسابك الشخصي حصراً",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    IdRow(
                        label = "معرف تطبيق AdMob (App ID) [محمي ومدمج]:",
                        value = AdMobManager.USER_APP_ID,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(AdMobManager.USER_APP_ID))
                            Toast.makeText(context, "تم نسخ معرف التطبيق", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IdRow(
                        label = "معرف إعلان البانر (Banner ID) [نشط ومدمج]:",
                        value = AdMobManager.USER_BANNER_ID,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(AdMobManager.USER_BANNER_ID))
                            Toast.makeText(context, "تم نسخ معرف البانر", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IdRow(
                        label = "معرف الإعلان البيني (Interstitial ID) [نشط ومدمج]:",
                        value = AdMobManager.USER_INTERSTITIAL_ID,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(AdMobManager.USER_INTERSTITIAL_ID))
                            Toast.makeText(context, "تم نسخ معرف الإعلان البيني", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IdRow(
                        label = "معرف إعلان المكافأة (Rewarded Video ID) [نشط ومدمج]:",
                        value = AdMobManager.USER_REWARDED_ID,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(AdMobManager.USER_REWARDED_ID))
                            Toast.makeText(context, "تم نسخ معرف إعلان المكافأة", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Checklist for Maximizing Profit
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "خطة تحقيق الأرباح وأمان حسابك",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfitChecklistItem(
                        number = "1",
                        title = "القاعدة الذهبية: الاختبار الآمن (Test Ads)",
                        desc = "التطبيق مضبوط تلقائياً على 'وضع الاختبار الآمن' أثناء التطوير والتجربة. هذا يحمي حسابك من تقييد جوجل بسبب النقرات الذاتية غير الصالحة."
                    )

                    ProfitChecklistItem(
                        number = "2",
                        title = "تقديم قيمة حقيقية تدفع المستخدم للعودة يومياً",
                        desc = "التطبيق يوفر إدارة متكاملة للمهام، والمواعيد، والمالية، ونظام مكافآت VIP لتحفيز المستخدمين على التفاعل اليومي وزيادة ظهور الإعلانات العضوية."
                    )

                    ProfitChecklistItem(
                        number = "3",
                        title = "نشر التطبيق على GitHub و Google Play",
                        desc = "التطبيق جاهز للنشر العام. المعرفات مدمجة ومغلقة في الكود بحيث تذهب 100% من عوائد الزوار إلى رصيدك الشخصي مباشرة."
                    )

                    ProfitChecklistItem(
                        number = "4",
                        title = "اكتمال الثلاثي الإعلاني (بانر + بيني + مكافأة)",
                        desc = "تم بنجاح ربط جميع المعرفات (Banner, Interstitial, Rewarded) لتحقيق أقصى عائد ممكن eCPM لكل 1000 ظهور."
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricSubItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun IdRow(label: String, value: String, onCopy: () -> Unit) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "نسخ",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfitChecklistItem(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
