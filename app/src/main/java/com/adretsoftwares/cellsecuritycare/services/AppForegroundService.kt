package com.adretsoftwares.cellsecuritycare.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.adretsoftwares.cellsecuritycare.R
import com.adretsoftwares.cellsecuritycare.common.Constants

class AppForegroundService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this, Constants.FOREGROUND_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifi)
            .setContentTitle("CellSecurityCare")
            .setContentText("Please let the app run in foreground to work properly")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, notification)

        return START_STICKY
    }
}