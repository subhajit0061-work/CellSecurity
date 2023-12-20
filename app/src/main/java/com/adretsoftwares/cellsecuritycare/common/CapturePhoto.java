package com.adretsoftwares.cellsecuritycare.common;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.adretsoftwares.cellsecuritycare.CameraService;

public class CapturePhoto {
    public void click(Context ctxt, String message) {
        SharedPreferences preferences = ctxt.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Intent front_translucent = new Intent(ctxt
                .getApplicationContext(), CameraService.class);
        front_translucent.putExtra("Front_Request", true);
        front_translucent.putExtra("message", message);
        front_translucent.putExtra("mobile", preferences.getString("mobile_number", ""));
        front_translucent.putExtra("Quality_Mode",
                20);
        ctxt.getApplicationContext().startService(
                front_translucent);
    }
}
