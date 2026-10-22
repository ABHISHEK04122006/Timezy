package com.timezy.widget.data.model

import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.timezy.widget.ui.theme.MidnightAnniversaryAccent
import com.timezy.widget.ui.theme.MidnightHolidayAccent
import com.timezy.widget.ui.theme.MidnightPrimaryAccent

enum class EventCategory(
    val id: String,
    val displayName: String,
    val defaultIconName: String
) {
    BIRTHDAY("birthday", "Birthday", "Cake"),
    ANNIVERSARY("anniversary", "Anniversary", "Favorite"),
    HOLIDAY("holiday", "Holiday", "Celebration"),
    TRAVEL("travel", "Travel", "Flight"),
    CAREER("career", "Career & Work", "Work"),
    EDUCATION("education", "Education", "School"),
    GAMING("gaming", "Gaming", "SportsEsports"),
    LIFESTYLE("lifestyle", "Life Event", "Home"),
    PETS("pets", "Pets", "Pets"),
    EXAM("exam", "Exam", "MenuBook"),
    OTHER("other", "General", "MoreHoriz");

    fun getIcon(): ImageVector {
        return when (this) {
            BIRTHDAY -> Icons.Default.Cake
            ANNIVERSARY -> Icons.Default.Favorite
            HOLIDAY -> Icons.Default.Celebration
            TRAVEL -> Icons.Default.Flight
            CAREER -> Icons.Default.Work
            EDUCATION -> Icons.Default.School
            GAMING -> Icons.Default.SportsEsports
            LIFESTYLE -> Icons.Default.Home
            PETS -> Icons.Default.Pets
            EXAM -> Icons.AutoMirrored.Filled.MenuBook
            OTHER -> Icons.Default.MoreHoriz
        }
    }

    @Composable
    fun getAccentColor(): Color {
        return when (this) {
            BIRTHDAY -> com.timezy.widget.ui.theme.CategoryBirthdayAccent
            HOLIDAY -> com.timezy.widget.ui.theme.CategoryHolidayAccent
            TRAVEL -> com.timezy.widget.ui.theme.CategoryTravelAccent
            ANNIVERSARY -> com.timezy.widget.ui.theme.CategoryHolidayAccent
            CAREER -> if (com.timezy.widget.ui.theme.CountdownTheme.colors.isDark) Color(0xFF64B5F6) else Color(0xFF0284C7)
            EDUCATION -> if (com.timezy.widget.ui.theme.CountdownTheme.colors.isDark) Color(0xFFFFB74D) else Color(0xFFD97706)
            GAMING -> if (com.timezy.widget.ui.theme.CountdownTheme.colors.isDark) Color(0xFFBA68C8) else Color(0xFF9333EA)
            PETS -> if (com.timezy.widget.ui.theme.CountdownTheme.colors.isDark) Color(0xFF81C784) else Color(0xFF16A34A)
            else -> com.timezy.widget.ui.theme.MidnightPrimaryAccent
        }
    }

    @Composable
    fun getContainerColor(): Color {
        return when (this) {
            BIRTHDAY -> com.timezy.widget.ui.theme.CategoryBirthdayBg
            HOLIDAY -> com.timezy.widget.ui.theme.CategoryHolidayBg
            TRAVEL -> com.timezy.widget.ui.theme.CategoryTravelBg
            ANNIVERSARY -> com.timezy.widget.ui.theme.CategoryHolidayBg
            else -> com.timezy.widget.ui.theme.MidnightSecondarySurface
        }
    }

    @Composable
    fun getBadgeColor(): Color {
        return when (this) {
            BIRTHDAY -> com.timezy.widget.ui.theme.CategoryBirthdayBadgeBg
            HOLIDAY -> com.timezy.widget.ui.theme.CategoryHolidayBadgeBg
            TRAVEL -> com.timezy.widget.ui.theme.CategoryTravelBadgeBg
            ANNIVERSARY -> com.timezy.widget.ui.theme.CategoryHolidayBadgeBg
            else -> com.timezy.widget.ui.theme.MidnightSecondarySurface
        }
    }

    @Composable
    fun getBorderColor(): Color {
        return when (this) {
            BIRTHDAY -> com.timezy.widget.ui.theme.CategoryBirthdayBorder
            HOLIDAY -> com.timezy.widget.ui.theme.CategoryHolidayBorder
            TRAVEL -> com.timezy.widget.ui.theme.CategoryTravelBorder
            ANNIVERSARY -> com.timezy.widget.ui.theme.CategoryHolidayBorder
            else -> com.timezy.widget.ui.theme.MidnightSubtleBorder
        }
    }

    @Composable
    fun getBadgeNumberColor(): Color {
        val isDark = com.timezy.widget.ui.theme.CountdownTheme.colors.isDark
        if (isDark) return Color.White
        return when (this) {
            BIRTHDAY -> Color(0xFF1E40AF) // Dark Blue for pale blue badge
            HOLIDAY, ANNIVERSARY -> Color(0xFF9D174D) // Dark Pink for pale pink badge
            TRAVEL -> Color(0xFF92400E) // Dark Amber for pale amber badge
            CAREER -> Color(0xFF075985)
            EDUCATION -> Color(0xFF9A3412)
            GAMING -> Color(0xFF6B21A8)
            PETS -> Color(0xFF166534)
            else -> com.timezy.widget.ui.theme.CountdownTheme.colors.textPrimary
        }
    }

    @Composable
    fun getBadgeLabelColor(): Color {
        val isDark = com.timezy.widget.ui.theme.CountdownTheme.colors.isDark
        if (isDark) return getAccentColor().copy(alpha = 0.9f)
        return when (this) {
            BIRTHDAY -> Color(0xFF2563EB) // Medium Blue
            HOLIDAY, ANNIVERSARY -> Color(0xFFBE185D) // Medium Pink
            TRAVEL -> Color(0xFFB45309) // Medium Amber
            CAREER -> Color(0xFF0284C7)
            EDUCATION -> Color(0xFFC2410C)
            GAMING -> Color(0xFF7E22CE)
            PETS -> Color(0xFF15803D)
            else -> com.timezy.widget.ui.theme.CountdownTheme.colors.textSecondary
        }
    }

    companion object {
        fun fromId(id: String): EventCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }
    }
}
