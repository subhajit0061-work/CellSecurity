package com.adretsoftwares.cellsecuritycare;

import static android.content.Context.MODE_PRIVATE;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.adretsoftwares.cellsecuritycare.common.SendSms;
import com.adretsoftwares.cellsecuritycare.common.Constants;

public class BatteryReceiver extends BroadcastReceiver {

    SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        if (preferences.getBoolean("battery", false)) {
            showNotification(context);
            new SendSms().send(context, preferences);
        }
    }

    private void sendMessage(Context context, SharedPreferences preferences) {
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
                    smsManager.sendTextMessage(numbers[i], null, "Hello", null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("SMS", "Error sending SMS: " + e.getMessage());
                }
            } else {
                Log.d("destination", numbers[i]);
            }
        }
    }

    private void showNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.SECURITY_CHANNEL_ID)
                .setSmallIcon(R.drawable.notifi)
                .setContentTitle("Low battery!")
                .setContentText("Your battery level is low. Please plugin to charge")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.LOW_BATTERY_NOTIFICATION_ID, builder.build());
    }

    private void showAlertDialog(Context context, SharedPreferences preferences, Intent intent) {
        int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;

        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        if (level == 20) {
            if (preferences.getBoolean("battery", false)) {
                // Create the object of AlertDialog Builder class
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Set the message show for the Alert time
                builder.setMessage("Do you want to exit ?");

                // Set Alert Title
                builder.setTitle("Alert !");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // When the user click yes button then app will close

                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        }
    }

}
