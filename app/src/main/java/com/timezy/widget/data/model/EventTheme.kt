package com.timezy.widget.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class EventTheme(
    val id: String,
    val displayName: String,
    val primaryColorHex: Long,
    val ringColorHex: String,
    val secondaryColorHex: Long,
    val surfaceColorHex: Long,
    val accentColorHex: Long
) {
    NEON_CYAN(
        id = "neon_cyan",
        displayName = "Blue",
        primaryColorHex = 0xFF20B9E8,
        ringColorHex = "#20B9E8",
        secondaryColorHex = 0xFF0284C7,
        surfaceColorHex = 0xFF0F1E2E,
        accentColorHex = 0xFF38BDF8
    ),
    COSMIC_PURPLE(
        id = "cosmic_purple",
        displayName = "Purple",
        primaryColorHex = 0xFFA78BFA,
        ringColorHex = "#A78BFA",
        secondaryColorHex = 0xFF7C3AED,
        surfaceColorHex = 0xFF1E172E,
        accentColorHex = 0xFFC4B5FD
    ),
    EMERALD_MINT(
        id = "emerald_mint",
        displayName = "Green",
        primaryColorHex = 0xFF34D399,
        ringColorHex = "#34D399",
        secondaryColorHex = 0xFF059669,
        surfaceColorHex = 0xFF0E241D,
        accentColorHex = 0xFF6EE7B7
    ),
    SUNSET_EMBER(
        id = "sunset_ember",
        displayName = "Orange",
        primaryColorHex = 0xFFF59E0B,
        ringColorHex = "#F59E0B",
        secondaryColorHex = 0xFFD97706,
        surfaceColorHex = 0xFF2A1713,
        accentColorHex = 0xFFFBBF24
    ),
    ROSE_VELVET(
        id = "rose_velvet",
        displayName = "Pink",
        primaryColorHex = 0xFFF472B6,
        ringColorHex = "#F472B6",
        secondaryColorHex = 0xFFDB2777,
        surfaceColorHex = 0xFF281119,
        accentColorHex = 0xFFF472B6
    );

    val primaryColor: Color get() = Color(primaryColorHex)
    val secondaryColor: Color get() = Color(secondaryColorHex)
    val surfaceColor: Color get() = Color(surfaceColorHex)
    val accentColor: Color get() = Color(accentColorHex)

    val gradientBrush: Brush
        get() = Brush.linearGradient(
            colors = listOf(primaryColor, secondaryColor)
        )

    val subtleGradientBrush: Brush
        get() = Brush.linearGradient(
            colors = listOf(surfaceColor, Color(0xFF13151B))
        )

    companion object {
        fun fromId(id: String?): EventTheme {
            return when (id?.lowercase()) {
                "neon_cyan", "blue" -> NEON_CYAN
                "cosmic_purple", "purple" -> COSMIC_PURPLE
                "emerald_mint", "green", "electric_lime" -> EMERALD_MINT
                "sunset_ember", "orange", "golden_luxe" -> SUNSET_EMBER
                "rose_velvet", "pink" -> ROSE_VELVET
                else -> NEON_CYAN
            }
        }
    }
}
