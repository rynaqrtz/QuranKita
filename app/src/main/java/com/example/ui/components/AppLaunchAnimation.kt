package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldMint

/**
 * Minimal, High-End Launch Loading Animation Component.
 *
 * Characteristics:
 * - Fluid kinetic motion (breathing elevation, rotating ambient emerald orbit arc, glowing aura).
 * - 100% borderless clean branding using the official Tixar photo.
 * - Subtle, deliberate micro-interactions: no harsh boxes, no generic spinners.
 */
@Composable
fun AppLaunchLoadingView(
    modifier: Modifier = Modifier,
    statusText: String = "Memuat Al-Qur'an & Jadwal...",
    logoSize: Dp = 64.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "launch_motion")

    // 1. Subtle breathing scale for natural organic feel (0.97f .. 1.03f)
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // 2. Ambient radial emerald aura breathing
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // 3. High-end kinetic orbit rotation (0 -> 360 deg)
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotation"
    )

    // 4. Ultra-minimal horizontal light beam sweep
    val beamProgress by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beam_progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("app_launch_loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // High-End Animated Logo Core
            Box(
                modifier = Modifier
                    .size(logoSize + 48.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Emerald Ambient Aura (soft radial gradient)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = breathingScale * 1.08f
                            scaleY = breathingScale * 1.08f
                        }
                ) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EmeraldGreen.copy(alpha = auraAlpha * 0.45f),
                                EmeraldMint.copy(alpha = auraAlpha * 0.18f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }

                // Minimalist Kinetic Emerald Arc (Smooth single-line orbit ring, no harsh container)
                Canvas(
                    modifier = Modifier
                        .size(logoSize + 24.dp)
                        .graphicsLayer {
                            rotationZ = orbitRotation
                        }
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val arcSize = size.minDimension - strokeWidth
                    val topLeftOffset = Offset(strokeWidth / 2f, strokeWidth / 2f)

                    // Delicate primary sweep arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                EmeraldGreen.copy(alpha = 0.2f),
                                EmeraldMint,
                                EmeraldGreen,
                                Color.Transparent
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = topLeftOffset,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Counter-point accent dot on the orbit
                    val dotRadius = 2.5.dp.toPx()
                    drawCircle(
                        color = EmeraldMint,
                        radius = dotRadius,
                        center = Offset(size.width - strokeWidth / 2f, size.height / 2f)
                    )
                }

                // Clean Modern Minimalist Quran Logo Icon
                Image(
                    painter = painterResource(id = R.drawable.quran_logo_clean),
                    contentDescription = "QuranKita Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(logoSize)
                        .scale(breathingScale)
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Title Typography (Clean Inter & Emerald Accent)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Quran",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Kita",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Minimalist Brand Byline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(EmeraldMint)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "by Tixar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // High-End Minimalist Horizontal Light Beam (Instead of clunky spinner)
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val beamWidth = w * 0.45f
                    val startX = (beamProgress * (w + beamWidth)) - beamWidth

                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                EmeraldGreen,
                                EmeraldMint,
                                Color.Transparent
                            ),
                            startX = startX,
                            endX = startX + beamWidth
                        ),
                        size = Size(beamWidth, size.height),
                        topLeft = Offset(startX, 0f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtle Status Text
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    letterSpacing = 0.2.sp
                ),
                color = Color.White.copy(alpha = 0.55f)
            )
        }
    }
}

/**
 * Screen wrapper with smooth fade transitions for launch animation.
 */
@Composable
fun AppLaunchLoadingScreen(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    statusText: String = "Memuat Al-Qur'an & Jadwal...",
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(600, easing = FastOutSlowInEasing))
        ) {
            AppLaunchLoadingView(statusText = statusText)
        }
    }
}
