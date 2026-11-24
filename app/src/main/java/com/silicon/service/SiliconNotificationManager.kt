package com.silicon.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.silicon.MainActivity
import com.silicon.R
import com.silicon.data.OrientationMode

object SiliconNotificationManager {

    private const val CHANNEL_ID = "silicon_active_channel"
    private const val NOTIFICATION_ID = 1042

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            setShowBadge(false)
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
        }
        manager.createNotificationChannel(channel)
    }

    fun startForeground(service: Service, orientation: OrientationMode) {
        ensureChannel(service)
        val notification = buildNotification(service, orientation)
        val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }
        runCatching {
            ServiceCompat.startForeground(service, NOTIFICATION_ID, notification, fgsType)
        }.onFailure {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(service, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(service).notify(NOTIFICATION_ID, notification)
            }
        }
    }

    fun stopForeground(service: Service) {
        runCatching {
            ServiceCompat.stopForeground(service, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
        cancel(service)
    }

    fun update(context: Context, isActive: Boolean, orientation: OrientationMode, enabled: Boolean) {
        if (!isActive || !enabled) {
            cancel(context)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ensureChannel(context)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, buildNotification(context, orientation))
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    private fun buildNotification(context: Context, orientation: OrientationMode): Notification {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPending = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val switchIntent = Intent(context, SiliconActionReceiver::class.java).apply {
            action = SiliconActionReceiver.ACTION_SWITCH_ORIENTATION
        }
        val switchPending = PendingIntent.getBroadcast(
            context,
            1,
            switchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val turnOffIntent = Intent(context, SiliconActionReceiver::class.java).apply {
            action = SiliconActionReceiver.ACTION_TURN_OFF
        }
        val turnOffPending = PendingIntent.getBroadcast(
            context,
            2,
            turnOffIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val opposite = orientation.toggledOpposite()
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_silicon_tile)
            .setColor(0xFFCC785C.toInt())
            .setContentTitle("${orientation.label} locked")
            .setContentText("Orientation is locked across apps")
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(openPending)
            .addAction(0, opposite.shortLabel, switchPending)
            .addAction(0, "Turn off", turnOffPending)
            .build()
    }
}
