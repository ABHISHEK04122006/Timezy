package com.timezy.widget.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.ui.theme.GoldPrimaryAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium Liquid-Glass Animated Splash Screen for Countdown - Widget & Reminders.
 *
 * Sequence:
 * - Phase 1 (0–500 ms): Clock fades in from 94% to 100% scale on a pure black background.
 * - Phase 2 (500–1800 ms): Hands accelerate smoothly into mechanical rotation with
 *                         realistic 3D metallic facets, golden light sweep, and pivot glow.
 * - Phase 3 (1200–2200 ms): Brand reveal ("Countdown", "Widget & Reminders") with clean typography.
 * - Phase 4 (~2500–2700 ms): Seamless exit transition to main application.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ----------------------------------------------------
    // Animation Controllers
    // ----------------------------------------------------
    val clockAlpha = remember { Animatable(0f) }
    val clockScale = remember { Animatable(0.94f) }

    // Hands rotation angles (0° = 12 o'clock, clockwise)
    val hourAngle = remember { Animatable(298f) } // ~10 o'clock
    val minuteAngle = remember { Animatable(48f) } // ~10 minutes
    val secondAngle = remember { Animatable(160f) } // ~32 seconds

    // Liquid-glass effect controllers
    val rimShineAngle = remember { Animatable(0f) }
    val lightSweepProgress = remember { Animatable(-0.4f) }
    val pivotGlow = remember { Animatable(0f) }

    // Brand reveal controllers
    val brandAlpha = remember { Animatable(0f) }
    val brandOffsetY = remember { Animatable(14f) }

    // Overall screen exit alpha
    val screenAlpha = remember { Animatable(1f) }

    // Ambient background gentle breathing glow
    val ambientTransition = rememberInfiniteTransition(label = "AmbientPulse")
    val ambientPulse by ambientTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    // Master Orchestration
    LaunchedEffect(Unit) {
        // Phase 1: Clock appearance (0–500 ms)
        launch {
            clockAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            clockScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 650,
                    easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
                )
            )
        }

        // Rim reflection traversal (350–2400 ms)
        launch {
            delay(350)
            rimShineAngle.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 2050, easing = LinearEasing)
            )
        }

        // Phase 2: Clock activation & hands movement (500–2400 ms)
        launch {
            delay(480)

            // Center pivot glow
            launch {
                pivotGlow.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                )
            }

            // Hour hand: smooth horological progression (+26°)
            launch {
                hourAngle.animateTo(
                    targetValue = 324f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = CubicBezierEasing(0.2f, 0.0f, 0.25f, 1.0f)
                    )
                )
            }

            // Minute hand: continuous moderate speed (+120°)
            launch {
                minuteAngle.animateTo(
                    targetValue = 172f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = CubicBezierEasing(0.18f, 0.0f, 0.22f, 1.0f)
                    )
                )
            }

            // Second hand: smooth continuous sweeping rotation (+450° / 1.25 full circles)
            launch {
                secondAngle.animateTo(
                    targetValue = 610f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = CubicBezierEasing(0.15f, 0.0f, 0.2f, 1.0f)
                    )
                )
            }
        }

        // Golden light sweep across the sapphire crystal dome (650–1850 ms)
        launch {
            delay(650)
            lightSweepProgress.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
            )
        }

        // Phase 3: Brand reveal (1200–2200 ms)
        launch {
            delay(1200)
            launch {
                brandAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
                )
            }
            launch {
                brandOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                )
            }
        }

        // Phase 4: Smooth exit transition (~2500–2700 ms)
        launch {
            delay(2550)
            screenAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
            )
            onSplashFinished()
        }
    }

    // Interaction source for tap-to-skip (instant access if user is ready)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Pure black background
            .alpha(screenAlpha.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                // If brand has started revealing, allow smooth instant skip
                if (brandAlpha.value > 0.4f) {
                    onSplashFinished()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ambient warm golden glow behind the clock
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(clockScale.value)
                    .alpha(clockAlpha.value)
            ) {
                // Soft golden radial bloom
                Canvas(modifier = Modifier.size(280.dp)) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x38F5C77E).copy(alpha = 0.35f * ambientPulse),
                                Color(0x18D4AF37).copy(alpha = 0.18f * ambientPulse),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }

                // The Golden Liquid-Glass Mechanical Clock
                LiquidGlassClockCanvas(
                    size = 210.dp,
                    hourAngle = hourAngle.value,
                    minuteAngle = minuteAngle.value,
                    secondAngle = secondAngle.value,
                    rimShineAngle = rimShineAngle.value,
                    lightSweepProgress = lightSweepProgress.value,
                    pivotGlowAlpha = pivotGlow.value
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Brand Reveal Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = brandOffsetY.value.dp)
                    .alpha(brandAlpha.value)
            ) {
                Text(
                    text = "Timezy",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.4).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "WIDGET & REMINDERS",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldPrimaryAccent.copy(alpha = 0.9f),
                    letterSpacing = 2.8.sp
                )
            }
        }
    }
}

/**
 * Custom-drawn luxury mechanical clock with 3D faceted metallic hands,
 * sapphire crystal light sweep, golden rim reflection, and engraved indices.
 */
@Composable
private fun LiquidGlassClockCanvas(
    size: Dp,
    hourAngle: Float,
    minuteAngle: Float,
    secondAngle: Float,
    rimShineAngle: Float,
    lightSweepProgress: Float,
    pivotGlowAlpha: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = this.center

        // ==========================================
        // 1. Outer Metallic Champagne-Gold Bezel
        // ==========================================
        val bezelThickness = radius * 0.075f
        val outerRadius = radius - bezelThickness / 2f

        // Rich metallic sweep gradient
        val goldBezelBrush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFFFFE8B5), // Specular light gold
                Color(0xFFD4AF37), // Classic rich gold
                Color(0xFF8A651E), // Shadow bronze
                Color(0xFFF5C77E), // Champagne highlight
                Color(0xFFE5BC6E), // Warm gold
                Color(0xFF785515), // Deep bevel shadow
                Color(0xFFFFE8B5)  // Specular light gold loop
            ),
            center = center
        )

        drawCircle(
            brush = goldBezelBrush,
            radius = outerRadius,
            center = center,
            style = Stroke(width = bezelThickness)
        )

        // Traveling rim specular reflection
        val shineRad = Math.toRadians(rimShineAngle.toDouble())
        val shineCenter = Offset(
            x = center.x + (outerRadius * cos(shineRad)).toFloat(),
            y = center.y + (outerRadius * sin(shineRad)).toFloat()
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color(0xFFFFE8B5).copy(alpha = 0.5f),
                    Color.Transparent
                ),
                center = shineCenter,
                radius = radius * 0.28f
            ),
            radius = radius * 0.28f,
            center = shineCenter
        )

        // ==========================================
        // 2. Obsidian Dial Face with Guilloché Rings
        // ==========================================
        val dialRadius = radius * 0.91f
        val dialPath = Path().apply {
            addOval(Rect(center.x - dialRadius, center.y - dialRadius, center.x + dialRadius, center.y + dialRadius))
        }

        // Deep obsidian-to-midnight dial gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1D1711), // Subtle warm obsidian center
                    Color(0xFF0F0E13), // Deep charcoal
                    Color(0xFF050508)  // Pure deep edge
                ),
                center = center,
                radius = dialRadius
            ),
            radius = dialRadius,
            center = center
        )

        // Inner bevel groove shadow for realistic depth
        drawCircle(
            color = Color(0x99000000),
            radius = dialRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Concentric micro-tracks (Horology guilloché lines)
        val ringColor = Color(0x1AF5C77E)
        drawCircle(color = ringColor, radius = dialRadius * 0.72f, center = center, style = Stroke(0.8.dp.toPx()))
        drawCircle(color = ringColor, radius = dialRadius * 0.50f, center = center, style = Stroke(0.6.dp.toPx()))
        drawCircle(color = ringColor, radius = dialRadius * 0.32f, center = center, style = Stroke(0.5.dp.toPx()))

        // ==========================================
        // 3. Faceted Gold Hour Indices & Minute Track
        // ==========================================
        drawClockIndices(center, dialRadius)

        // ==========================================
        // 4. Liquid-Glass Caustics & Light Sweep
        // ==========================================
        clipPath(dialPath) {
            // Liquid golden wave caustics in lower-right quadrant (matching app icon)
            val wavePath = Path().apply {
                moveTo(center.x - dialRadius * 0.35f, center.y + dialRadius * 0.85f)
                cubicTo(
                    center.x + dialRadius * 0.1f, center.y + dialRadius * 0.5f,
                    center.x + dialRadius * 0.45f, center.y + dialRadius * 0.65f,
                    center.x + dialRadius * 0.88f, center.y + dialRadius * 0.12f
                )
                cubicTo(
                    center.x + dialRadius * 0.75f, center.y + dialRadius * 0.4f,
                    center.x + dialRadius * 0.35f, center.y + dialRadius * 0.75f,
                    center.x - dialRadius * 0.2f, center.y + dialRadius * 0.95f
                )
                close()
            }
            drawPath(
                path = wavePath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x60F5C77E),
                        Color(0x30D4AF37),
                        Color.Transparent
                    ),
                    center = Offset(center.x + dialRadius * 0.45f, center.y + dialRadius * 0.5f),
                    radius = dialRadius * 0.7f
                )
            )

            // Upper curved crystal highlight (crescent lens refraction)
            val causticHeight = dialRadius * 0.75f
            drawOval(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color(0x05FFFFFF),
                        Color.Transparent
                    ),
                    startY = center.y - dialRadius,
                    endY = center.y - dialRadius + causticHeight
                ),
                topLeft = Offset(center.x - dialRadius * 0.9f, center.y - dialRadius),
                size = Size(dialRadius * 1.8f, causticHeight)
            )

            // Dynamic golden light sweep across the glass surface
            if (lightSweepProgress in -0.3f..1.3f) {
                val sweepX = center.x + (dialRadius * 2.4f * (lightSweepProgress - 0.5f))
                val sweepBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x18F5C77E),
                        Color(0x40FFFFFF),
                        Color(0x20F5C77E),
                        Color.Transparent
                    ),
                    start = Offset(sweepX - 40.dp.toPx(), center.y - dialRadius),
                    end = Offset(sweepX + 40.dp.toPx(), center.y + dialRadius)
                )
                drawRect(
                    brush = sweepBrush,
                    topLeft = Offset(center.x - dialRadius, center.y - dialRadius),
                    size = Size(dialRadius * 2f, dialRadius * 2f)
                )
            }
        }

        // ==========================================
        // 5. Independently Animated Hands
        // ==========================================

        // A. Hour Hand (Rounded Capsule Metallic Hand)
        withTransform({
            rotate(degrees = hourAngle, pivot = center)
        }) {
            drawCapsuleHand(
                center = center,
                length = dialRadius * 0.48f,
                width = 6.5.dp.toPx(),
                tailLength = 6.dp.toPx(),
                lightColor = Color(0xFFFFEEBA),
                shadowColor = Color(0xFFC49735)
            )
        }

        // B. Minute Hand (Longer Rounded Capsule Hand)
        withTransform({
            rotate(degrees = minuteAngle, pivot = center)
        }) {
            drawCapsuleHand(
                center = center,
                length = dialRadius * 0.74f,
                width = 5.6.dp.toPx(),
                tailLength = 8.dp.toPx(),
                lightColor = Color(0xFFFFF2D0),
                shadowColor = Color(0xFFD4AF37)
            )
        }

        // C. Second Hand (Hairline Polished Needle with Counterbalance)
        withTransform({
            rotate(degrees = secondAngle, pivot = center)
        }) {
            drawSecondHand(
                center = center,
                length = dialRadius * 0.85f,
                tailLength = dialRadius * 0.22f
            )
        }

        // ==========================================
        // 6. Central Pivot Cap & Jewel
        // ==========================================
        drawCenterPivot(center, pivotGlowAlpha)
    }
}

/**
 * Draws rounded pill markers at 12, 3, 6, 9 and gold dots at 1, 2, 4, 5, 7, 8, 10, 11 (matching the clock logo).
 */
private fun DrawScope.drawClockIndices(center: Offset, dialRadius: Float) {
    for (hour in 0 until 12) {
        val angleDeg = hour * 30f
        withTransform({
            rotate(degrees = angleDeg, pivot = center)
        }) {
            val isMainPill = (hour % 3 == 0)

            if (isMainPill) {
                // 12, 3, 6, 9 o'clock rounded capsule pill
                val pillWidth = 4.2.dp.toPx()
                val pillHeight = 12.5.dp.toPx()
                val topY = center.y - dialRadius * 0.88f

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF2D0), // Light champagne top
                            Color(0xFFE5BC6E), // Metallic gold body
                            Color(0xFFA67D28)  // Deep shadow base
                        ),
                        startY = topY,
                        endY = topY + pillHeight
                    ),
                    topLeft = Offset(center.x - pillWidth / 2f, topY),
                    size = Size(pillWidth, pillHeight),
                    cornerRadius = CornerRadius(pillWidth / 2f, pillWidth / 2f)
                )
            } else {
                // 1, 2, 4, 5, 7, 8, 10, 11 circular gold dots
                val dotCenterY = center.y - dialRadius * 0.82f
                val dotRadius = 2.4.dp.toPx()

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF4D6),
                            Color(0xFFE5BC6E),
                            Color(0xFF9E7528)
                        ),
                        center = Offset(center.x - 0.5.dp.toPx(), dotCenterY - 0.5.dp.toPx()),
                        radius = dotRadius
                    ),
                    radius = dotRadius,
                    center = Offset(center.x, dotCenterY)
                )
            }
        }
    }
}

/**
 * Draws a rounded metallic capsule hand with 3D gradient reflection (matching the clock logo).
 */
private fun DrawScope.drawCapsuleHand(
    center: Offset,
    length: Float,
    width: Float,
    tailLength: Float,
    lightColor: Color,
    shadowColor: Color
) {
    val tipY = center.y - length
    val tailY = center.y + tailLength
    val totalHeight = length + tailLength
    val cornerRadius = CornerRadius(width / 2f, width / 2f)

    // Drop shadow
    drawRoundRect(
        color = Color(0x60000000),
        topLeft = Offset(center.x - width / 2f + 2.dp.toPx(), tipY + 2.5.dp.toPx()),
        size = Size(width, totalHeight),
        cornerRadius = cornerRadius
    )

    // Main metallic body
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                lightColor,
                lightColor,
                shadowColor
            ),
            startX = center.x - width / 2f,
            endX = center.x + width / 2f
        ),
        topLeft = Offset(center.x - width / 2f, tipY),
        size = Size(width, totalHeight),
        cornerRadius = cornerRadius
    )

    // Longitudinal specular gleam line along the hand
    drawLine(
        color = Color.White.copy(alpha = 0.45f),
        start = Offset(center.x - width * 0.15f, tipY + width * 0.5f),
        end = Offset(center.x - width * 0.15f, tailY - width * 0.5f),
        strokeWidth = 0.8.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Draws a 3D faceted Dauphine hand with light and shadow facets for realistic metallic reflection.
 */
private fun DrawScope.drawDauphineHand(
    center: Offset,
    length: Float,
    halfWidth: Float,
    tailLength: Float,
    baseWidth: Float,
    lightColor: Color,
    shadowColor: Color
) {
    val waistY = center.y - length * 0.16f
    val tipY = center.y - length
    val tailY = center.y + tailLength

    // Drop shadow under hand
    val shadowPath = Path().apply {
        moveTo(center.x + 2.dp.toPx(), tipY + 2.5.dp.toPx())
        lineTo(center.x + halfWidth + 2.dp.toPx(), waistY + 2.5.dp.toPx())
        lineTo(center.x + baseWidth + 2.dp.toPx(), tailY + 2.5.dp.toPx())
        lineTo(center.x - baseWidth + 2.dp.toPx(), tailY + 2.5.dp.toPx())
        lineTo(center.x - halfWidth + 2.dp.toPx(), waistY + 2.5.dp.toPx())
        close()
    }
    drawPath(shadowPath, color = Color(0x66000000), style = Fill)

    // Left Facet (Light side)
    val leftFacet = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x - halfWidth, waistY)
        lineTo(center.x - baseWidth, tailY * 0.6f + center.y * 0.4f)
        lineTo(center.x, tailY)
        close()
    }
    drawPath(leftFacet, color = lightColor, style = Fill)

    // Right Facet (Shadow side)
    val rightFacet = Path().apply {
        moveTo(center.x, tipY)
        lineTo(center.x + halfWidth, waistY)
        lineTo(center.x + baseWidth, tailY * 0.6f + center.y * 0.4f)
        lineTo(center.x, tailY)
        close()
    }
    drawPath(rightFacet, color = shadowColor, style = Fill)

    // Crisp center spine highlight
    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(center.x, tipY),
        end = Offset(center.x, tailY),
        strokeWidth = 0.7.dp.toPx()
    )
}

/**
 * Draws a hairline second hand with circular counterbalance.
 */
private fun DrawScope.drawSecondHand(
    center: Offset,
    length: Float,
    tailLength: Float
) {
    val tipY = center.y - length
    val tailY = center.y + tailLength
    val counterCenterY = center.y + tailLength * 0.65f

    // Drop shadow
    drawLine(
        color = Color(0x4D000000),
        start = Offset(center.x + 1.5.dp.toPx(), tailY + 2.dp.toPx()),
        end = Offset(center.x + 1.5.dp.toPx(), tipY + 2.dp.toPx()),
        strokeWidth = 1.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Needle shaft
    drawLine(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF6DF), Color(0xFFF5C77E), Color(0xFFD4AF37)),
            startY = tipY,
            endY = tailY
        ),
        start = Offset(center.x, tailY),
        end = Offset(center.x, tipY),
        strokeWidth = 1.1.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Counterbalance ring
    val ringRadius = 4.2.dp.toPx()
    drawCircle(
        color = Color(0xFFF5C77E),
        radius = ringRadius,
        center = Offset(center.x, counterCenterY),
        style = Stroke(width = 1.4.dp.toPx())
    )
}

/**
 * Draws the central canon pinion cap and jewel pivot.
 */
private fun DrawScope.drawCenterPivot(center: Offset, pivotGlowAlpha: Float) {
    // Subtle animated pivot bloom
    if (pivotGlowAlpha > 0f) {
        val glowRadius = 18.dp.toPx()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x66FFE4A0).copy(alpha = 0.4f * pivotGlowAlpha),
                    Color.Transparent
                ),
                center = center,
                radius = glowRadius
            ),
            radius = glowRadius,
            center = center
        )
    }

    // Outer gold collar
    drawCircle(
        color = Color(0xFF8C6418),
        radius = 6.dp.toPx(),
        center = center
    )

    // Beveled gold disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF3D4), Color(0xFFD4AF37), Color(0xFFA67D28)),
            center = Offset(center.x - 1.dp.toPx(), center.y - 1.dp.toPx()),
            radius = 4.6.dp.toPx()
        ),
        radius = 4.6.dp.toPx(),
        center = center
    )

    // Center jewel pin
    drawCircle(
        color = Color(0xFF14120E),
        radius = 2.dp.toPx(),
        center = center
    )

    // Specular glint
    drawCircle(
        color = Color.White,
        radius = 0.8.dp.toPx(),
        center = Offset(center.x - 0.7.dp.toPx(), center.y - 0.7.dp.toPx())
    )
}
