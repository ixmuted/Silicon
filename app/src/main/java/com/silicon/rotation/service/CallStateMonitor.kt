package com.silicon.rotation.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class CallStateMonitor(
    private val context: Context,
    private val onCallStateChanged: (Boolean) -> Unit
) {
    private val telephonyManager = context.getSystemService(TelephonyManager::class.java)
    private var registered = false
    private var modernCallback: Any? = null
    @Suppress("DEPRECATION")
    private var legacyListener: PhoneStateListener? = null

    fun start() {
        if (registered || telephonyManager == null) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val callback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    val inCall = state == TelephonyManager.CALL_STATE_RINGING ||
                        state == TelephonyManager.CALL_STATE_OFFHOOK
                    onCallStateChanged(inCall)
                }
            }
            telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
            modernCallback = callback
        } else {
            @Suppress("DEPRECATION")
            val listener = object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    val inCall = state == TelephonyManager.CALL_STATE_RINGING ||
                        state == TelephonyManager.CALL_STATE_OFFHOOK
                    onCallStateChanged(inCall)
                }
            }
            @Suppress("DEPRECATION")
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
            legacyListener = listener
        }
        registered = true
    }

    fun stop() {
        if (!registered || telephonyManager == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val callback = modernCallback as? TelephonyCallback
            if (callback != null) {
                telephonyManager.unregisterTelephonyCallback(callback)
            }
            modernCallback = null
        } else {
            @Suppress("DEPRECATION")
            legacyListener?.let {
                telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE)
            }
            legacyListener = null
        }
        registered = false
    }
}
