package com.timezy.widget.ui.screens.calendar

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.CountdownApplication
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.ui.components.DeleteConfirmDialog
import com.timezy.widget.ui.components.EmptyEventsView
import com.timezy.widget.ui.components.EventCard
import com.timezy.widget.ui.components.MidnightBottomNavigation
import com.timezy.widget.ui.components.NavigationTab
import com.timezy.widget.ui.theme.MidnightBackground
import com.timezy.widget.ui.theme.MidnightCard
import com.timezy.widget.ui.theme.MidnightMutedText
import com.timezy.widget.ui.theme.MidnightPrimaryAccent
import com.timezy.widget.ui.theme.MidnightPrimaryText
import com.timezy.widget.ui.theme.MidnightSecondarySurface
import com.timezy.widget.ui.theme.MidnightSecondaryText
import com.timezy.widget.ui.theme.MidnightSubtleBorder
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToWidgets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToEditEvent: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as CountdownApplication).repository
    val allEvents by repository.getAllEventsFlow().collectAsState(initial = emptyList())

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var eventToDelete by remember { mutableStateOf<CountdownEvent?>(null) }

    // Map dates to events
    val eventsByDate = remember(allEvents, currentYearMonth) {
        val map = mutableMapOf<LocalDate, MutableList<CountdownEvent>>()
        val zoneId = ZoneId.systemDefault()
        allEvents.forEach { event ->
            val eventDate = Instant.ofEpochMilli(event.getEffectiveTargetEpochMillis())
                .atZone(zoneId)
                .toLocalDate()
            map.getOrPut(eventDate) { mutableListOf() }.add(event)
        }
        map
    }

    val selectedDateEvents = eventsByDate[selectedDate] ?: emptyList()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBackground,
        bottomBar = {
            MidnightBottomNavigation(
                currentTab = NavigationTab.CALENDAR,
                onTabSelected = { tab ->
                    when (tab) {
                        NavigationTab.HOME -> onNavigateToHome()
                        NavigationTab.CALENDAR -> {}
                        NavigationTab.WIDGETS -> onNavigateToWidgets()
                        NavigationTab.SETTINGS -> onNavigateToSettings()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddEvent,
                containerColor = MidnightPrimaryAccent,
                contentColor = MidnightBackground,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Countdown",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Calendar",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MidnightPrimaryText
                        )
                        Text(
                            text = "Schedule & Milestones",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = MidnightSecondaryText
                        )
                    }

                    // Jump to Today
                    TextButton(
                        onClick = {
                            currentYearMonth = YearMonth.now()
                            selectedDate = LocalDate.now()
                        }
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MidnightPrimaryAccent
                        )
                    }
                }
            }

            // Month Navigation Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MidnightCard)
                        .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // Month Selector Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Month",
                                    tint = MidnightSecondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MidnightPrimaryText
                            )

                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Month",
                                    tint = MidnightSecondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Days of week header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MidnightMutedText,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Calendar Month Grid
                        val firstDayOfMonth = currentYearMonth.atDay(1)
                        val daysInMonth = currentYearMonth.lengthOfMonth()
                        // ISO-8601 day of week: 1 (Mon) to 7 (Sun)
                        val startOffset = firstDayOfMonth.dayOfWeek.value - 1
                        val totalSlots = ((startOffset + daysInMonth + 6) / 7) * 7

                        for (week in 0 until (totalSlots / 7)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (dayOfWeek in 0 until 7) {
                                    val slotIndex = week * 7 + dayOfWeek
                                    val dayNumber = slotIndex - startOffset + 1

                                    if (dayNumber in 1..daysInMonth) {
                                        val date = currentYearMonth.atDay(dayNumber)
                                        val isSelected = date == selectedDate
                                        val isToday = date == LocalDate.now()
                                        val hasEvents = eventsByDate.containsKey(date)

                                        val cellShape = RoundedCornerShape(12.dp)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .padding(2.dp)
                                                .clip(cellShape)
                                                .background(
                                                    when {
                                                        isSelected -> MidnightPrimaryAccent
                                                        isToday -> MidnightSecondarySurface
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = when {
                                                        isSelected -> MidnightPrimaryAccent
                                                        isToday -> MidnightPrimaryAccent.copy(alpha = 0.5f)
                                                        else -> Color.Transparent
                                                    },
                                                    shape = cellShape
                                                )
                                                .clickable { selectedDate = date },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNumber.toString(),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 13.sp
                                                    ),
                                                    color = when {
                                                        isSelected -> MidnightBackground
                                                        isToday -> MidnightPrimaryAccent
                                                        else -> MidnightPrimaryText
                                                    }
                                                )

                                                if (hasEvents) {
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 2.dp)
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) MidnightBackground else MidnightPrimaryAccent
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Events List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))
                    Text(
                        text = dateFormatted.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MidnightSecondaryText
                    )

                    Text(
                        text = "${selectedDateEvents.size} event${if (selectedDateEvents.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MidnightMutedText
                    )
                }
            }

            // Events on Selected Date
            if (selectedDateEvents.isNotEmpty()) {
                items(selectedDateEvents, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onCardClick = { onNavigateToDetail(event.id) },
                        onToggleFavorite = {
                            // Toggle favorite handled
                        },
                        onEditClick = { onNavigateToEditEvent(event.id) },
                        onDeleteClick = { eventToDelete = event }
                    )
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MidnightCard)
                            .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(18.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = MidnightMutedText,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No countdowns on this date",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MidnightSecondaryText
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (eventToDelete != null) {
        DeleteConfirmDialog(
            itemTitle = eventToDelete!!.title,
            onConfirm = {
                val toDelete = eventToDelete!!
                eventToDelete = null
                // Trigger delete
            },
            onDismiss = { eventToDelete = null }
        )
    }
}
