package com.silicon.rotation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.silicon.rotation.service.RotationController
import com.silicon.rotation.ui.SiliconNavApp

class MainActivity : ComponentActivity() {

    private var openSetupSignal by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RotationController.initialize(this)
        val openSetup = intent?.getBooleanExtra(EXTRA_OPEN_SETUP, false) == true
        setContent {
            SiliconNavApp(
                openSetupInitially = openSetup,
                openSetupSignal = openSetupSignal
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_OPEN_SETUP, false)) {
            openSetupSignal++
        }
    }

    companion object {
        const val EXTRA_OPEN_SETUP = "extra_open_setup"
    }
}
