package com.timezy.widget.data.model

enum class CircleStyle(val displayName: String) {
    LIQUID_GLASS("Liquid Glass"),
    PURE_WHITE("Pure White"),
    SMOKED_GLASS("Smoked Glass");

    companion object {
        val OBSIDIAN_GLASS: CircleStyle get() = SMOKED_GLASS

        fun fromName(name: String?): CircleStyle {
            return when (name?.uppercase()) {
                "PURE_WHITE" -> PURE_WHITE
                "SMOKED_GLASS", "OBSIDIAN_GLASS" -> SMOKED_GLASS
                else -> LIQUID_GLASS
            }
        }
    }
}
