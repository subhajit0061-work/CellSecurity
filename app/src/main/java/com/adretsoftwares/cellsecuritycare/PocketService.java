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
public class PocketService extends Service implements SensorEventListener {
    private SensorManager sensorMan;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Notification _notification;
    public static boolean flag = false;
    private static final int SENSOR_SENSITIVITY = 4;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


//        startInForeground();
        if (Build.VERSION.SDK_INT >= 26) {
            String NOTIFICATION_CHANNEL_ID = "example.permanence";
            String channelName = "Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            _notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();


        }
        if (Build.VERSION.SDK_INT >= 26) {

            startForeground(10101, _notification);
        }
        // Toast.makeText(this, "service Start successfully", Toast.LENGTH_SHORT).show();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//                            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//                            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        //        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //        sensorMan.registerListener(this, accelerometer,
        //                SensorManager.SENSOR_DELAY_UI);
        if (accelerometer != null) {
            mSensorManager.registerListener(PocketService.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(PocketService.this, "Accelerometer not available on this device.", Toast.LENGTH_LONG).show();
            stopSelf();
            // Log.e("MotionActivity", "Accelerometer not available on this device.");
        }
//                                                mSensorManager.registerListener(PocketThiefActivity.this, mSensor,
//                                                        SensorManager.SENSOR_DELAY_NORMAL);
        //                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                startForegroundService(new Intent(PocketThiefActivity.this, PocketService.class));
//                            } else {
//                                startService(new Intent(PocketThiefActivity.this, PocketService.class));
//                            }
        Toast.makeText(PocketService.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !flag) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate the magnitude of acceleration
            double acceleration = Math.sqrt(x * x + y * y + z * z);

            // You can set a threshold for motion detection
            if (acceleration > 10) {
                Log.d("MotionActivity", "Motion detected!");
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //To play audio on clap
                //playAudio();
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
        sensorMan.unregisterListener(this);
        mSensorManager.unregisterListener(this);
    }

}