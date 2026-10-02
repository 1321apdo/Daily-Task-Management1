package com.example.ui.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Glowing color tokens
val NeonEmerald = Color(0xFF00FF87)
val DeepEmerald = Color(0xFF006D44)
val NeonGold = Color(0xFFFFD700)
val GlowAmber = Color(0xFFFFA500)
val DarkAtmosphere = Color(0xFF0A120F)
val DarkSurfaceGlass = Color(0xFF13201A).copy(alpha = 0.88f)

/**
 * Ambient Glow Background (الجدار الملائم للإطارات العائمة المتوهجة)
 * Creates deep atmospheric radial gradients behind cards.
 */
@Composable
fun AmbientGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkAtmosphere)
            .drawBehind {
                if (size.width > 20f && size.height > 20f) {
                    val p = pulse.coerceIn(0.5f, 2.0f)
                    // Top-right emerald ambient orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonEmerald.copy(alpha = 0.14f * p),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.9f, size.height * 0.15f),
                            radius = maxOf(20f, size.width * 0.7f * p)
                        )
                    )
                    // Center-left gold subtle orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonGold.copy(alpha = 0.08f * p),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.1f, size.height * 0.5f),
                            radius = maxOf(20f, size.width * 0.6f)
                        )
                    )
                    // Bottom-right emerald soft light
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                DeepEmerald.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.8f, size.height * 0.85f),
                            radius = maxOf(20f, size.width * 0.8f)
                        )
                    )
                }
            },
        content = content
    )
}

/**
 * Floating Glowing Card (إطار عائم متوهج أنيق)
 * Features glowing borders, rounded floating depth, and glassmorphic surface.
 */
@Composable
fun GlowFloatingCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeonEmerald,
    secondaryGlowColor: Color = NeonGold,
    shape: Shape = RoundedCornerShape(18.dp),
    borderWidth: Dp = 1.2.dp,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "card_press_scale"
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            glowColor.copy(alpha = if (isPressed) 0.9f else 0.55f),
            secondaryGlowColor.copy(alpha = if (isPressed) 0.7f else 0.25f),
            Color.Transparent,
            glowColor.copy(alpha = 0.35f)
        )
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.4f),
                spotColor = glowColor.copy(alpha = 0.6f)
            )
            .clip(shape)
            .background(DarkSurfaceGlass)
            .border(borderWidth, borderBrush, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        content = content
    )
}

/**
 * Illuminated Button (زر بإضاءة وهالة عند الضغط)
 */
@Composable
fun GlowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = NeonEmerald,
    shape: Shape = RoundedCornerShape(14.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "btn_scale"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 0.35f,
        label = "btn_glow"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 14.dp else 6.dp,
                shape = shape,
                spotColor = glowColor.copy(alpha = glowAlpha),
                ambientColor = glowColor.copy(alpha = glowAlpha)
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        DeepEmerald,
                        if (isPressed) NeonEmerald else Color(0xFF009156)
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        glowColor.copy(alpha = if (isPressed) 1f else 0.6f),
                        NeonGold.copy(alpha = if (isPressed) 0.8f else 0.3f)
                    )
                ),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
