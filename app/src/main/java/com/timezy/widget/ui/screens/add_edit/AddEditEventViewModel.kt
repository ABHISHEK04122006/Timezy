package com.timezy.widget.ui.screens.add_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timezy.widget.CountdownApplication
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.data.model.RecurrenceType
import com.timezy.widget.data.repository.EventRepository
import com.timezy.widget.notifications.ReminderScheduler
import com.timezy.widget.utils.DateTimeUtils
import com.timezy.widget.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val targetDate: LocalDate = LocalDate.now().plusDays(7),
    val hasSpecificTime: Boolean = false,
    val targetTime: LocalTime = LocalTime.of(12, 0),
    val category: EventCategory = EventCategory.OTHER,
    val theme: EventTheme = EventTheme.NEON_CYAN,
    val isFavorite: Boolean = false,
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val reminderMinutesBefore: Int = 1440, // 1 day before
    val isNotificationEnabled: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val isEditMode: Boolean = false,
    val eventId: Long? = null,
    val titleError: String? = null,
    val isSaved: Boolean = false
)

class AddEditEventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository = (application as CountdownApplication).repository

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: Long?) {
        if (eventId == null || eventId <= 0L) return

        viewModelScope.launch {
            val event = repository.getEventById(eventId) ?: return@launch
            val date = DateTimeUtils.toLocalDate(event.targetDateTimeEpochMillis)
            val time = DateTimeUtils.toLocalTime(event.targetDateTimeEpochMillis)

            _uiState.update {
                it.copy(
                    eventId = event.id,
                    isEditMode = true,
                    title = event.title,
                    description = event.description,
                    targetDate = date,
                    hasSpecificTime = event.hasSpecificTime,
                    targetTime = time,
                    category = event.category,
                    theme = event.theme,
                    isFavorite = event.isFavorite,
                    recurrence = event.recurrence,
                    reminderMinutesBefore = event.reminderMinutesBefore,
                    isNotificationEnabled = event.isNotificationEnabled,
                    createdAtEpochMillis = if (event.createdAtEpochMillis > 0L) event.createdAtEpochMillis else System.currentTimeMillis()
                )
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc) }
    }

    fun onDateChange(newDate: LocalDate) {
        _uiState.update { it.copy(targetDate = newDate) }
    }

    fun onHasSpecificTimeChange(hasTime: Boolean) {
        _uiState.update { it.copy(hasSpecificTime = hasTime) }
    }

    fun onTimeChange(newTime: LocalTime) {
        _uiState.update { it.copy(targetTime = newTime) }
    }

    fun onCategoryChange(newCategory: EventCategory) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onThemeChange(newTheme: EventTheme) {
        _uiState.update { it.copy(theme = newTheme) }
    }

    fun onFavoriteChange(isFav: Boolean) {
        _uiState.update { it.copy(isFavorite = isFav) }
    }

    fun onRecurrenceChange(newRecurrence: RecurrenceType) {
        _uiState.update { it.copy(recurrence = newRecurrence) }
    }

    fun onReminderMinutesChange(minutes: Int) {
        _uiState.update { it.copy(reminderMinutesBefore = minutes) }
    }

    fun onNotificationEnabledChange(enabled: Boolean) {
        _uiState.update { it.copy(isNotificationEnabled = enabled) }
    }

    fun saveEvent(): Boolean {
        val state = _uiState.value
        if (state.title.trim().isBlank()) {
            _uiState.update { it.copy(titleError = "Please enter an event name") }
            return false
        }

        val epochMillis = DateTimeUtils.toEpochMillis(
            date = state.targetDate,
            time = if (state.hasSpecificTime) state.targetTime else null
        )

        viewModelScope.launch {
            val event = CountdownEvent(
                id = state.eventId ?: 0L,
                title = state.title.trim(),
                description = state.description.trim(),
                targetDateTimeEpochMillis = epochMillis,
                hasSpecificTime = state.hasSpecificTime,
                category = state.category,
                theme = state.theme,
                isFavorite = state.isFavorite,
                recurrence = state.recurrence,
                reminderMinutesBefore = state.reminderMinutesBefore,
                isNotificationEnabled = state.isNotificationEnabled,
                createdAtEpochMillis = if (state.isEditMode && state.createdAtEpochMillis > 0L) state.createdAtEpochMillis else System.currentTimeMillis(),
                updatedAtEpochMillis = System.currentTimeMillis()
            )

            val savedId = if (state.isEditMode) {
                repository.updateEvent(event)
                event.id
            } else {
                repository.insertEvent(event)
            }

            val savedEvent = event.copy(id = savedId)

            // Schedule / update reminder
            ReminderScheduler.scheduleReminder(getApplication(), savedEvent)

            // Update home-screen widgets linked to this event
            WidgetUpdateHelper.updateWidgetsForEvent(getApplication(), savedId)
            WidgetUpdateHelper.updateAllWidgets(getApplication())

            _uiState.update { it.copy(isSaved = true) }
        }

        return true
    }
}
