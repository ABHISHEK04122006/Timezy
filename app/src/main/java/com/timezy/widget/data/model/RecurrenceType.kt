package com.timezy.widget.data.model

enum class RecurrenceType(val displayName: String) {
    NONE("Never"),
    DAILY("Every Day"),
    WEEKLY("Every Week"),
    MONTHLY("Every Month"),
    YEARLY("Every Year");

    companion object {
        fun fromName(name: String?): RecurrenceType {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}
