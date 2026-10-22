package com.timezy.widget.ui.screens.add_edit

import android.app.TimePickerDialog
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.ui.components.CategoryPicker
import com.timezy.widget.ui.components.ColorThemePicker
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    viewModel: AddEditEventViewModel,
    eventId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        if (eventId != null && eventId > 0L) {
            viewModel.loadEvent(eventId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var recurrenceExpanded by remember { mutableStateOf(false) }
    var reminderExpanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.targetDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.onDateChange(localDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = MidnightPrimaryAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = MidnightSecondaryText)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Countdown" else "New Countdown",
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
                actions = {
                    IconButton(onClick = { viewModel.saveEvent() }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = MidnightPrimaryAccent
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Event Title Input
            Column {
                Text(
                    text = "EVENT NAME *",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MidnightPrimaryAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    placeholder = { Text("e.g. Hawaii Summer Trip, Sarah's 25th Birthday", color = MidnightMutedText) },
                    isError = uiState.titleError != null,
                    supportingText = {
                        if (uiState.titleError != null) {
                            Text(uiState.titleError!!, color = Color(0xFFF17D91))
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MidnightPrimaryAccent,
                        unfocusedBorderColor = MidnightSubtleBorder,
                        focusedTextColor = MidnightPrimaryText,
                        unfocusedTextColor = MidnightPrimaryText,
                        focusedContainerColor = MidnightSecondarySurface,
                        unfocusedContainerColor = MidnightSecondarySurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Category Picker
            Column {
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MidnightPrimaryAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryPicker(
                    selectedCategory = uiState.category,
                    onCategorySelected = { viewModel.onCategoryChange(it) }
                )
            }

            // Theme & Color Picker
            Column {
                Text(
                    text = "THEME & ACCENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MidnightPrimaryAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                ColorThemePicker(
                    selectedTheme = uiState.theme,
                    onThemeSelected = { viewModel.onThemeChange(it) }
                )
            }

            // Date Picker Card
            Column {
                Text(
                    text = "TARGET DATE *",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MidnightPrimaryAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showDatePicker = true }
                        .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(14.dp)),
                    color = MidnightCard,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MidnightPrimaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = DateTimeUtils.formatDate(
                                    uiState.targetDate
                                        .atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MidnightPrimaryText
                            )
                            Text(
                                text = "Tap to change target date",
                                style = MaterialTheme.typography.bodySmall,
                                color = MidnightSecondaryText
                            )
                        }
                    }
                }
            }

            // Time Option (Toggle + Time Picker)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SPECIFIC TIME",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MidnightPrimaryAccent
                        )
                        Text(
                            text = "Set an exact target hour and minute",
                            style = MaterialTheme.typography.bodySmall,
                            color = MidnightSecondaryText
                        )
                    }

                    Switch(
                        checked = uiState.hasSpecificTime,
                        onCheckedChange = { viewModel.onHasSpecificTimeChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MidnightPrimaryAccent,
                            checkedTrackColor = MidnightAccentBackground,
                            uncheckedThumbColor = MidnightMutedText,
                            uncheckedTrackColor = MidnightSecondarySurface
                        )
                    )
                }

                if (uiState.hasSpecificTime) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        viewModel.onTimeChange(LocalTime.of(hourOfDay, minute))
                                    },
                                    uiState.targetTime.hour,
                                    uiState.targetTime.minute,
                                    false
                                ).show()
                            }
                            .border(1.dp, MidnightSubtleBorder, RoundedCornerShape(14.dp)),
                        color = MidnightCard,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MidnightPrimaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = DateTimeUtils.formatTime(
                                    uiState.targetDate.atTime(uiState.targetTime)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MidnightPrimaryText
                            )
                        }
                    }
                }
            }

            // Recurrence Option
            Column {
                Text(
                    text = "RECURRENCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MidnightPrimaryAccent
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = recurrenceExpanded,
                    onExpandedChange = { recurrenceExpanded = !recurrenceExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.recurrence.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                        leadingIcon = {
                            Icon(Icons.Default.Repeat, contentDescription = null, tint = MidnightPrimaryAccent)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MidnightPrimaryAccent,
                            unfocusedBorderColor = MidnightSubtleBorder,
                            focusedTextColor = MidnightPrimaryText,
                            unfocusedTextColor = MidnightPrimaryText,
                            focusedContainerColor = MidnightSecondarySurface,
                            unfocusedContainerColor = MidnightSecondarySurface
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = recurrenceExpanded,
                        onDismissRequest = { recurrenceExpanded = false }
                    ) {
                        RecurrenceType.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.displayName) },
                                onClick = {
                                    viewModel.onRecurrenceChange(option)
                                    recurrenceExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Reminders / Notification
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REMINDER NOTIFICATIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MidnightPrimaryAccent
                        )
                        Text(
                            text = "Receive an alert before the event starts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MidnightSecondaryText
                        )
                    }

                    Switch(
                        checked = uiState.isNotificationEnabled,
                        onCheckedChange = { viewModel.onNotificationEnabledChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MidnightPrimaryAccent,
                            checkedTrackColor = MidnightAccentBackground,
                            uncheckedThumbColor = MidnightMutedText,
                            uncheckedTrackColor = MidnightSecondarySurface
                        )
                    )
                }

                if (uiState.isNotificationEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val reminderOptions = listOf(
                        0 to "At time of event",
                        60 to "1 hour before",
                        1440 to "1 day before",
                        2880 to "2 days before",
                        10080 to "1 week before"
                    )
                    val currentLabel = reminderOptions.firstOrNull { it.first == uiState.reminderMinutesBefore }?.second ?: "1 day before"

                    ExposedDropdownMenuBox(
                        expanded = reminderExpanded,
                        onExpandedChange = { reminderExpanded = !reminderExpanded }
                    ) {
                        OutlinedTextField(
                            value = currentLabel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reminderExpanded) },
                            leadingIcon = {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = MidnightPrimaryAccent)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MidnightPrimaryAccent,
                                unfocusedBorderColor = MidnightSubtleBorder,
                                focusedTextColor = MidnightPrimaryText,
                                unfocusedTextColor = MidnightPrimaryText,
                                focusedContainerColor = MidnightSecondarySurface,
                                unfocusedContainerColor = MidnightSecondarySurface
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = reminderExpanded,
                            onDismissRequest = { reminderExpanded = false }
                        ) {
                            reminderOptions.forEach { (minutes, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.onReminderMinutesChange(minutes)
                                        reminderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Favorite Option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = if (uiState.isFavorite) Color(0xFFF17D91) else MidnightMutedText
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Mark as Favorite",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MidnightPrimaryText
                    )
                }

                Switch(
                    checked = uiState.isFavorite,
                    onCheckedChange = { viewModel.onFavoriteChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFF17D91),
                        checkedTrackColor = Color(0xFFF17D91).copy(alpha = 0.25f),
                        uncheckedThumbColor = MidnightMutedText,
                        uncheckedTrackColor = MidnightSecondarySurface
                    )
                )
            }

            // Description / Notes Input
            Column {
                Text(
                    text = "DESCRIPTION / NOTES (OPTIONAL)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MidnightPrimaryAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    placeholder = { Text("Add reminders, packing checklist, celebration ideas...", color = MidnightMutedText) },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MidnightPrimaryAccent,
                        unfocusedBorderColor = MidnightSubtleBorder,
                        focusedTextColor = MidnightPrimaryText,
                        unfocusedTextColor = MidnightPrimaryText,
                        focusedContainerColor = MidnightSecondarySurface,
                        unfocusedContainerColor = MidnightSecondarySurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save Action Button
            Button(
                onClick = { viewModel.saveEvent() },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MidnightPrimaryAccent,
                    contentColor = MidnightBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isEditMode) "Save Changes" else "Create Countdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
