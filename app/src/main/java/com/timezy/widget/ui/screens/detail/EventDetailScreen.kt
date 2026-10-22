package com.timezy.widget.ui.screens.detail

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.ui.components.DeleteConfirmDialog
import com.timezy.widget.ui.components.LiveCounterDisplay
import com.timezy.widget.ui.components.WidgetTypePickerDialog
import com.timezy.widget.ui.theme.MidnightAccentBackground
import com.timezy.widget.ui.theme.MidnightBackground
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder
import com.timezy.widget.utils.DateTimeUtils
import com.timezy.widget.widget.WidgetUpdateHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailViewModel,
    eventId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val event by viewModel.event.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    val showSeconds by viewModel.showSeconds.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showWidgetPicker by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }

    BackHandler {
        onNavigateBack()
    }

    if (event == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading countdown...", color = MidnightMutedText)
        }
        return
    }

    val currentEvent = event!!
    val theme = currentEvent.theme
    val remaining = currentEvent.calculateRemainingTime(nowMillis)
    val categoryAccent = currentEvent.category.getAccentColor()

    val progress = DateTimeUtils.calculateProgress(
        createdEpochMillis = currentEvent.createdAtEpochMillis,
        targetEpochMillis = currentEvent.getEffectiveTargetEpochMillis(nowMillis),
        nowEpochMillis = nowMillis
    )
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBackground,
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MidnightPrimaryText
                        )
                    }
                },
                actions = {
                    // Pin Widget to Home
                    IconButton(onClick = {
                        showWidgetPicker = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Add Widget to Home",
                            tint = MidnightPrimaryAccent
                        )
                    }

                    // Favorite Toggle
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (currentEvent.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (currentEvent.isFavorite) Color(0xFFF17D91) else MidnightMutedText
                        )
                    }

                    // Share Countdown
                    IconButton(
                        onClick = {
                            val shareText = "⏳ Countdown to ${currentEvent.title}: ${remaining.displayDaysText}!\n" +
                                    "Date: ${DateTimeUtils.formatFullDateTime(currentEvent.getEffectiveTargetEpochMillis(nowMillis))}\n" +
                                    "Tracked with Timezy app"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Countdown"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MidnightPrimaryText
                        )
                    }

                    // Edit
                    IconButton(onClick = { onNavigateToEdit(currentEvent.id) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MidnightPrimaryText
                        )
                    }

                    // Delete
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF17D91)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card: Midnight Minimal
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MidnightCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Category Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MidnightAccentBackground)
                            .border(1.dp, categoryAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = currentEvent.category.getIcon(),
                            contentDescription = null,
                            tint = categoryAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentEvent.category.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = categoryAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = currentEvent.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        ),
                        color = MidnightPrimaryText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = remaining.displayDaysText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = categoryAccent
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Countdown Hero Block
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MidnightSecondarySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = remaining.primaryNumberText,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 48.sp
                                ),
                                color = categoryAccent
                            )
                            Text(
                                text = "DAYS REMAINING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MidnightMutedText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Live Ticking Units (Days, Hours, Mins, Secs)
                    LiveCounterDisplay(
                        remaining = remaining,
                        theme = theme,
                        showSeconds = showSeconds
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Elapsed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MidnightMutedText
                            )
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = categoryAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = categoryAccent,
                            trackColor = MidnightSecondarySurface,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }

            // Quick Pin Widget to Home Screen
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showWidgetPicker = true
                    },
                shape = RoundedCornerShape(16.dp),
                color = MidnightCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MidnightSecondarySurface)
                            .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Widget Options",
                            tint = MidnightPrimaryAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add Widget to Home Screen",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MidnightPrimaryText
                        )
                        Text(
                            text = "Choose Transparent, Liquid Glass, or Card style",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MidnightSecondaryText
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Widget",
                        tint = MidnightPrimaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Target Date & Time Card
            DetailInfoCard(
                icon = Icons.Default.CalendarMonth,
                iconColor = MidnightPrimaryAccent,
                title = "Target Date & Time",
                content = DateTimeUtils.formatFullDateTime(currentEvent.getEffectiveTargetEpochMillis(nowMillis))
            )

            // Recurrence Card
            if (currentEvent.recurrence != RecurrenceType.NONE) {
                DetailInfoCard(
                    icon = Icons.Default.Repeat,
                    iconColor = MidnightPrimaryAccent,
                    title = "Recurring Event",
                    content = "Repeats ${currentEvent.recurrence.displayName.lowercase()}. Automatically calculates the next occurrence after each cycle."
                )
            }

            // Reminders Status
            DetailInfoCard(
                icon = Icons.Default.Notifications,
                iconColor = MidnightPrimaryAccent,
                title = "Reminder Alert",
                content = if (currentEvent.isNotificationEnabled) {
                    when (currentEvent.reminderMinutesBefore) {
                        0 -> "Scheduled at the exact time of the event"
                        60 -> "Scheduled 1 hour before the event"
                        1440 -> "Scheduled 1 day before the event"
                        2880 -> "Scheduled 2 days before the event"
                        else -> "Scheduled ${currentEvent.reminderMinutesBefore / 1440} days before"
                    }
                } else {
                    "Notifications disabled for this event"
                }
            )

            // Description / Notes Card
            if (currentEvent.description.isNotBlank()) {
                DetailInfoCard(
                    icon = Icons.Default.Description,
                    iconColor = MidnightPrimaryAccent,
                    title = "Notes & Checklist",
                    content = currentEvent.description
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Row: Edit & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onNavigateToEdit(currentEvent.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightPrimaryAccent,
                        contentColor = MidnightBackground
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Event", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showDeleteDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightSecondarySurface,
                        contentColor = Color(0xFFF17D91)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFF17D91),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemTitle = currentEvent.title,
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteEvent()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showWidgetPicker) {
        WidgetTypePickerDialog(
            eventId = currentEvent.id,
            eventTitle = currentEvent.title,
            onDismiss = { showWidgetPicker = false }
        )
    }
}

@Composable
fun DetailInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MidnightCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightSubtleBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MidnightSecondarySurface)
                    .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MidnightMutedText
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MidnightPrimaryText
                )
            }
        }
    }
}
