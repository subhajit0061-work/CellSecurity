package com.adretsoftwares.cellsecuritycare.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME
import com.adretsoftwares.cellsecuritycare.common.SendSms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

const val NO_OF_REPEATS = 10
const val DELAY = 120000L

class MobileTrackingService : Service() {

    private lateinit var job: Job

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job = CoroutineScope(Dispatchers.Default).launch {
            repeat(NO_OF_REPEATS) {
                if (isActive) {
                    SendSms().send(
                        this@MobileTrackingService,
                        getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
                    )
                }
                delay(DELAY)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}