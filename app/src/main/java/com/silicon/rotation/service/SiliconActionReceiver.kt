package com.silicon.rotation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SiliconActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TURN_OFF -> RotationController.disable(context)
            ACTION_SWITCH_ORIENTATION -> {
                val next = RotationController.state.value.orientation.toggledOpposite()
                RotationController.setOrientation(context, next, activate = true)
            }
            ACTION_TOGGLE -> RotationController.toggle(context)
        }
    }

    companion object {
        const val ACTION_TURN_OFF = "com.silicon.rotation.ACTION_TURN_OFF"
        const val ACTION_SWITCH_ORIENTATION = "com.silicon.rotation.ACTION_SWITCH_ORIENTATION"
        const val ACTION_TOGGLE = "com.silicon.rotation.ACTION_TOGGLE"
    }
}
