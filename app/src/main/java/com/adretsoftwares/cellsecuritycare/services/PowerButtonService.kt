package com.adretsoftwares.cellsecuritycare.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi

class PowerButtonService : Service() {

    private var powerButtonCount = 0
    private lateinit var screenOnReceiver: BroadcastReceiver
    private var previousClick = 0L

    override fun onCreate() {
        super.onCreate()
        screenOnReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onReceive(context: Context?, intent: Intent?) {
                val currentClick = System.currentTimeMillis()
                if (currentClick > previousClick + 4000) {
//                    if (powerButtonCount == 0) powerButtonCount++
//                    else powerButtonCount = 0
                    powerButtonCount = 1
                    previousClick = currentClick
                } else {
                    powerButtonCount++
                }
                Log.d("res", powerButtonCount.toString())
                if (powerButtonCount == 3) {
                    startSmsService()
                    powerButtonCount = 0
                }
            }
        }
        registerReceiver(screenOnReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF).apply {
            addAction(Intent.ACTION_SCREEN_ON)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startSmsService() {
        startForegroundService(Intent(this, SmsService::class.java))
    }

    override fun onBind(p0: Intent?): IBinder? = null
}