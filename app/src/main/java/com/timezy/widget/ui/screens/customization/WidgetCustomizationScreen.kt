package com.timezy.widget.ui.screens.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.CircleStyle
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.TimeRemaining
import com.timezy.widget.ui.components.MidnightBottomNavigation
import com.timezy.widget.ui.components.NavigationTab
import com.timezy.widget.ui.theme.MidnightAccentBackground
import com.timezy.widget.ui.theme.MidnightBackground
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder
import com.timezy.widget.ui.theme.PurplePrimary
import com.timezy.widget.utils.DateTimeUtils
import com.timezy.widget.widget.WidgetUpdateHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetCustomizationScreen(
    viewModel: CustomizationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()
    val theme = uiState.selectedTheme
    val sampleEvent = uiState.sampleEvent
    val remaining = sampleEvent.calculateRemainingTime(nowMillis)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refreshInstalledWidgets()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Liquid Glass & Widgets",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MidnightPrimaryText
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MidnightPrimaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightBackground
                )
            )
        },
        bottomBar = {
            MidnightBottomNavigation(
                currentTab = NavigationTab.WIDGETS,
                onTabSelected = { tab ->
                    when (tab) {
                        NavigationTab.HOME -> onNavigateToHome()
                        NavigationTab.CALENDAR -> onNavigateToCalendar()
                        NavigationTab.WIDGETS -> { /* Current tab */ }
                        NavigationTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Active Widgets Selection Section (if any widgets are on the home screen)
            if (uiState.installedWidgets.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = MidnightPrimaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CUSTOMIZE WIDGET ON HOME SCREEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = MidnightPrimaryAccent
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Default template option
                        item {
                            val isSelected = uiState.selectedWidgetId == null
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectWidget(null) }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) MidnightPrimaryAccent else MidnightSubtleBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                color = if (isSelected) MidnightAccentBackground else MidnightCard,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Default Template",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) MidnightPrimaryAccent else MidnightPrimaryText
                                    )
                                }
                            }
                        }

                        // Each installed widget on the home screen
                        items(uiState.installedWidgets, key = { it.appWidgetId }) { widget ->
                            val isSelected = uiState.selectedWidgetId == widget.appWidgetId
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectWidget(widget.appWidgetId) }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) widget.theme.primaryColor else MidnightSubtleBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                color = if (isSelected) MidnightAccentBackground else MidnightCard,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(widget.theme.primaryColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Widget: ${widget.title}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) widget.theme.primaryColor else MidnightPrimaryText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 200.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Live Widget Preview Header
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = MidnightPrimaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE WIDGET PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 12.sp
                        ),
                        color = MidnightPrimaryAccent
                    )
                }

                // Full-width segmented selector (Cards | List style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightCard)
                        .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WidgetPreviewSize.entries.forEach { size ->
                        val isSelected = size == uiState.previewSize
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MidnightAccentBackground else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MidnightPrimaryAccent.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setPreviewSize(size) }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = size.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    lineHeight = 13.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = if (isSelected) MidnightPrimaryAccent else MidnightSecondaryText,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Widget Wallpaper Preview Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF191B22),
                                Color(0xFF101216),
                                MidnightBackground
                            )
                        )
                    )
                    .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularCountdownMockup(
                    event = sampleEvent,
                    remaining = remaining,
                    circleStyle = uiState.circleStyle,
                    ringColor = theme.primaryColor,
                    ringColorHex = theme.ringColorHex,
                    isLiquidGlassEnabled = uiState.isLiquidGlassEnabled,
                    showSeconds = uiState.showSeconds,
                    isMedium = (uiState.previewSize == WidgetPreviewSize.COUNTDOWN_MEDIUM),
                    nowMillis = nowMillis
                )
            }

            // Circle Style Selector
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = null,
                        tint = MidnightPrimaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CIRCLE WIDGET STYLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MidnightPrimaryAccent
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        CircleStyle.LIQUID_GLASS to "Liquid Glass",
                        CircleStyle.PURE_WHITE to "Pure White",
                        CircleStyle.SMOKED_GLASS to "Smoked Glass"
                    ).forEach { (style, label) ->
                        val isSelected = style == uiState.circleStyle
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setCircleStyle(style) }
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MidnightPrimaryAccent else MidnightSubtleBorder,
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            color = if (isSelected) MidnightAccentBackground else MidnightCard,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) MidnightPrimaryAccent else MidnightPrimaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Widget Styling Options Section
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = MidnightPrimaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WIDGET PREFERENCES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MidnightPrimaryAccent
                    )
                }

                // Liquid Glass Effect toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MidnightCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Liquid Glass Effect",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MidnightPrimaryText
                            )
                            Text(
                                text = "Translucent frosted background showing home wallpaper",
                                style = MaterialTheme.typography.bodySmall,
                                color = MidnightSecondaryText
                            )
                        }

                        Switch(
                            checked = uiState.isLiquidGlassEnabled,
                            onCheckedChange = { viewModel.setLiquidGlassEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MidnightPrimaryAccent,
                                checkedTrackColor = MidnightAccentBackground,
                                uncheckedThumbColor = MidnightMutedText,
                                uncheckedTrackColor = MidnightSecondarySurface
                            )
                        )
                    }
                }

                // Show seconds toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MidnightCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Seconds Counter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MidnightPrimaryText
                            )
                            Text(
                                text = "Display animated seconds on live countdown cards",
                                style = MaterialTheme.typography.bodySmall,
                                color = MidnightSecondaryText
                            )
                        }

                        Switch(
                            checked = uiState.showSeconds,
                            onCheckedChange = { viewModel.setShowSeconds(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MidnightPrimaryAccent,
                                checkedTrackColor = MidnightAccentBackground,
                                uncheckedThumbColor = MidnightMutedText,
                                uncheckedTrackColor = MidnightSecondarySurface
                            )
                        )
                    }
                }
            }


            // Direct Pin Widget Button
            Button(
                onClick = {
                    val pinEventId = if (uiState.hasRealEvents) sampleEvent.id else -1L
                    when (uiState.previewSize) {
                        WidgetPreviewSize.COUNTDOWN_CIRCLE -> {
                            WidgetUpdateHelper.requestPinCircleWidget(
                                context = context,
                                eventId = pinEventId,
                                theme = uiState.selectedTheme,
                                style = uiState.circleStyle
                            )
                        }
                        WidgetPreviewSize.COUNTDOWN_MEDIUM -> {
                            WidgetUpdateHelper.requestPinMediumWidget(
                                context = context,
                                eventId = pinEventId,
                                theme = uiState.selectedTheme,
                                style = uiState.circleStyle
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MidnightSecondarySurface,
                    contentColor = MidnightPrimaryAccent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Widgets, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (uiState.previewSize) {
                        WidgetPreviewSize.COUNTDOWN_CIRCLE -> "Add Countdown Widget to Home Screen"
                        WidgetPreviewSize.COUNTDOWN_MEDIUM -> "Add Countdown (medium) to Home Screen"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Save / Apply Button
            val selectedWidget = uiState.installedWidgets.firstOrNull { it.appWidgetId == uiState.selectedWidgetId }
            val widgetLabel = if (selectedWidget != null) "Widget: ${selectedWidget.title}" else "Widget"
            Button(
                onClick = { viewModel.applyCustomizations() },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isSavedSuccess) Color(0xFF10B981) else MidnightPrimaryAccent,
                    contentColor = if (uiState.isSavedSuccess) Color.White else MidnightBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isSavedSuccess) {
                        if (uiState.selectedWidgetId != null) "Applied to $widgetLabel!" else "Preferences Applied!"
                    } else {
                        if (uiState.selectedWidgetId != null) "Apply to $widgetLabel" else "Apply Widget Styling"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

/**
 * Visual Mockup of the Transparent Circular Countdown Widget
 */
@Composable
fun CircularCountdownMockup(
    event: CountdownEvent,
    remaining: TimeRemaining,
    circleStyle: CircleStyle,
    ringColor: Color,
    ringColorHex: String,
    isLiquidGlassEnabled: Boolean,
    showSeconds: Boolean,
    isMedium: Boolean,
    nowMillis: Long
) {
    val circleSize = if (isMedium) 136.dp else 110.dp
    val strokeWidth = if (isMedium) 8.5.dp else 7.dp

    val (progress, _) = WidgetUpdateHelper.calculateProgress(event, nowMillis)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Outer Container for Ring + Center Disc
        Box(
            modifier = Modifier.size(circleSize),
            contentAlignment = Alignment.Center
        ) {
            // 1. Draw Inactive Track + Active Progress Ring
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokePx = strokeWidth.toPx()
                val arcRadius = (size.minDimension - strokePx) / 2f
                val centerOffset = Offset(size.width / 2f, size.height / 2f)

                // Track ring
                drawCircle(
                    color = Color(0x334B5563),
                    radius = arcRadius,
                    center = centerOffset,
                    style = Stroke(width = strokePx)
                )

                // Active colored progress arc (shrinks as target approaches)
                if (progress > 0f) {
                    val sweepAngle = progress.coerceIn(0.01f, 1.0f) * 360f
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(centerOffset.x - arcRadius, centerOffset.y - arcRadius),
                        size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            }

            // 2. Disc Interior according to CircleStyle
            val innerDiscSize = circleSize - (strokeWidth * 2) + 2.dp

            val discModifier = when (circleStyle) {
                CircleStyle.PURE_WHITE -> {
                    Modifier
                        .size(innerDiscSize)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                }
                CircleStyle.LIQUID_GLASS -> {
                    if (isLiquidGlassEnabled) {
                        Modifier
                            .size(innerDiscSize)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xEBFFFFFF), // 92% frosted highlight
                                        Color(0xC4F1F5F9), // 77% frosted center
                                        Color(0x91E2E8F0)  // 57% soft edge
                                    )
                                )
                            )
                            .border(1.5.dp, Color(0xDCFFFFFF), CircleShape)
                    } else {
                        Modifier
                            .size(innerDiscSize)
                            .clip(CircleShape)
                            .background(Color(0x55E2E8F0))
                            .border(1.dp, Color(0x82CBD5E1), CircleShape)
                    }
                }
                CircleStyle.SMOKED_GLASS -> {
                    Modifier
                        .size(innerDiscSize)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xD71E232D), // 84% dark smoked glass
                                    Color(0xE610141B)  // 90% obsidian slate
                                )
                            )
                        )
                        .border(1.dp, Color(0x6E4B5563), CircleShape)
                }
            }

            val numberColor = when (circleStyle) {
                CircleStyle.PURE_WHITE -> Color(0xFF000000)
                CircleStyle.LIQUID_GLASS -> Color(0xFF0F172A)
                CircleStyle.SMOKED_GLASS -> Color(0xFFF8FAFC)
            }

            val labelColor = when (circleStyle) {
                CircleStyle.PURE_WHITE -> Color(0xFF111827)
                CircleStyle.LIQUID_GLASS -> Color(0xFF334155)
                CircleStyle.SMOKED_GLASS -> Color(0xFF94A3B8)
            }

            Box(
                modifier = discModifier,
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = remaining.primaryNumberText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isMedium) 40.sp else 31.sp,
                            lineHeight = if (isMedium) 40.sp else 31.sp
                        ),
                        color = numberColor
                    )

                    Text(
                        text = if (remaining.primaryNumberText == "1") "Day" else "Days",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isMedium) 12.sp else 10.sp,
                            lineHeight = if (isMedium) 12.sp else 10.sp
                        ),
                        color = labelColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = event.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (isMedium) 13.5.sp else 12.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.7f),
                    offset = Offset(0f, 2f),
                    blurRadius = 4f
                )
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (showSeconds) {
            Spacer(modifier = Modifier.height(2.dp))
            val timeText = WidgetUpdateHelper.formatSecondaryTimer(event, remaining, nowMillis)
            Text(
                text = timeText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(0f, 1f),
                        blurRadius = 3f
                    )
                ),
                color = ringColor
            )
        }
    }
}

/**
 * Exact replica of the Circular Liquid Glass widget shown in the user's screenshot
 */
@Composable
fun LiquidGlassCircleMockup(
    circleStyle: CircleStyle,
    daysNumber: String,
    eventTitle: String = ""
) {
    val bgModifier = when (circleStyle) {
        CircleStyle.LIQUID_GLASS -> Modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xE6FFFFFF),
                        Color(0xCCF1F5F9),
                        Color(0xB3E2E8F0)
                    )
                )
            )
            .border(2.dp, Color.White, CircleShape)

        CircleStyle.PURE_WHITE -> Modifier
            .background(Color.White)
            .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)

        CircleStyle.SMOKED_GLASS -> Modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xCC1E172E),
                        Color(0xB3111827)
                    )
                )
            )
            .border(1.5.dp, MidnightPrimaryAccent.copy(alpha = 0.5f), CircleShape)
    }

    val textColor = if (circleStyle == CircleStyle.SMOKED_GLASS) MidnightPrimaryText else Color(0xFF0F172A)
    val labelColor = if (circleStyle == CircleStyle.SMOKED_GLASS) MidnightPrimaryAccent else Color(0xFF475569)

    Box(
        modifier = Modifier
            .size(146.dp)
            .shadow(16.dp, CircleShape)
            .clip(CircleShape)
            .then(bgModifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = daysNumber,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp
                ),
                color = textColor
            )

            Text(
                text = "Days",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = labelColor,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun CompactWidgetMockup(
    event: com.timezy.widget.data.model.CountdownEvent,
    remaining: com.timezy.widget.data.model.TimeRemaining,
    isTransparent: Boolean
) {
    val theme = event.theme
    val shape = RoundedCornerShape(22.dp)
    val bgColor = if (isTransparent) theme.surfaceColor.copy(alpha = 0.55f) else Color(0xFF141926)

    Box(
        modifier = Modifier
            .size(width = 175.dp, height = 150.dp)
            .clip(shape)
            .background(bgColor)
            .border(1.5.dp, theme.primaryColor.copy(alpha = 0.5f), shape)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            // Category badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.primaryColor.copy(alpha = 0.2f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = event.category.displayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = theme.accentColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Big Days
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = remaining.days.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = theme.primaryColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DAYS\nLEFT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp
                    ),
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title & Date
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = DateTimeUtils.formatDate(event.targetDateTimeEpochMillis),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TransparentCountdownMockup(
    event: com.timezy.widget.data.model.CountdownEvent,
    remaining: com.timezy.widget.data.model.TimeRemaining
) {
    val lavenderAccent = Color(0xFFB69AFF)
    val whitePrimary = Color.White
    val subtleGray = Color(0xFFA1A5B0)
    val mutedGray = Color(0xFF8E929E)
    val textShadow = Shadow(
        color = Color(0xB0000000),
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )

    // Genuinely transparent background: 0% opacity, no card background, no border
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left info column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Minimal Lavender Accent Category
                Text(
                    text = event.category.displayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        shadow = textShadow
                    ),
                    color = lavenderAccent
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Prominent White Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = textShadow
                    ),
                    color = whitePrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Subtle Gray Date
                Text(
                    text = DateTimeUtils.formatDate(event.targetDateTimeEpochMillis),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        shadow = textShadow
                    ),
                    color = subtleGray,
                    maxLines = 1
                )

                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            shadow = textShadow
                        ),
                        color = mutedGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right prominent countdown days column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = remaining.primaryNumberText,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = textShadow
                    ),
                    color = whitePrimary
                )

                Text(
                    text = remaining.primaryLabelText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        shadow = textShadow
                    ),
                    color = lavenderAccent
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${remaining.hours}h ${remaining.minutes}m",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        shadow = textShadow
                    ),
                    color = subtleGray
                )
            }
        }
    }
}

@Deprecated("Replaced by TransparentCountdownMockup", ReplaceWith("TransparentCountdownMockup(event, remaining)"))
@Composable
fun BannerWidgetMockup(
    event: com.timezy.widget.data.model.CountdownEvent,
    remaining: com.timezy.widget.data.model.TimeRemaining,
    isTransparent: Boolean = true
) {
    TransparentCountdownMockup(event = event, remaining = remaining)
}
