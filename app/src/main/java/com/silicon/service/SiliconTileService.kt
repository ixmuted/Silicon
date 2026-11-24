package com.silicon.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.silicon.MainActivity
import com.silicon.R
import com.silicon.util.SystemIntegration

class SiliconTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        RotationController.initialize(this)
        renderTile()
    }

    override fun onClick() {
        super.onClick()
        RotationController.initialize(this)
        if (!SystemIntegration.isAccessibilityEnabled(this)) {
            openAppForSetup()
            return
        }
        RotationController.toggle(this)
        renderTile()
    }

    private fun renderTile() {
        val tile = qsTile ?: return
        val enabled = SystemIntegration.isAccessibilityEnabled(this)
        val state = RotationController.state.value
        tile.icon = Icon.createWithResource(this, R.drawable.ic_silicon_tile)
        tile.label = getString(R.string.app_name)
        if (!enabled) {
            tile.state = Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "Setup required"
            }
        } else if (state.isActive) {
            tile.state = Tile.STATE_ACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = state.orientation.shortLabel
            }
        } else {
            tile.state = Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "Off"
            }
        }
        tile.updateTile()
    }

    private fun openAppForSetup() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_OPEN_SETUP, true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
