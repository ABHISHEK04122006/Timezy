package com.timezy.widget.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.ui.theme.PurpleGlow
import com.timezy.widget.ui.theme.PurplePrimary

import com.timezy.widget.ui.theme.MidnightBackground
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightSubtleBorder

/**
 * Clean Midnight Minimal background container
 */
@Composable
fun LuxuryAmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        content()
    }
}

/**
 * Reusable Midnight Minimal card with subtle charcoal border
 */
@Composable
fun LuxuryGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    accentColor: Color = MidnightPrimaryAccent,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val baseModifier = modifier
        .clip(shape)
        .background(MidnightCard)
        .border(1.dp, MidnightSubtleBorder, shape)

    val finalModifier = if (onClick != null) {
        baseModifier.clickable { onClick() }
    } else {
        baseModifier
    }

    Box(modifier = finalModifier) {
        content()
    }
}

/**
 * Floating Liquid Glass Circle Orb (Matching user's screenshot with +304 Days)
 */
@Composable
fun LiquidGlassOrb(
    daysText: String,
    title: String,
    categoryName: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    onClick: (() -> Unit)? = null,
    onPinGlassWidget: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidShine")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbShineOffset"
    )

    val orbShape = CircleShape

    val clickModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = clickModifier.padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 20.dp,
                    shape = orbShape,
                    ambientColor = accentColor.copy(alpha = 0.4f),
                    spotColor = accentColor.copy(alpha = 0.5f)
                )
                .clip(orbShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x35FFFFFF),
                            Color(0x18FFFFFF),
                            Color(0x0E0E18)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x80FFFFFF),
                            Color(0x20FFFFFF),
                            accentColor.copy(alpha = 0.6f),
                            Color(0x40FFFFFF)
                        )
                    ),
                    shape = orbShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Specular top highlight crescent
            Box(
                modifier = Modifier
                    .size(size * 0.85f)
                    .offset(y = (-size * 0.15f))
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x30FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Inner Countdown Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = daysText,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )

                Text(
                    text = "Days",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Widget Pin Badge (1-tap to home screen)
            if (onPinGlassWidget != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-6).dp, y = (-6).dp)
                        .size(32.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFA855F7),
                                    Color(0xFF6D28D9)
                                )
                            )
                        )
                        .border(1.2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                        .clickable { onPinGlassWidget() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Pin Glass Widget to Home Screen",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title below orb
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = categoryName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp
            ),
            color = accentColor,
            textAlign = TextAlign.Center
        )
    }
}
