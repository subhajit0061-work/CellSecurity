package com.adretsoftwares.cellsecuritycare.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.adretsoftwares.cellsecuritycare.R
import com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME
import com.adretsoftwares.cellsecuritycare.common.Constants
import com.google.android.gms.location.LocationServices

class SmsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        showNotification()
        sendSms()
        return START_STICKY
    }

    private fun sendSms() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener {
                val message =
                    "EMERGENCY!!! This is an emergency message from cell security application" + "!. \n\nTheir location is: http://maps.google.com?q=" + it.latitude + "," + it.longitude

                sendMessage(getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE), message)
                stopSelf()
            }
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, Constants.FOREGROUND_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifi)
            .setContentTitle("Alert!")
            .setContentText("Fetching location")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(Constants.FOREGROUND_SMS_SERVICE_ID, notification)
    }

    private fun sendMessage(preferences: SharedPreferences, message: String) {
        val numbers = arrayOf(
            preferences.getString("emNu1", ""),
            preferences.getString("emNu2", ""),
            preferences.getString("emNu3", ""),
            preferences.getString("emNu4", ""),
            preferences.getString("emNu5", "")
        )
        val smsManager = SmsManager.getDefault()
        for (i in numbers.indices) {
            if (numbers[i] != "") {
                try {
                    smsManager.sendTextMessage(numbers[i], null, message, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("SMS", "Error sending SMS: " + e.message)
                }
            } else {
                Log.d("destination", numbers[i]!!)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null
}