package com.adretsoftwares.cellsecuritycare;

import static android.content.Context.MODE_PRIVATE;
import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

public class RebootReceiver extends BroadcastReceiver {
    SharedPreferences preferences;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_REBOOT)) {
            // Display a password entry dialog
            final EditText passwordInput = new EditText(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enter Password");
            builder.setView(passwordInput);
            preferences =context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            if(preferences.getBoolean("poweroff",false)) {
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Verify the entered password (you should implement your own logic here)
                        String enteredPassword = passwordInput.getText().toString();
                        if (enteredPassword.equals(preferences.getString("poweroffpass","@#"))) {
                            // Password is correct, allow the reboot
                            rebootDevice();
                        } else {
                            // Password is incorrect, prevent the reboot
                            Toast.makeText(context, "Incorrect password. Reboot denied.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled, prevent the reboot
                        Toast.makeText(context, "Reboot canceled.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        }
    }

    private void rebootDevice() {
        try {
            // Reboot the device programmatically (requires appropriate permissions)
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

