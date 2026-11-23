package com.silicon.rotation.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object SiliconWidgetUpdater {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun updateAll(context: Context) {
        val appContext = context.applicationContext
        scope.launch {
            runCatching { SiliconBarWidget().updateAll(appContext) }
            runCatching { SiliconSquareWidget().updateAll(appContext) }
        }
    }
}
