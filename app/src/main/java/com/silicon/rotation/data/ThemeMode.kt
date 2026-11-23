package com.silicon.rotation.data

enum class ThemeMode(val id: String, val label: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark"),
    PURE_DARK("pure_dark", "Pure dark");

    companion object {
        fun fromId(id: String?): ThemeMode {
            return entries.firstOrNull { it.id == id } ?: SYSTEM
        }
    }
}
