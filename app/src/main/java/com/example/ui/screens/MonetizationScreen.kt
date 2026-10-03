package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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

    var isLiveMode by remember { mutableStateOf(!adStats.isTestMode) }
    var bannerIdInput by remember { mutableStateOf(adStats.customBannerId.ifBlank { AdMobManager.USER_BANNER_ID }) }
    var appIdInput by remember { mutableStateOf(adStats.customAppId.ifBlank { AdMobManager.USER_APP_ID }) }
    var interstitialIdInput by remember { mutableStateOf(adStats.customInterstitialId.ifBlank { AdMobManager.USER_INTERSTITIAL_ID }) }
    var rewardedIdInput by remember { mutableStateOf(adStats.customRewardedId.ifBlank { AdMobManager.USER_REWARDED_ID }) }

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
                    text = "مركز أرباح إعلانات Google AdMob",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "تم ربط معرفاتك الرسمية بنجاح لبدء تحقيق أقصى عائد مالي من التطبيق",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA5B8AD)
                )
            }
        }

        // Active Banner on Monetization Page
        item {
            AdBannerView()
        }

        // Verified Account Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "معرفات AdMob الخاصة بك مدمجة ونشطة",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF14532D)
                            )
                        )
                        Text(
                            text = "رقم التطبيق والوحدة الإعلانية للبانر تم تفعيلهم في الكود والمانيفست",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF166534)
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
                                text = "تقدير أرباح الإعلانات",
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
                        text = "يمكنك اختبار الإعلان البيني (Interstitial) وفيديو المكافأة (Rewarded) للتأكد من سلاسة العرض واحتساب الأرباح",
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

        // Your Configured IDs Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المعرفات المربوطة حالياً في الكود",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    IdRow(
                        label = "معرف تطبيق AdMob (App ID):",
                        value = appIdInput,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(appIdInput))
                            Toast.makeText(context, "تم نسخ معرف التطبيق", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IdRow(
                        label = "معرف وحدة البانر (Banner ID):",
                        value = bannerIdInput,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(bannerIdInput))
                            Toast.makeText(context, "تم نسخ معرف وحدة البانر", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = interstitialIdInput,
                        onValueChange = { interstitialIdInput = it },
                        label = { Text("معرف وحدة الإعلان البيني (اختياري)") },
                        placeholder = { Text("ca-app-pub-8214981197698574/4793744758") },
                        supportingText = {
                            Text("يظهر بملء الشاشة بين التنقلات الرئيسية")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rewardedIdInput,
                        onValueChange = { rewardedIdInput = it },
                        label = { Text("معرف إعلان بمكافأة Rewarded (الأعلى ربحاً)") },
                        placeholder = { Text("ca-app-pub-8214981197698574/xxxxxxx") },
                        supportingText = {
                            Text("أنشئه من خيار 'إنشاء وحدة إعلانية أخرى' -> 'إعلان بمكافأة'")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.updateAdMobConfig(
                                isTestMode = !isLiveMode,
                                customBannerId = bannerIdInput.trim(),
                                customInterstitialId = interstitialIdInput.trim(),
                                customRewardedId = rewardedIdInput.trim(),
                                customAppId = appIdInput.trim()
                            )
                            Toast.makeText(context, "تم حفظ إعدادات المعرفات بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ التغييرات")
                    }
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
                            text = "خطة تحقيق الأرباح وخطواتك القادمة",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfitChecklistItem(
                        number = "1",
                        title = "فترة التفعيل الأولية (ساعة واحدة)",
                        desc = "كما ظهر في لقطة شاشتك، الوحدات الإعلانية الجديدة في أدموب تستغرق ما بين 30 إلى 60 دقيقة لتبدأ في تقديم الإعلانات الحية."
                    )

                    ProfitChecklistItem(
                        number = "2",
                        title = "إضافة وحدة إعلانية بينية (Interstitial)",
                        desc = "اضغط على زر 'إنشاء وحدة إعلانية أخرى' في حسابك واختر 'بيني (Interstitial)'، فالإعلانات البينية تحقق أرباحاً أعلى بـ 4 إلى 8 أضعاف البانر العادي."
                    )

                    ProfitChecklistItem(
                        number = "3",
                        title = "نشر التطبيق على Google Play",
                        desc = "بمجرد طرح التطبيق على متجر بلاي، اربط التطبيق من خيار 'جميع التطبيقات' في أدموب لفتح سقف عوائد eCPM والبدء في استلام أرباحك البنكية."
                    )

                    ProfitChecklistItem(
                        number = "4",
                        title = "عدم النقر على إعلاناتك بنفسك",
                        desc = "احرص على عدم النقر الذاتي على الإعلانات لتفادي إغلاق حسابك بواسطة خوارزميات جوجل لحماية حركة المرور."
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
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1
            )
            IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
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
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
