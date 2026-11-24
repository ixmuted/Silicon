package com.silicon.data

import android.content.pm.ActivityInfo

enum class OrientationMode(
    val id: String,
    val label: String,
    val shortLabel: String,
    val activityInfoValue: Int,
    val iconDegrees: Float
) {
    PORTRAIT(
        id = "portrait",
        label = "Portrait",
        shortLabel = "Portrait",
        activityInfoValue = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
        iconDegrees = 0f
    ),
    LANDSCAPE(
        id = "landscape",
        label = "Landscape",
        shortLabel = "Landscape",
        activityInfoValue = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
        iconDegrees = 90f
    ),
    REVERSE_PORTRAIT(
        id = "reverse_portrait",
        label = "Reverse portrait",
        shortLabel = "Rev. portrait",
        activityInfoValue = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
        iconDegrees = 180f
    ),
    REVERSE_LANDSCAPE(
        id = "reverse_landscape",
        label = "Reverse landscape",
        shortLabel = "Rev. landscape",
        activityInfoValue = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
        iconDegrees = 270f
    );

    fun toggledOpposite(): OrientationMode {
        return when (this) {
            PORTRAIT, REVERSE_PORTRAIT -> LANDSCAPE
            LANDSCAPE, REVERSE_LANDSCAPE -> PORTRAIT
        }
    }

    fun next(): OrientationMode {
        val values = entries
        return values[(ordinal + 1) % values.size]
    }

    companion object {
        fun fromId(id: String?): OrientationMode {
            return entries.firstOrNull { it.id == id } ?: LANDSCAPE
        }
    }
}
