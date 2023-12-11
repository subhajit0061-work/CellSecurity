package com.adretsoftwares.cellsecuritycare.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.adretsoftwares.cellsecuritycare.common.Constants;
import com.adretsoftwares.cellsecuritycare.util.UtilityAndConstant;

public class CellSecurity extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        new UtilityAndConstant().init(this);
        createNotificationChannel();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("applhggu", "apllocation closed");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String SECURITY_CHANNEL_NAME = "Security channel";
            final String LOW_BATTERY_DESCRIPTION = "Notifies in case of emergencies";
            NotificationChannel lowBatteryChannel = new NotificationChannel(
                    Constants.SECURITY_CHANNEL_ID,
                    SECURITY_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            lowBatteryChannel.setDescription(LOW_BATTERY_DESCRIPTION);

            NotificationChannel foregroundChannel = new NotificationChannel(
                    Constants.FOREGROUND_CHANNEL_ID,
                    "Foreground service",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(lowBatteryChannel);
            notificationManager.createNotificationChannel(foregroundChannel);
        }
    }
}
