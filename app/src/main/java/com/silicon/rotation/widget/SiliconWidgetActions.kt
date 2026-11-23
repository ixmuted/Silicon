package com.silicon.rotation.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.silicon.rotation.MainActivity
import com.silicon.rotation.data.OrientationMode
import com.silicon.rotation.service.RotationController
import com.silicon.rotation.util.SystemIntegration

val OrientationParamKey = ActionParameters.Key<String>("orientation_id")

class ToggleRotationAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        RotationController.initialize(context)
        if (!SystemIntegration.isAccessibilityEnabled(context)) {
            launchSetup(context)
            return
        }
        RotationController.toggle(context)
    }
}

class SetOrientationAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        RotationController.initialize(context)
        if (!SystemIntegration.isAccessibilityEnabled(context)) {
            launchSetup(context)
            return
        }
        val mode = OrientationMode.fromId(parameters[OrientationParamKey])
        RotationController.setOrientation(context, mode, activate = true)
    }
}

class CycleOrientationAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        RotationController.initialize(context)
        if (!SystemIntegration.isAccessibilityEnabled(context)) {
            launchSetup(context)
            return
        }
        RotationController.cycleOrientation(context)
    }
}

private fun launchSetup(context: Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(MainActivity.EXTRA_OPEN_SETUP, true)
    }
    context.startActivity(intent)
}
