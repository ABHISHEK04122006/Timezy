package com.timezy.widget.ui.screens.customization

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timezy.widget.CountdownApplication
import com.timezy.widget.data.model.CircleStyle
import com.timezy.widget.data.model.CountdownEvent
import com.timezy.widget.data.model.EventCategory
import com.timezy.widget.data.model.EventTheme
import com.timezy.widget.data.preferences.AppPreferences
import com.timezy.widget.widget.WidgetUpdateHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomizationUiState(
    val selectedTheme: EventTheme = EventTheme.NEON_CYAN,
    val circleStyle: CircleStyle = CircleStyle.LIQUID_GLASS,
    val isLiquidGlassEnabled: Boolean = true,
    val isTransparent: Boolean = true,
    val showSeconds: Boolean = true,
    val previewSize: WidgetPreviewSize = WidgetPreviewSize.COUNTDOWN_CIRCLE,
    val sampleEvent: CountdownEvent = CountdownEvent(
        id = -1L,
        title = "Preview Event",
        description = "Live countdown preview",
        targetDateTimeEpochMillis = System.currentTimeMillis() + (30L * 86400000L),
        hasSpecificTime = false,
        category = EventCategory.OTHER,
        theme = EventTheme.NEON_CYAN
    ),
    val hasRealEvents: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val installedWidgets: List<WidgetUpdateHelper.InstalledWidgetInfo> = emptyList(),
    val selectedWidgetId: Int? = null // null means "Default / New Widgets"
)

enum class WidgetPreviewSize(val label: String) {
    COUNTDOWN_CIRCLE("Countdown"),
    COUNTDOWN_MEDIUM("Countdown (medium)");

    companion object {
        val CIRCLE_GLASS: WidgetPreviewSize get() = COUNTDOWN_CIRCLE
        val COMPACT: WidgetPreviewSize get() = COUNTDOWN_CIRCLE
        val TRANSPARENT_4X2: WidgetPreviewSize get() = COUNTDOWN_MEDIUM
        val LARGE: WidgetPreviewSize get() = COUNTDOWN_MEDIUM
    }
}

class CustomizationViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences: AppPreferences = (application as CountdownApplication).preferences
    private val repository = (application as CountdownApplication).repository

    private var userExplicitlySelectedTemplate = false

    private val _uiState = MutableStateFlow(
        CustomizationUiState(
            selectedTheme = preferences.getWidgetAccentTheme(),
            circleStyle = preferences.getWidgetStyle(),
            isLiquidGlassEnabled = preferences.isWidgetLiquidGlassEnabled(),
            isTransparent = preferences.isWidgetLiquidGlassEnabled(),
            showSeconds = preferences.isWidgetShowSecondsEnabled()
        )
    )
    val uiState: StateFlow<CustomizationUiState> = _uiState.asStateFlow()

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    val nowMillis: StateFlow<Long> = _nowMillis.asStateFlow()

    init {
        // Live second ticker for live preview animations
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _nowMillis.value = System.currentTimeMillis()
            }
        }

        // Load active widgets from home screen
        refreshInstalledWidgets()

        // Observe real events to display in the live preview
        viewModelScope.launch {
            repository.getAllEventsFlow().collect { events ->
                val currentSelectedWidgetId = _uiState.value.selectedWidgetId
                if (currentSelectedWidgetId == null) {
                    val actualEvent = events.firstOrNull { it.isFavorite } ?: events.firstOrNull()
                    if (actualEvent != null) {
                        _uiState.update { current ->
                            current.copy(
                                sampleEvent = actualEvent.copy(theme = current.selectedTheme),
                                hasRealEvents = true
                            )
                        }
                    } else {
                        _uiState.update { current ->
                            current.copy(
                                sampleEvent = CountdownEvent(
                                    id = -1L,
                                    title = "New Year's Eve",
                                    description = "Live countdown preview",
                                    targetDateTimeEpochMillis = System.currentTimeMillis() + (72L * 86400000L),
                                    hasSpecificTime = false,
                                    category = EventCategory.OTHER,
                                    theme = current.selectedTheme
                                ),
                                hasRealEvents = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun refreshInstalledWidgets() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            val list = WidgetUpdateHelper.getInstalledWidgetsInfo(app)
            _uiState.update { current ->
                val targetId = when {
                    current.selectedWidgetId != null && list.any { it.appWidgetId == current.selectedWidgetId } -> current.selectedWidgetId
                    userExplicitlySelectedTemplate -> null
                    list.isNotEmpty() -> list.first().appWidgetId
                    else -> null
                }

                if (targetId != null) {
                    val widget = list.firstOrNull { it.appWidgetId == targetId }
                    val theme = WidgetUpdateHelper.getWidgetColorTheme(app, targetId)
                    val style = WidgetUpdateHelper.getWidgetCircleStyle(app, targetId)
                    val showSecs = WidgetUpdateHelper.isWidgetShowSecondsEnabled(app, targetId)
                    val glass = WidgetUpdateHelper.isWidgetLiquidGlassEnabled(app, targetId)
                    val event = widget?.event ?: current.sampleEvent

                    current.copy(
                        installedWidgets = list,
                        selectedWidgetId = targetId,
                        selectedTheme = theme,
                        circleStyle = style,
                        showSeconds = showSecs,
                        isLiquidGlassEnabled = glass,
                        sampleEvent = event.copy(theme = theme),
                        previewSize = if (widget?.isMedium == true) WidgetPreviewSize.COUNTDOWN_MEDIUM else WidgetPreviewSize.COUNTDOWN_CIRCLE
                    )
                } else {
                    current.copy(
                        installedWidgets = list,
                        selectedWidgetId = null
                    )
                }
            }
        }
    }

    fun selectWidget(appWidgetId: Int?) {
        val app = getApplication<Application>()
        if (appWidgetId == null) {
            userExplicitlySelectedTemplate = true
            // Revert to global defaults
            val defaultTheme = preferences.getWidgetAccentTheme()
            val defaultStyle = preferences.getWidgetStyle()
            val defaultSeconds = preferences.isWidgetShowSecondsEnabled()
            val defaultGlass = preferences.isWidgetLiquidGlassEnabled()
            _uiState.update { current ->
                current.copy(
                    selectedWidgetId = null,
                    selectedTheme = defaultTheme,
                    circleStyle = defaultStyle,
                    showSeconds = defaultSeconds,
                    isLiquidGlassEnabled = defaultGlass,
                    sampleEvent = current.sampleEvent.copy(theme = defaultTheme)
                )
            }
        } else {
            userExplicitlySelectedTemplate = false
            val widget = _uiState.value.installedWidgets.firstOrNull { it.appWidgetId == appWidgetId }
            val theme = WidgetUpdateHelper.getWidgetColorTheme(app, appWidgetId)
            val style = WidgetUpdateHelper.getWidgetCircleStyle(app, appWidgetId)
            val showSecs = WidgetUpdateHelper.isWidgetShowSecondsEnabled(app, appWidgetId)
            val glass = WidgetUpdateHelper.isWidgetLiquidGlassEnabled(app, appWidgetId)
            val event = widget?.event ?: _uiState.value.sampleEvent

            _uiState.update { current ->
                current.copy(
                    selectedWidgetId = appWidgetId,
                    selectedTheme = theme,
                    circleStyle = style,
                    showSeconds = showSecs,
                    isLiquidGlassEnabled = glass,
                    sampleEvent = event.copy(theme = theme),
                    previewSize = if (widget?.isMedium == true) WidgetPreviewSize.COUNTDOWN_MEDIUM else WidgetPreviewSize.COUNTDOWN_CIRCLE
                )
            }
        }
    }

    fun selectTheme(theme: EventTheme) {
        _uiState.update { current ->
            current.copy(
                selectedTheme = theme,
                sampleEvent = current.sampleEvent.copy(theme = theme)
            )
        }
    }

    fun setCircleStyle(style: CircleStyle) {
        val app = getApplication<Application>()
        val targetId = _uiState.value.selectedWidgetId
            ?: if (!userExplicitlySelectedTemplate) _uiState.value.installedWidgets.firstOrNull()?.appWidgetId else null

        if (targetId != null) {
            WidgetUpdateHelper.setWidgetStyle(app, targetId, style)
            WidgetUpdateHelper.updateSpecificWidget(app, targetId)
            _uiState.update { current ->
                val updatedWidgets = current.installedWidgets.map {
                    if (it.appWidgetId == targetId) it.copy(circleStyle = style) else it
                }
                current.copy(
                    selectedWidgetId = targetId,
                    circleStyle = style,
                    installedWidgets = updatedWidgets
                )
            }
        } else {
            viewModelScope.launch {
                preferences.setWidgetStyle(style)
            }
            _uiState.update { it.copy(circleStyle = style) }
        }
    }

    fun setLiquidGlassEnabled(enabled: Boolean) {
        val app = getApplication<Application>()
        val targetId = _uiState.value.selectedWidgetId
            ?: if (!userExplicitlySelectedTemplate) _uiState.value.installedWidgets.firstOrNull()?.appWidgetId else null

        if (targetId != null) {
            WidgetUpdateHelper.setWidgetLiquidGlass(app, targetId, enabled)
            WidgetUpdateHelper.updateSpecificWidget(app, targetId)
            _uiState.update { current ->
                val updatedWidgets = current.installedWidgets.map {
                    if (it.appWidgetId == targetId) it.copy(isLiquidGlass = enabled) else it
                }
                current.copy(
                    selectedWidgetId = targetId,
                    isLiquidGlassEnabled = enabled,
                    isTransparent = enabled,
                    installedWidgets = updatedWidgets
                )
            }
        } else {
            viewModelScope.launch {
                preferences.setWidgetLiquidGlassEnabled(enabled)
                preferences.setWidgetTransparency(enabled)
            }
            _uiState.update { it.copy(isLiquidGlassEnabled = enabled, isTransparent = enabled) }
        }
    }

    fun setTransparency(isTransparent: Boolean) {
        setLiquidGlassEnabled(isTransparent)
    }

    fun setShowSeconds(show: Boolean) {
        val app = getApplication<Application>()
        val targetId = _uiState.value.selectedWidgetId
            ?: if (!userExplicitlySelectedTemplate) _uiState.value.installedWidgets.firstOrNull()?.appWidgetId else null

        if (targetId != null) {
            WidgetUpdateHelper.setWidgetShowSeconds(app, targetId, show)
            WidgetUpdateHelper.updateSpecificWidget(app, targetId)
            _uiState.update { current ->
                val updatedWidgets = current.installedWidgets.map {
                    if (it.appWidgetId == targetId) it.copy(showSeconds = show) else it
                }
                current.copy(
                    selectedWidgetId = targetId,
                    showSeconds = show,
                    installedWidgets = updatedWidgets
                )
            }
        } else {
            viewModelScope.launch {
                preferences.setShowSeconds(show)
            }
            _uiState.update { it.copy(showSeconds = show) }
        }
    }

    fun setPreviewSize(size: WidgetPreviewSize) {
        _uiState.update { it.copy(previewSize = size) }
    }

    fun applyCustomizations() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val app = getApplication<Application>()
            val targetId = currentState.selectedWidgetId
                ?: if (!userExplicitlySelectedTemplate) currentState.installedWidgets.firstOrNull()?.appWidgetId else null

            if (targetId != null) {
                // Save styling specifically for the selected widget instance
                WidgetUpdateHelper.setWidgetStyle(app, targetId, currentState.circleStyle)
                WidgetUpdateHelper.setWidgetShowSeconds(app, targetId, currentState.showSeconds)
                WidgetUpdateHelper.setWidgetLiquidGlass(app, targetId, currentState.isLiquidGlassEnabled)

                // Update only this widget on the home screen
                WidgetUpdateHelper.updateSpecificWidget(app, targetId)
            } else {
                // Save as default template for future widgets
                preferences.setWidgetStyle(currentState.circleStyle)
                preferences.setWidgetAccentTheme(currentState.selectedTheme)
                preferences.setWidgetLiquidGlassEnabled(currentState.isLiquidGlassEnabled)
                preferences.setWidgetTransparency(currentState.isLiquidGlassEnabled)
                preferences.setShowSeconds(currentState.showSeconds)
            }

            // Refresh installed widgets list to show updated colors
            val updatedList = WidgetUpdateHelper.getInstalledWidgetsInfo(app)
            _uiState.update {
                it.copy(
                    installedWidgets = updatedList,
                    selectedWidgetId = targetId,
                    isSavedSuccess = true
                )
            }

            delay(2500)
            _uiState.update { it.copy(isSavedSuccess = false) }
        }
    }
}
