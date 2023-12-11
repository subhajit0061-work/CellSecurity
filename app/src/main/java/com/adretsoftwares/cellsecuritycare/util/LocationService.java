package com.adretsoftwares.cellsecuritycare.util;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.adretsoftwares.cellsecuritycare.R;
import com.adretsoftwares.cellsecuritycare.daos.DateDao;
import com.adretsoftwares.cellsecuritycare.database.CellSecurityDatabase;
import com.adretsoftwares.cellsecuritycare.entities.DateEntity;
import com.adretsoftwares.cellsecuritycare.model.LocationEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.Executors;


public class LocationService extends Service {

    public static String lattitude;
    public static String longitude;
    private static final String CHANNEL_ID = "12345";
    private static final int NOTIFICATION_ID = 12345;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private NotificationManager notificationManager;
    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult);
            }
        };


    }


    @SuppressLint("MissingPermission")
    private void createLocationRequest() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void dateTimeDbInsertion(Context context){
//        DateEntity entity = new DateEntity();
//
//        entity.setYourDate(new Date(System.currentTimeMillis())); // Set the date
//        entity.setYourTime(new Time(System.currentTimeMillis())); // Set the time
//        CellSecurityDatabase cellSecurityDatabase = CellSecurityDatabase.getInstance(context.getApplicationContext());
//        DateDao dateDao = cellSecurityDatabase.dateDao();
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                // Perform your insert operation here
//                dateDao.insert(entity);
//            }
//        });
//
//    }

    private void removeLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        stopForeground(true);
        stopSelf();
    }

    private void onNewLocation(LocationResult locationResult) {
        location = locationResult.getLastLocation();
//        EventBus.getDefault().post(new LocationEvent(location != null ? location.getLatitude() : null,
//                location != null ? location.getLongitude() : null));
        if (location!=null){
            lattitude= String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }

        Log.d("theloc", "the location is " + (location != null ? location.getLatitude() : null)
                + " and " + (location != null ? location.getLongitude() : null));
        // startForeground(NOTIFICATION_ID, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createLocationRequest();
        //dateTimeDbInsertion(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "locations", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        startForeground(NOTIFICATION_ID, getNotification());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    public Notification getNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Updates")
                .setContentText("Tracking Location")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
        }
        return notificationBuilder.build();
    }
}


