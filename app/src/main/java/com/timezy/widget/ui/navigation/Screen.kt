package com.timezy.widget.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object AddEvent : Screen("add_edit")
    object EditEvent : Screen("add_edit?eventId={eventId}") {
        fun createRoute(eventId: Long) = "add_edit?eventId=$eventId"
    }
    object Detail : Screen("detail/{eventId}") {
        fun createRoute(eventId: Long) = "detail/$eventId"
    }
    object WidgetCustomization : Screen("widget_customization")
    object Settings : Screen("settings")
}
