package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MobileTracking extends AppCompatActivity {
    TextView textView2,textActive;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    SwitchMaterial num1_switch;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    ProgressDialog progressBar;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_tracking);
        progressBar = new ProgressDialog(this);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        num1_switch = findViewById(R.id.num1_switch);
        textActive = findViewById(R.id.textView2);
        if(preferences.contains("mobileTracker")&&preferences.getBoolean("mobileTracker",false)){
            num1_switch.setChecked(true);
            textActive.setTextColor(Color.parseColor("#4CAF50"));
            textActive.setText("Activated");
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        num1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    textActive.setTextColor(Color.parseColor("#4CAF50"));
                    textActive.setText("Activated");
                    editor.putBoolean("mobileTracker",true).commit();
                    startLocationTrackingService();
                }else {
                    textActive.setTextColor(Color.parseColor("#000000"));
                    textActive.setText("Activate");
                    editor.putBoolean("mobileTracker",false).commit();
                    stopLocationTrackingService();
                }
            }
        });
    }
    private void startLocationTrackingService() {
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        startService(serviceIntent);
    }

    private void stopLocationTrackingService() {
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}