package com.timezy.widget.data.model

enum class FilterOption(val displayName: String) {
    ALL("All Events"),
    UPCOMING("Upcoming"),
    TODAY("Today"),
    PAST("Past"),
    FAVORITES("Favorites");

    companion object {
        fun fromName(name: String?): FilterOption {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: ALL
        }
    }
}
