package com.adretsoftwares.cellsecuritycare.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class SendSms {
    public void send(Context context, SharedPreferences preferences) {
        FusedLocationProviderClient fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("location", location.toString());
                            String message = "EMERGENCY!!! This is an emergency message from cell security application" + "!. \n\nTheir location is: http://maps.google.com?q=" + location.getLatitude() + "," + location.getLongitude();
                            sendMessage(preferences, message);
                        } else {
                            Log.d("location", "Can't access your location");
                        }
                    }
                });
    }

    private void sendMessage(SharedPreferences preferences, String message) {
        String[] numbers = {
                preferences.getString("emNu1", ""),
                preferences.getString("emNu2", ""),
                preferences.getString("emNu3", ""),
                preferences.getString("emNu4", ""),
                preferences.getString("emNu5", "")};
        SmsManager smsManager = SmsManager.getDefault();
        for (int i = 0; i < numbers.length; i++) {
            if (!numbers[i].equals("")) {
                try {
                    smsManager.sendTextMessage(numbers[i], null, message, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("SMS", "Error sending SMS: " + e.getMessage());
                }
            } else {
                Log.d("destination", numbers[i]);
            }
        }
    }
}
