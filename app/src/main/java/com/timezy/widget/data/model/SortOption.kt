package com.timezy.widget.data.model

enum class SortOption(val displayName: String) {
    TARGET_DATE_ASC("Event Date (Soonest)"),
    TARGET_DATE_DESC("Event Date (Furthest)"),
    CREATED_AT_DESC("Recently Added"),
    TITLE_ASC("Alphabetical (A - Z)");

    companion object {
        fun fromName(name: String?): SortOption {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: TARGET_DATE_ASC
        }
    }
}
