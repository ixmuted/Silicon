package com.silicon.rotation.data

enum class ThemeMode(val id: String, val label: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromId(id: String?): ThemeMode {
            return when (id) {
                "light" -> LIGHT
                "dark", "pure_dark" -> DARK
                else -> SYSTEM
            }
        }
    }
}
