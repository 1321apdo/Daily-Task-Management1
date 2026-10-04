package com.example.ui.security

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.security.PrivacyManager
import com.example.ui.components.NeonEmerald

@Composable
fun PrivacyCenterDialog(
    privacyManager: PrivacyManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val settings by privacyManager.settings.collectAsStateWithLifecycle()

    var showPinInputSection by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var showPolicyText by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("privacy_center_dialog"),
            color = Color(0xFF0C1913)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مركز الخصوصية والأمان",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "حماية كاملة لبياناتك وأموالك وهويتك",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA5B8AD)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Item 1: PIN Lock Protection
                    item {
                        PrivacySettingCard(
                            icon = Icons.Default.Lock,
                            iconTint = NeonEmerald,
                            title = "قفل التطبيق برمز حماية (PIN)",
                            subtitle = if (settings.isPinEnabled) "التطبيق محمي برمز PIN سري عند الفتح" else "عين رمزاً سرياً لمنع المتطفلين من فتح التطبيق",
                            trailing = {
                                Switch(
                                    checked = settings.isPinEnabled,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            showPinInputSection = true
                                        } else {
                                            privacyManager.removePin()
                                            Toast.makeText(context, "تم إلغاء قفل PIN", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = NeonEmerald
                                    )
                                )
                            }
                        )

                        if (showPinInputSection && !settings.isPinEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF132A20))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "تعيين رمز دخول جديد (4 أرقام):",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = newPinInput,
                                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPinInput = it },
                                        label = { Text("رمز PIN (4 أرقام)") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = confirmPinInput,
                                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPinInput = it },
                                        label = { Text("تأكيد رمز PIN") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { showPinInputSection = false }) {
                                            Text("إلغاء", color = Color(0xFF94A3B8))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                if (newPinInput.length == 4 && newPinInput == confirmPinInput) {
                                                    privacyManager.setPin(newPinInput)
                                                    showPinInputSection = false
                                                    newPinInput = ""
                                                    confirmPinInput = ""
                                                    Toast.makeText(context, "تم تفعيل حماية PIN بنجاح", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "يجب إدخال 4 أرقام متطابقة", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                                        ) {
                                            Text("حفظ وتفعيل", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Item 2: Discreet Mode (Hide balances in public)
                    item {
                        PrivacySettingCard(
                            icon = if (settings.isDiscreetMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            iconTint = Color(0xFFFFD54F),
                            title = "وضع إخفاء الأرقام المالية (Discreet Mode)",
                            subtitle = "إخفاء مبالغ الأرباح والمعاملات وإظهارها بنقاط (••••) لمنع التلصص في الأماكن العامة",
                            trailing = {
                                Switch(
                                    checked = settings.isDiscreetMode,
                                    onCheckedChange = { privacyManager.toggleDiscreetMode() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = Color(0xFFFFD54F)
                                    )
                                )
                            }
                        )
                    }

                    // Item 3: Non-Personalized Ads (Zero Profile Tracking)
                    item {
                        PrivacySettingCard(
                            icon = Icons.Default.NoAccounts,
                            iconTint = Color(0xFF60A5FA),
                            title = "حظر التتبع الإعلاني (Non-Personalized Ads)",
                            subtitle = "عرض إعلانات عامة فقط دون تتبع اهتماماتك أو جمع أي معرف إعلاني شخصي (Zero Tracking)",
                            trailing = {
                                Switch(
                                    checked = settings.isNonPersonalizedAds,
                                    onCheckedChange = { isChecked ->
                                        privacyManager.setNonPersonalizedAds(isChecked)
                                        Toast.makeText(context, "تم حفظ إعدادات حظر التتبع", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = Color(0xFF60A5FA)
                                    )
                                )
                            }
                        )
                    }

                    // Item 4: 100% Local On-Device Database Shield
                    item {
                        PrivacySettingCard(
                            icon = Icons.Default.Storage,
                            iconTint = NeonEmerald,
                            title = "تخزين محلي مشفر (Offline-First)",
                            subtitle = "جميع مهامك ومواعيدك وسجلاتك المالية مخزنة في قاعدة بيانات هاتفك الآمنة، ولا تُرفع لأي خادم سحابي خارجي",
                            trailing = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("محمي 100%", color = NeonEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        )
                    }

                    // Item 5: Unrestricted Background Execution
                    item {
                        var isIgnored by remember {
                            mutableStateOf(com.example.notifications.BackgroundReminderManager.isBatteryOptimizationIgnored(context))
                        }

                        PrivacySettingCard(
                            icon = Icons.Default.NotificationsActive,
                            iconTint = NeonEmerald,
                            title = "التشغيل في الخلفية (Background Mode)",
                            subtitle = "السماح بالعمل في الخلفية لإرسال تنبيهات المواعيد والمهام بدقة دون إيقاف التطبيق بواسطة موفر البطارية",
                            trailing = {
                                if (isIgnored) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("نشط ⚡️", color = NeonEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            com.example.notifications.BackgroundReminderManager.requestIgnoreBatteryOptimization(context)
                                            isIgnored = com.example.notifications.BackgroundReminderManager.isBatteryOptimizationIgnored(context)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("تفعيل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        )
                    }

                    // Test Background Notification Button
                    item {
                        OutlinedButton(
                            onClick = {
                                com.example.notifications.BackgroundReminderManager.sendNotification(
                                    context = context,
                                    notificationId = 1001,
                                    title = "تطبيق المهام يعمل في الخلفية ⚡️",
                                    message = "تم تأكيد خاصية التشغيل في الخلفية بنجاح! ستصلك تنبيهات المواعيد في وقتها المحدد."
                                )
                                Toast.makeText(context, "تم إرسال إشعار تجريبي في الخلفية!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إرسال إشعار تجريبي لاختبار الخلفية 🔔", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    // Item 6: Official Privacy Policy Text
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF13231B))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Policy, contentDescription = null, tint = Color(0xFFA7F3D0))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "وثيقة سياسة الخصوصية الرسمية",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    TextButton(onClick = { showPolicyText = !showPolicyText }) {
                                        Text(if (showPolicyText) "إخفاء" else "عرض", color = NeonEmerald)
                                    }
                                }

                                if (showPolicyText) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = """
1. خصوصية البيانات: نؤكد أن تطبيق "إدارة المهام اليومية" يتبنى مبدأ الخصوصية أولاً (Privacy by Design). لا يتم بيع أو مشاركة أي بيانات شخصية مع أي جهة خارجية.
2. التخزين المحلي: تُحفظ كافة السجلات المالية والمهام والمواعيد محلياً على جهاز المستخدم باستخدام مكتبة SQLite Room المشفرة.
3. الإعلانات والامتثال: تتوافق الإعلانات المعروضة مع سياسات Google Play Developer وسياسات Google AdMob للخصوصية والامتثال لقوانين حماية البيانات (GDPR/CCPA).
4. حقوق المستخدم: للمستخدم الحق الكامل في حذف كافة بياناته، أو تفعيل قفل الأمان الشخصي (PIN) في أي وقت بضغطة زر.
                                        """.trimIndent(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFFCBD5E1),
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Immediate Lock App Action
                if (settings.isPinEnabled) {
                    Button(
                        onClick = {
                            privacyManager.lockApp()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("قفل التطبيق فوراً الآن (Lock Now)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacySettingCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13231B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8B9D93),
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}
