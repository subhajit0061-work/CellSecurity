package com.adretsoftwares.cellsecuritycare.services

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.IBinder
import android.util.Log
import com.adretsoftwares.cellsecuritycare.PinLockActivity
import java.util.Timer
import java.util.TimerTask

class AppMonitoringService : Service() {

    private var lockedAppsList: List<String> = listOf("com.oplus.camera")

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        loadLockedApps()
        startAppCheck()
    }

    private fun loadLockedApps() {

    }

    private fun startAppCheck() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val packageName = activityManager.getRunningTasks(1)[0].topActivity?.packageName
                Log.d("app", "$packageName")
                if (lockedAppsList.contains(packageName)) {
                    Intent(this@AppMonitoringService, PinLockActivity::class.java)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK)
                        .also {
                            startActivity(it)
                        }
                }
                Log.d("app", "checking for app")
            }
        }, 0, 1000)
    }
}