package com.timezy.widget.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.ui.theme.GoldCardBorder
import com.timezy.widget.ui.theme.GoldPrimaryAccent
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder
import com.timezy.widget.utils.DateTimeUtils
import com.timezy.widget.widget.WidgetUpdateHelper

@Composable
fun FeaturedCountdownCard(
    event: CountdownEvent,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    nowMillis: Long = System.currentTimeMillis(),
    showSeconds: Boolean = true
) {
    val remaining = event.calculateRemainingTime(nowMillis)
    val cardShape = RoundedCornerShape(24.dp)
    val categoryAccent = event.category.getAccentColor()
    val context = LocalContext.current
    var showWidgetPicker by remember { mutableStateOf(false) }

    val progress = DateTimeUtils.calculateProgress(
        createdEpochMillis = event.createdAtEpochMillis,
        targetEpochMillis = event.getEffectiveTargetEpochMillis(nowMillis),
        nowEpochMillis = nowMillis
    )
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    val isDark = com.timezy.widget.ui.theme.CountdownTheme.colors.isDark
    val cardGradient = if (isDark) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF382914), // Warm amber glow
                Color(0xFF1B1815),
                Color(0xFF12141B)
            ),
            center = Offset(0f, 0f),
            radius = 800f
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFDF8),
                Color(0xFFF9FAFD),
                Color(0xFFFFFFFF)
            ),
            center = Offset(0f, 0f),
            radius = 800f
        )
    }

    val chipContentColor = if (isDark) GoldPrimaryAccent else Color(0xFF92400E)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(cardGradient)
            .border(width = 1.dp, color = GoldCardBorder, shape = cardShape)
            .clickable { onCardClick() }
            .padding(20.dp)
    ) {
        Column {
            // Top Row: Category Pill & Actions (Gift & Heart in circular buttons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip (e.g. Travel)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF261D13) else Color(0xFFFEF3C7))
                        .border(1.dp, if (isDark) Color(0xFF453620) else Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = event.category.getIcon(),
                        contentDescription = null,
                        tint = chipContentColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.category.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = chipContentColor
                    )

                    if (event.recurrence != RecurrenceType.NONE) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Recurring",
                            tint = chipContentColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Actions: Gift Box & Heart in circular buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MidnightSecondarySurface)
                            .border(1.dp, MidnightSubtleBorder, CircleShape)
                            .clickable {
                                showWidgetPicker = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Add Widget to Home Screen",
                            tint = MidnightPrimaryText,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MidnightSecondarySurface)
                            .border(1.dp, MidnightSubtleBorder, CircleShape)
                            .clickable { onToggleFavorite() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (event.isFavorite) {
                                if (isDark) Color(0xFFFFA6B8) else Color(0xFFE11D48)
                            } else MidnightSecondaryText,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = (-0.3).sp
                ),
                color = MidnightPrimaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Date
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MidnightMutedText,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = DateTimeUtils.formatDate(event.getEffectiveTargetEpochMillis(nowMillis)) +
                            if (event.hasSpecificTime) " • " + DateTimeUtils.formatTime(event.getEffectiveTargetEpochMillis(nowMillis)) else "",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MidnightSecondaryText
                )
            }

            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                    color = MidnightMutedText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Countdown Timer with Golden SECS block
            LiveCounterDisplay(
                remaining = remaining,
                showSeconds = showSeconds
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Progress Bar & Days Left Stats
            val daysLeft = remaining.days + 1
            val daysLeftText = if (daysLeft == 1) "1 day left" else "$daysLeft days left"

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = daysLeftText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = MidnightPrimaryText
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}% elapsed",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = MidnightMutedText
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = GoldPrimaryAccent,
                    trackColor = if (isDark) Color(0xFF22242E) else MidnightSubtleBorder,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }

    if (showWidgetPicker) {
        WidgetTypePickerDialog(
            eventId = event.id,
            eventTitle = event.title,
            onDismiss = { showWidgetPicker = false }
        )
    }
}
