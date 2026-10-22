package com.timezy.widget.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "countdown_settings")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // SYSTEM, DARK, LIGHT
        val KEY_FEATURED_EVENT_ID = longPreferencesKey("featured_event_id")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val KEY_SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val KEY_DEFAULT_SORT = stringPreferencesKey("default_sort")
        val KEY_WIDGET_TRANSPARENCY = booleanPreferencesKey("widget_transparency")
        val KEY_WIDGET_STYLE = stringPreferencesKey("widget_style")
        val KEY_WIDGET_ACCENT_COLOR = stringPreferencesKey("widget_accent_color")
        val KEY_WIDGET_LIQUID_GLASS = booleanPreferencesKey("widget_liquid_glass")
    }

    suspend fun isNotificationsEnabled(): Boolean {
        return context.dataStore.data.first()[KEY_NOTIFICATIONS_ENABLED] ?: true
    }

    private val fastPrefs = context.getSharedPreferences("countdown_theme_fast", Context.MODE_PRIVATE)

    fun getInitialThemeMode(): String {
        return fastPrefs.getString(KEY_THEME_MODE.name, "SYSTEM") ?: "SYSTEM"
    }

    fun getWidgetStyle(): com.timezy.widget.data.model.CircleStyle {
        val styleName = fastPrefs.getString(KEY_WIDGET_STYLE.name, com.timezy.widget.data.model.CircleStyle.LIQUID_GLASS.name)
        return com.timezy.widget.data.model.CircleStyle.fromName(styleName)
    }

    fun getWidgetAccentTheme(): com.timezy.widget.data.model.EventTheme {
        val colorId = fastPrefs.getString(KEY_WIDGET_ACCENT_COLOR.name, com.timezy.widget.data.model.EventTheme.NEON_CYAN.id)
        return com.timezy.widget.data.model.EventTheme.fromId(colorId)
    }

    fun isWidgetLiquidGlassEnabled(): Boolean {
        return fastPrefs.getBoolean(KEY_WIDGET_LIQUID_GLASS.name, true)
    }

    fun isWidgetShowSecondsEnabled(): Boolean {
        return fastPrefs.getBoolean(KEY_SHOW_SECONDS.name, true)
    }

    val themeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: fastPrefs.getString(KEY_THEME_MODE.name, "SYSTEM") ?: "SYSTEM"
    }

    val featuredEventIdFlow: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[KEY_FEATURED_EVENT_ID]
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
    }

    val showSecondsFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SHOW_SECONDS] ?: fastPrefs.getBoolean(KEY_SHOW_SECONDS.name, true)
    }

    val defaultSortFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_SORT] ?: "TARGET_DATE_ASC"
    }

    val widgetTransparencyFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_WIDGET_TRANSPARENCY] ?: false
    }

    val widgetStyleFlow: Flow<com.timezy.widget.data.model.CircleStyle> = context.dataStore.data.map { preferences ->
        val name = preferences[KEY_WIDGET_STYLE] ?: fastPrefs.getString(KEY_WIDGET_STYLE.name, com.timezy.widget.data.model.CircleStyle.LIQUID_GLASS.name)
        com.timezy.widget.data.model.CircleStyle.fromName(name)
    }

    val widgetAccentThemeFlow: Flow<com.timezy.widget.data.model.EventTheme> = context.dataStore.data.map { preferences ->
        val id = preferences[KEY_WIDGET_ACCENT_COLOR] ?: fastPrefs.getString(KEY_WIDGET_ACCENT_COLOR.name, com.timezy.widget.data.model.EventTheme.NEON_CYAN.id)
        com.timezy.widget.data.model.EventTheme.fromId(id)
    }

    val widgetLiquidGlassFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_WIDGET_LIQUID_GLASS] ?: fastPrefs.getBoolean(KEY_WIDGET_LIQUID_GLASS.name, true)
    }

    suspend fun setThemeMode(mode: String) {
        fastPrefs.edit().putString(KEY_THEME_MODE.name, mode).apply()
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setFeaturedEventId(id: Long?) {
        context.dataStore.edit { preferences ->
            if (id != null) {
                preferences[KEY_FEATURED_EVENT_ID] = id
            } else {
                preferences.remove(KEY_FEATURED_EVENT_ID)
            }
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setShowSeconds(show: Boolean) {
        fastPrefs.edit().putBoolean(KEY_SHOW_SECONDS.name, show).apply()
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOW_SECONDS] = show
        }
    }

    suspend fun setDefaultSort(sort: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_SORT] = sort
        }
    }

    suspend fun setWidgetTransparency(transparent: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_WIDGET_TRANSPARENCY] = transparent
        }
    }

    suspend fun setWidgetStyle(style: com.timezy.widget.data.model.CircleStyle) {
        fastPrefs.edit().putString(KEY_WIDGET_STYLE.name, style.name).apply()
        context.dataStore.edit { preferences ->
            preferences[KEY_WIDGET_STYLE] = style.name
        }
    }

    suspend fun setWidgetAccentTheme(theme: com.timezy.widget.data.model.EventTheme) {
        fastPrefs.edit().putString(KEY_WIDGET_ACCENT_COLOR.name, theme.id).apply()
        context.dataStore.edit { preferences ->
            preferences[KEY_WIDGET_ACCENT_COLOR] = theme.id
        }
    }

    suspend fun setWidgetLiquidGlassEnabled(enabled: Boolean) {
        fastPrefs.edit().putBoolean(KEY_WIDGET_LIQUID_GLASS.name, enabled).apply()
        context.dataStore.edit { preferences ->
            preferences[KEY_WIDGET_LIQUID_GLASS] = enabled
        }
    }
}
