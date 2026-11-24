package com.silicon

import android.app.Application
import com.silicon.service.RotationController
import com.silicon.service.SiliconNotificationManager

class SiliconApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RotationController.initialize(this)
        SiliconNotificationManager.ensureChannel(this)
    }
}
