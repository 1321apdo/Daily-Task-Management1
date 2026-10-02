package com.example.ui.rewards

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlowButton
import com.example.ui.components.GlowFloatingCard
import com.example.ui.components.NeonEmerald
import com.example.ui.components.NeonGold

@Composable
fun FloatingRewardPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by RewardGameManager.state.collectAsStateWithLifecycle()
    val infiniteTransition = rememberInfiniteTransition(label = "pill_glow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pill_pulse"
    )

    Surface(
        modifier = modifier
            .scale(pulse)
            .shadow(12.dp, CircleShape, spotColor = NeonGold, ambientColor = NeonGold)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("floating_reward_pill"),
        color = Color(0xFF15221B),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.sweepGradient(listOf(NeonGold, NeonEmerald, NeonGold))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = "النقاط",
                tint = NeonGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "${state.points} نقطة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "اضغط واكسب",
                    color = NeonEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RewardGameDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val gameState by RewardGameManager.state.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }
    val isCoinPressed by interactionSource.collectIsPressedAsState()
    val coinScale by animateFloatAsState(
        targetValue = if (isCoinPressed) 0.85f else 1f,
        label = "coin_press"
    )

    var floatingTextVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "لعبة تجميع نقاط الأرباح",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = gameState.levelName,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonEmerald
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Streak & Multiplier Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1C2B22))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "حماسة النقرات: ${gameState.streak}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonGold)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${gameState.multiplier}x مضاعف",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // The Big Luminous Tappable Coin / Gem
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(coinScale)
                        .shadow(
                            elevation = if (isCoinPressed) 26.dp else 16.dp,
                            shape = CircleShape,
                            spotColor = NeonEmerald,
                            ambientColor = NeonGold
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonGold, Color(0xFFC88A00), Color(0xFF006D44))
                            )
                        )
                        .border(
                            3.dp,
                            Brush.sweepGradient(listOf(NeonEmerald, NeonGold, NeonEmerald)),
                            CircleShape
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            RewardGameManager.tapToEarn(activity)
                            floatingTextVisible = true
                        }
                        .testTag("tap_coin_target"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "انقر لربح النقاط",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "اضغط واكسب!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "رصيدك الحالي: ${gameState.points} نقطة",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mystery Chest Progress
                val chestProgress = (15 - gameState.nextChestTapsRemaining).coerceAtLeast(0) / 15f
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "صندوق الكنز القادم",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "باقي ${gameState.nextChestTapsRemaining} نقرات",
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { chestProgress },
                        color = NeonGold,
                        trackColor = Color(0xFF263A2F),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }

                if (gameState.nextChestTapsRemaining <= 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GlowButton(
                        onClick = { RewardGameManager.openMysteryChest(activity) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "افتح صندوق الكنز الآن (+250 نقطة!)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Watch Ad for 3x Bonus
                Button(
                    onClick = {
                        if (activity != null) {
                            RewardGameManager.watchAdForBonus(activity)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("watch_ad_bonus_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC88A00)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مضاعفة النقاط x3 بمشاهدة إعلان (+500)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A2D))
            ) {
                Text("متابعة الأعمال", color = Color.White)
            }
        },
        containerColor = Color(0xFF111E18),
        shape = RoundedCornerShape(22.dp)
    )
}
