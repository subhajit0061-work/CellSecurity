package com.adretsoftwares.cellsecuritycare;

import static com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class    LocationTrackingService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    SharedPreferences preferences;
    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    // Handle the received location data here
                    LoginUpdate(preferences.getString("mobile_number",""),preferences.getString("name","")
                            ,preferences.getString("emailId",""),String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setFastestInterval(5000); // Fastest update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void LoginUpdate(final String mobile_number,final String name,final String emailId,
                             final String lat,final String longitude) {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //converting response to json object
                    Log.e("Error ",s);
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
//                    if (obj.getBoolean("error")) {
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                    } else {
////                        JSONObject user = obj.getJSONObject("user");
////                        SharedPreferences.Editor editor = preferences.edit();
////                        editor.putString("emailId",emailId);
////                        editor.putString("name", name);
////                        editor.putString("emergencyMobile1",emergencyMobile1);
////                        editor.putString("emergencyMobile2", emergencyMobile2);
////                        editor.putString("emergencyMobile3", emergencyMobile3);
////                        editor.putString("mobile_number", mobile_number);
////                        editor.apply();
//
//                       // Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
////                        Intent intent = new Intent(MobileTracking.this, MainActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        startActivity(intent);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                Log.e("mobile_number",mobile_number);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_number", mobile_number);
                params.put("name", name);
                params.put("emailId", emailId);
                params.put("lat", lat);
                params.put("long", longitude);
                // params.put("token", token);

                //returing the response
                return requestHandler.sendPostRequest("https://cellsecuritycare.com/api/index2.php?apicall=updatetrack", params);
            }
        }
        Login ul = new Login();
        ul.execute();
    }
}
