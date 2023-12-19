package com.adretsoftwares.cellsecuritycare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.adretsoftwares.cellsecuritycare.common.Constants;

public class PocketService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    Notification _notification;
    public static boolean flag = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flag = false;

        if (Build.VERSION.SDK_INT >= 26) {
            String NOTIFICATION_CHANNEL_ID = "example.permanence";
            String channelName = "Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.FOREGROUND_CHANNEL_ID);
            _notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.notifi)
                    .setContentTitle("App is running in background")
                    .setContentText("Detecting motion")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();


        }
        if (Build.VERSION.SDK_INT >= 26) {

            startForeground(10101, _notification);
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorManager.registerListener(PocketService.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(PocketService.this, "Accelerometer not available on this device.", Toast.LENGTH_LONG).show();
            stopSelf();
        }

        Toast.makeText(PocketService.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !flag) {
            Log.d("alarm", "all good");
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z);

            if (acceleration > 10) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                flag = true;
                ComponentName cn = new ComponentName(this, EnterPin.class);
                intent.setComponent(cn);
                getApplicationContext().startActivity(intent);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        flag = false;
        mSensorManager = null;
    }

}