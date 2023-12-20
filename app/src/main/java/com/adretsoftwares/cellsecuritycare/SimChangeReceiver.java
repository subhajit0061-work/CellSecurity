package com.adretsoftwares.cellsecuritycare;

import static android.content.Context.MODE_PRIVATE;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adretsoftwares.cellsecuritycare.common.SendSms;
import com.adretsoftwares.cellsecuritycare.common.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class SimChangeReceiver extends BroadcastReceiver {
    SharedPreferences preferences;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
//        Log.d("SimChangedReceiver", "--> SIM state changed <--");
//
//
//        SubscriptionManager sm = SubscriptionManager.from(context);

//        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        List<SubscriptionInfo> sis = sm.getActiveSubscriptionInfoList();
//
//        SubscriptionInfo si = sis.get(0);
//        SubscriptionInfo si1 = sis.get(1);
//
//
//        String iccId = si.getIccId();
//        String iccId1 = si1.getIccId();

        if (preferences.contains("simchange")) {
            if (preferences.getBoolean("simchange", false)) {
                showNotification(context);
                send(context, preferences);
            }
        }

//        Log.e("iccId---------", iccId);
//        Log.e("iccId1---------", iccId1);

    }

    private void showNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.SECURITY_CHANNEL_ID)
                .setSmallIcon(R.drawable.notifi)
                .setContentTitle("Sim card changed!")
                .setContentText("Your sim card has been changed")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        context.getSystemService(NotificationManager.class).notify(Constants.SIM_CHANGE_NOTIFICATION_ID, builder.build());
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

    private void showAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the message show for the Alert time
        builder.setMessage("SIM card has been changed?");

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            dialog.cancel();
        });


        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

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
                            String message = "EMERGENCY!!! This is an emergency message from cell security application for user" + "!. \n\nTheir location is: http://maps.google.com?q=" + location.getLatitude() + "," + location.getLongitude();
                            sendMessage(preferences, message);
                        } else {
                            Log.d("location", "Can't access your location");
                        }
                    }
                });
    }
}
