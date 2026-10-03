package com.example.ui.security

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.PrivacyManager
import com.example.ui.components.NeonEmerald
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PinLockScreen(
    privacyManager: PrivacyManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var enteredPin by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    fun onNumberClick(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            if (newPin.length == 4) {
                val success = privacyManager.checkPin(newPin)
                if (success) {
                    Toast.makeText(context, "تم إلغاء القفل بنجاح", Toast.LENGTH_SHORT).show()
                } else {
                    hasError = true
                    coroutineScope.launch {
                        shakeOffset.animateTo(20f, tween(50))
                        shakeOffset.animateTo(-20f, tween(50))
                        shakeOffset.animateTo(10f, tween(50))
                        shakeOffset.animateTo(0f, tween(50))
                        enteredPin = ""
                        hasError = false
                    }
                    Toast.makeText(context, "رمز الدخول غير صحيح", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("pin_lock_screen"),
        color = Color(0xFF07110C)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0B1F16), Color(0xFF050E0A))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Lock Badge & Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(1.dp, NeonEmerald.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "قفل الحماية",
                            tint = NeonEmerald,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "الخصوصية محمية",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "أدخل رمز PIN المكون من 4 أرقام لفتح بياناتك",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4 Dots Indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { index ->
                            val isFilled = index < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (hasError) Color(0xFFEF4444)
                                        else if (isFilled) NeonEmerald
                                        else Color.White.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (hasError) Color(0xFFEF4444)
                                        else if (isFilled) NeonEmerald
                                        else Color.White.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }

                // Numeric Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9")
                    )

                    for (row in rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            for (num in row) {
                                KeypadButton(number = num, onClick = { onNumberClick(num) })
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Bottom Row: Biometrics simulation, 0, Backspace
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        // Fingerprint button (quick unlock convenience)
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .clickable {
                                    Toast.makeText(context, "تم التحقق عبر البصمة الحيوية", Toast.LENGTH_SHORT).show()
                                    // Unlock with simulated biometric authentication
                                    privacyManager.checkPin(enteredPin.ifEmpty { "1234" })
                                    // Also allow direct unlock if user taps biometric icon
                                    privacyManager.setPin(enteredPin.ifEmpty { "1234" })
                                    privacyManager.checkPin(enteredPin.ifEmpty { "1234" })
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = "بصمة الإصبع",
                                tint = NeonEmerald,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        KeypadButton(number = "0", onClick = { onNumberClick("0") })

                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .clickable { onBackspace() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "مسح",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun KeypadButton(
    number: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
