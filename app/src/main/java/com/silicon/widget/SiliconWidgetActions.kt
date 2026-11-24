package com.silicon.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.silicon.MainActivity
import com.silicon.data.OrientationMode
import com.silicon.service.RotationController
import com.silicon.util.SystemIntegration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val OrientationParamKey = ActionParameters.Key<String>("orientation_id")

class SelectOrientationAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val handled = withContext(Dispatchers.Main.immediate) {
            RotationController.initialize(context)
            if (!SystemIntegration.isAccessibilityEnabled(context)) {
                launchSetup(context)
                return@withContext false
            }
            val mode = OrientationMode.fromId(parameters[OrientationParamKey])
            val current = RotationController.state.value
            if (current.isActive && current.orientation == mode) {
                RotationController.disable(context)
            } else {
                RotationController.setOrientation(context, mode, activate = true)
            }
            true
        }
        if (!handled) return
        runCatching { SiliconBarWidget().updateAll(context) }
        runCatching { SiliconSquareWidget().updateAll(context) }
    }
}

private fun launchSetup(context: Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(MainActivity.EXTRA_OPEN_SETUP, true)
    }
    context.startActivity(intent)
}
