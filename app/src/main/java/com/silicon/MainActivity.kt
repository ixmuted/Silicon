package com.silicon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.silicon.service.RotationController
import com.silicon.ui.SiliconNavApp

class MainActivity : ComponentActivity() {

    private var openSetupSignal by mutableIntStateOf(0)

    @Volatile
    private var holdSplash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            holdSplash = true
            splash.setKeepOnScreenCondition { holdSplash }
            Handler(Looper.getMainLooper()).postDelayed({ holdSplash = false }, SPLASH_DURATION_MS)
        }

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
        private const val SPLASH_DURATION_MS = 1500L
    }
}
