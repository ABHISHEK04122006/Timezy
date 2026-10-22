package com.timezy.widget.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.data.model.TimeRemaining
import com.timezy.widget.ui.theme.CountdownTheme
import com.timezy.widget.ui.theme.MidnightAccentBackground
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSubtleBorder

@Composable
fun LiveCounterDisplay(
    remaining: TimeRemaining,
    theme: EventTheme = EventTheme.COSMIC_PURPLE,
    modifier: Modifier = Modifier,
    showSeconds: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CounterUnit(
            value = remaining.days.toString(),
            label = if (remaining.days == 1) "DAY" else "DAYS",
            modifier = Modifier.weight(1f)
        )
        CounterUnit(
            value = String.format("%02d", remaining.hours),
            label = "HOURS",
            modifier = Modifier.weight(1f)
        )
        CounterUnit(
            value = String.format("%02d", remaining.minutes),
            label = "MINS",
            modifier = Modifier.weight(1f)
        )
        if (showSeconds) {
            CounterUnit(
                value = String.format("%02d", remaining.seconds),
                label = "SECS",
                modifier = Modifier.weight(1f),
                isAccent = true
            )
        }
    }
}

@Composable
fun CounterUnit(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false
) {
    val shape = RoundedCornerShape(14.dp)
    val isDark = CountdownTheme.colors.isDark

    val boxBackground = if (isAccent) {
        if (isDark) MidnightPrimaryAccent else MidnightAccentBackground
    } else {
        MidnightSecondarySurface
    }

    val boxBorder = if (isAccent) {
        MidnightPrimaryAccent
    } else {
        MidnightSubtleBorder
    }

    val numberColor = if (isAccent) {
        if (isDark) Color(0xFF171923) else Color(0xFF92400E)
    } else {
        MidnightPrimaryText
    }

    val labelColor = if (isAccent) {
        if (isDark) Color(0xFF171923).copy(alpha = 0.85f) else Color(0xFFB45309)
    } else {
        MidnightMutedText
    }

    val numberFontSize = when {
        value.length >= 4 -> 15.sp
        value.length == 3 -> 17.sp
        else -> 20.sp
    }

    Column(
        modifier = modifier
            .clip(shape)
            .background(boxBackground)
            .border(
                width = 1.dp,
                color = boxBorder,
                shape = shape
            )
            .padding(vertical = 10.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                slideInVertically { height -> height } togetherWith
                        slideOutVertically { height -> -height }
            },
            label = "CounterAnimation"
        ) { targetVal ->
            Text(
                text = targetVal,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = numberFontSize,
                    letterSpacing = (-0.5).sp
                ),
                color = numberColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            ),
            color = labelColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
