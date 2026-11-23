package com.silicon.rotation

import android.app.Application
import com.silicon.rotation.service.RotationController
import com.silicon.rotation.service.SiliconNotificationManager

class SiliconApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RotationController.initialize(this)
        SiliconNotificationManager.ensureChannel(this)
    }
}
