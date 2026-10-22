package com.timezy.widget.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==========================================
// Static Constant Color Palettes
// ==========================================

// --- Dark (Midnight Obsidian & Champagne Gold) ---
val DarkBackground = Color(0xFF08090B)
val DarkCard = Color(0xFF111318)
val DarkSecondarySurface = Color(0xFF171922)
val DarkSubtleBorder = Color(0xFF24262D)
val DarkPrimaryText = Color(0xFFF5F5F7)
val DarkSecondaryText = Color(0xFF9A9DA8)
val DarkMutedText = Color(0xFF6E7280)

val DarkPrimaryAccent = Color(0xFFF5C77E) // Champagne Gold
val DarkAccentBackground = Color(0xFF281F12)
val DarkCardBorder = Color(0xFF453620)
val DarkAccentGradientStart = Color(0xFF382914)
val DarkDestructive = Color(0xFFE87989)

// --- Light (Clean Modern Material Design) ---
val LightBackground = Color(0xFFF6F7FB)
val LightCard = Color(0xFFFFFFFF)
val LightSecondarySurface = Color(0xFFEDF0F5)
val LightSubtleBorder = Color(0xFFE0E5EE)
val LightPrimaryText = Color(0xFF171923)
val LightSecondaryText = Color(0xFF606575)
val LightMutedText = Color(0xFF747B8A)

val LightPrimaryAccent = Color(0xFFC28A2C) // Rich Warm Gold with accessible contrast on light surfaces
val LightAccentBackground = Color(0xFFFEF3C7) // Soft amber/gold tint
val LightCardBorder = Color(0xFFE0E5EE)
val LightAccentGradientStart = Color(0xFFFFF7ED)
val LightDestructive = Color(0xFFDC2626)

// ==========================================
// Category Colors
// ==========================================
val DarkCategoryBirthdayAccent = Color(0xFF7A96EC)
val DarkCategoryBirthdayBg = Color(0xFF161D2B)
val DarkCategoryBirthdayBadgeBg = Color(0xFF161B28)
val DarkCategoryBirthdayBorder = Color(0xFF242E45)

val LightCategoryBirthdayAccent = Color(0xFF2563EB)
val LightCategoryBirthdayBg = Color(0xFFEFF6FF)
val LightCategoryBirthdayBadgeBg = Color(0xFFDBEAFE)
val LightCategoryBirthdayBorder = Color(0xFFBFDBFE)

val DarkCategoryHolidayAccent = Color(0xFFFFA6B8)
val DarkCategoryHolidayBg = Color(0xFF281B24)
val DarkCategoryHolidayBadgeBg = Color(0xFF22161E)
val DarkCategoryHolidayBorder = Color(0xFF38212D)

val LightCategoryHolidayAccent = Color(0xFFDB2777)
val LightCategoryHolidayBg = Color(0xFFFDF2F8)
val LightCategoryHolidayBadgeBg = Color(0xFFFCE7F3)
val LightCategoryHolidayBorder = Color(0xFFFBCFE8)

val DarkCategoryTravelAccent = Color(0xFFF5C77E)
val DarkCategoryTravelBg = Color(0xFF2A2012)
val DarkCategoryTravelBadgeBg = Color(0xFF261D13)
val DarkCategoryTravelBorder = Color(0xFF453620)

val LightCategoryTravelAccent = Color(0xFFD97706)
val LightCategoryTravelBg = Color(0xFFFFFBEB)
val LightCategoryTravelBadgeBg = Color(0xFFFEF3C7)
val LightCategoryTravelBorder = Color(0xFFFDE68A)

// Status / Feedback
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)

// ==========================================
// Semantic Dynamic Colors (Theme-Adaptive)
// ==========================================
val MidnightBackground: Color
    @Composable
    get() = LocalCountdownColors.current.background

val MidnightCard: Color
    @Composable
    get() = LocalCountdownColors.current.card

val MidnightSecondarySurface: Color
    @Composable
    get() = LocalCountdownColors.current.surfaceVariant

val MidnightSubtleBorder: Color
    @Composable
    get() = LocalCountdownColors.current.border

val MidnightPrimaryText: Color
    @Composable
    get() = LocalCountdownColors.current.textPrimary

val MidnightSecondaryText: Color
    @Composable
    get() = LocalCountdownColors.current.textSecondary

val MidnightMutedText: Color
    @Composable
    get() = LocalCountdownColors.current.textMuted

val MidnightPrimaryAccent: Color
    @Composable
    get() = LocalCountdownColors.current.accent

val MidnightAccentBackground: Color
    @Composable
    get() = LocalCountdownColors.current.accentBackground

val GoldPrimaryAccent: Color
    @Composable
    get() = LocalCountdownColors.current.accent

val GoldAccentBackground: Color
    @Composable
    get() = LocalCountdownColors.current.accentBackground

val GoldCardBorder: Color
    @Composable
    get() = LocalCountdownColors.current.cardBorder

val MidnightAnniversaryAccent: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) Color(0xFF79D6B0) else Color(0xFF059669)

val MidnightHolidayAccent: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) Color(0xFFFFA6B8) else Color(0xFFDB2777)

// Dynamic Category Colors
val CategoryBirthdayAccent: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryBirthdayAccent else LightCategoryBirthdayAccent

val CategoryBirthdayBg: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryBirthdayBg else LightCategoryBirthdayBg

val CategoryBirthdayBadgeBg: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryBirthdayBadgeBg else LightCategoryBirthdayBadgeBg

val CategoryBirthdayBorder: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryBirthdayBorder else LightCategoryBirthdayBorder

val CategoryHolidayAccent: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryHolidayAccent else LightCategoryHolidayAccent

val CategoryHolidayBg: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryHolidayBg else LightCategoryHolidayBg

val CategoryHolidayBadgeBg: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryHolidayBadgeBg else LightCategoryHolidayBadgeBg

val CategoryHolidayBorder: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryHolidayBorder else LightCategoryHolidayBorder

val CategoryTravelAccent: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryTravelAccent else LightCategoryTravelAccent

val CategoryTravelBg: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryTravelBg else LightCategoryTravelBg

val CategoryTravelBadgeBg: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryTravelBadgeBg else LightCategoryTravelBadgeBg

val CategoryTravelBorder: Color
    @Composable
    get() = if (LocalCountdownColors.current.isDark) DarkCategoryTravelBorder else LightCategoryTravelBorder

// Legacy Aliases
val ObsidianBackground: Color @Composable get() = MidnightBackground
val ObsidianSurface: Color @Composable get() = MidnightCard
val ObsidianSurfaceVariant: Color @Composable get() = MidnightSecondarySurface
val ObsidianCard: Color @Composable get() = MidnightCard
val ObsidianBorder: Color @Composable get() = MidnightSubtleBorder

val CharcoalBackground: Color @Composable get() = MidnightBackground
val CharcoalSurface: Color @Composable get() = MidnightCard
val CharcoalSurfaceVariant: Color @Composable get() = MidnightSecondarySurface
val CharcoalCard: Color @Composable get() = MidnightCard
val CharcoalCardBorder: Color @Composable get() = MidnightSubtleBorder
val CharcoalDivider: Color @Composable get() = MidnightSubtleBorder

val PurplePrimary: Color @Composable get() = MidnightPrimaryAccent
val PurplePrimaryVariant: Color @Composable get() = if (LocalCountdownColors.current.isDark) Color(0xFFA27BFF) else Color(0xFF7C3AED)
val PurpleLight: Color @Composable get() = MidnightPrimaryAccent
val PurpleSubtle: Color @Composable get() = MidnightAccentBackground
val PurpleGlow = Color(0x33B69AFF)

val CyanAccent: Color @Composable get() = MidnightAnniversaryAccent
val CyanGlow = Color(0x3379D6B0)
val SunsetOrange = Color(0xFFF59E0B)
val EmeraldGreen: Color @Composable get() = MidnightAnniversaryAccent
val RoseCrimson: Color @Composable get() = MidnightHolidayAccent
val AmberGold: Color @Composable get() = GoldPrimaryAccent
val NeonPink: Color @Composable get() = MidnightHolidayAccent

val TextPrimaryDark = DarkPrimaryText
val TextSecondaryDark = DarkSecondaryText
val TextTertiaryDark = DarkMutedText

val TextPrimaryLight = LightPrimaryText
val TextSecondaryLight = LightSecondaryText
val TextTertiaryLight = LightMutedText
val LightDivider = LightSubtleBorder
