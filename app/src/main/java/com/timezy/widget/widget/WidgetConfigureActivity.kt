package com.timezy.widget.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.CountdownApplication
import com.timezy.widget.data.model.CircleStyle
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.ui.screens.customization.CircularCountdownMockup
import com.timezy.widget.ui.theme.CountdownWidgetTheme
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
import kotlinx.coroutines.delay

class WidgetConfigureActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set result to CANCELED initially
        setResult(Activity.RESULT_CANCELED)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Load initial per-widget saved preferences
        val initialEventId = WidgetUpdateHelper.getWidgetEventId(this, appWidgetId)
        val initialTheme = WidgetUpdateHelper.getWidgetColorTheme(this, appWidgetId)
        val initialStyle = WidgetUpdateHelper.getWidgetCircleStyle(this, appWidgetId)
        val initialShowSeconds = WidgetUpdateHelper.isWidgetShowSecondsEnabled(this, appWidgetId)
        val initialLiquidGlass = WidgetUpdateHelper.isWidgetLiquidGlassEnabled(this, appWidgetId)

        val repository = (application as CountdownApplication).repository

        setContent {
            CountdownWidgetTheme {
                val events by repository.getAllEventsFlow().collectAsState(initial = emptyList())

                var selectedEventId by remember { mutableLongStateOf(initialEventId) }
                var selectedTheme by remember { mutableStateOf(initialTheme) }
                var selectedStyle by remember { mutableStateOf(initialStyle) }
                var showSeconds by remember { mutableStateOf(initialShowSeconds) }
                var isLiquidGlassEnabled by remember { mutableStateOf(initialLiquidGlass) }

                // Live ticking time for preview
                var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000)
                        nowMillis = System.currentTimeMillis()
                    }
                }

                // Resolve event to display in live preview
                val previewEvent = remember(selectedEventId, events) {
                    val base = if (selectedEventId != -1L) {
                        events.firstOrNull { it.id == selectedEventId }
                    } else {
                        events.sortedBy { it.getEffectiveTargetEpochMillis(nowMillis) }
                            .firstOrNull { it.getEffectiveTargetEpochMillis(nowMillis) >= nowMillis }
                            ?: events.firstOrNull()
                    }

                    base ?: CountdownEvent(
                        id = -1L,
                        title = "Preview Event",
                        description = "Widget live preview",
                        targetDateTimeEpochMillis = nowMillis + (30L * 86400000L),
                        hasSpecificTime = false,
                        category = EventCategory.OTHER,
                        theme = EventTheme.NEON_CYAN
                    )
                }

                val remaining = previewEvent.calculateRemainingTime(nowMillis)

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "Configure Widget",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MidnightPrimaryText
                                    )
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = MidnightPrimaryText
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MidnightBackground
                            )
                        )
                    },
                    containerColor = MidnightBackground
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(2.dp))
                            // Live Widget Preview Header
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
                        }

                        // Widget Preview Canvas
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
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
                                    event = previewEvent,
                                    remaining = remaining,
                                    circleStyle = selectedStyle,
                                    ringColor = previewEvent.theme.primaryColor,
                                    ringColorHex = previewEvent.theme.ringColorHex,
                                    isLiquidGlassEnabled = isLiquidGlassEnabled,
                                    showSeconds = showSeconds,
                                    isMedium = false,
                                    nowMillis = nowMillis
                                )
                            }
                        }

                        // Circle Widget Style
                        item {
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
                                        val isSelected = style == selectedStyle
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { selectedStyle = style }
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
                        }

                        // Preferences (Show Seconds & Liquid Glass)
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

                                // Show Seconds Toggle
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MidnightCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
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
                                                text = "Display live ticking seconds counter on widget",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MidnightSecondaryText
                                            )
                                        }

                                        Switch(
                                            checked = showSeconds,
                                            onCheckedChange = { showSeconds = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = MidnightPrimaryAccent,
                                                checkedTrackColor = MidnightAccentBackground,
                                                uncheckedThumbColor = MidnightMutedText,
                                                uncheckedTrackColor = MidnightSecondarySurface
                                            )
                                        )
                                    }
                                }

                                // Liquid Glass Toggle
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MidnightCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
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
                                            checked = isLiquidGlassEnabled,
                                            onCheckedChange = { isLiquidGlassEnabled = it },
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
                        }

                        // Event Selection Section
                        item {
                            Text(
                                text = "SELECT COUNTDOWN EVENT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MidnightPrimaryAccent
                            )
                        }

                        if (events.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "No countdowns yet",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MidnightPrimaryText
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Create your first countdown in the app to display it on this widget.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MidnightSecondaryText
                                    )
                                }
                            }
                        } else {
                            // Automatic option
                            item {
                                AutoOptionCard(
                                    isSelected = (selectedEventId == -1L),
                                    accentColor = selectedTheme.primaryColor,
                                    onClick = { selectedEventId = -1L }
                                )
                            }

                            // Individual events
                            items(events, key = { it.id }) { event ->
                                EventSelectCard(
                                    event = event,
                                    isSelected = (selectedEventId == event.id),
                                    accentColor = selectedTheme.primaryColor,
                                    onClick = { selectedEventId = event.id }
                                )
                            }
                        }

                        // Apply Button
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    saveAndApply(
                                        selectedEventId = selectedEventId,
                                        selectedTheme = previewEvent.theme,
                                        selectedStyle = selectedStyle,
                                        showSeconds = showSeconds,
                                        isLiquidGlassEnabled = isLiquidGlassEnabled
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MidnightPrimaryAccent,
                                    contentColor = MidnightBackground
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Save Widget Configuration",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    private fun saveAndApply(
        selectedEventId: Long,
        selectedTheme: EventTheme,
        selectedStyle: CircleStyle,
        showSeconds: Boolean,
        isLiquidGlassEnabled: Boolean
    ) {
        val context = this
        // 1. Save all settings specifically for this appWidgetId
        WidgetUpdateHelper.setWidgetEventId(context, appWidgetId, selectedEventId)
        WidgetUpdateHelper.setWidgetColorTheme(context, appWidgetId, selectedTheme)
        WidgetUpdateHelper.setWidgetStyle(context, appWidgetId, selectedStyle)
        WidgetUpdateHelper.setWidgetShowSeconds(context, appWidgetId, showSeconds)
        WidgetUpdateHelper.setWidgetLiquidGlass(context, appWidgetId, isLiquidGlassEnabled)

        // 2. Refresh only this widget instance
        WidgetUpdateHelper.updateSpecificWidgetSync(context, appWidgetId)

        // 3. Return RESULT_OK to launcher
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}

@Composable
fun AutoOptionCard(
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else MidnightSubtleBorder,
                shape = shape
            ),
        shape = shape,
        color = if (isSelected) MidnightAccentBackground else MidnightCard
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Closest Upcoming Event (Auto)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MidnightPrimaryText
                )
                Text(
                    text = "Automatically switches to next nearest event",
                    style = MaterialTheme.typography.bodySmall,
                    color = MidnightSecondaryText
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EventSelectCard(
    event: CountdownEvent,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val remaining = event.calculateRemainingTime()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else MidnightSubtleBorder,
                shape = shape
            ),
        shape = shape,
        color = if (isSelected) MidnightAccentBackground else MidnightCard
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = event.category.getIcon(),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MidnightPrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = DateTimeUtils.formatDate(event.getEffectiveTargetEpochMillis()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MidnightSecondaryText
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = remaining.displayDaysText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = accentColor
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
